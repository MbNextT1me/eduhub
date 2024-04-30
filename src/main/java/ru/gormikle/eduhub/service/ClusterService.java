package ru.gormikle.eduhub.service;

import com.jcraft.jsch.*;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gormikle.eduhub.dto.ClusterDto;
import ru.gormikle.eduhub.entity.Cluster;
import ru.gormikle.eduhub.entity.File;
import ru.gormikle.eduhub.entity.FileCategory;
import ru.gormikle.eduhub.entity.Task;
import ru.gormikle.eduhub.mapper.ClusterMapper;
import ru.gormikle.eduhub.repository.ClusterRepository;
import ru.gormikle.eduhub.repository.FileRepository;
import ru.gormikle.eduhub.repository.TaskRepository;
import ru.gormikle.eduhub.service.basic.BaseMappedService;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@Slf4j
public class ClusterService extends BaseMappedService<Cluster, ClusterDto,String,ClusterRepository, ClusterMapper> {
    @Value("${file.path}")
    private String fileStoragePath;

    public ClusterService(ClusterRepository repository, ClusterMapper mapper, PasswordEncoder passwordEncoder, FileRepository fileRepository, TaskRepository taskRepository, TaskService taskService){
        super(repository,mapper);
        this.passwordEncoder = passwordEncoder;
        this.fileRepository = fileRepository;
        this.taskRepository = taskRepository;
        this.taskService = taskService;
    }

    private final PasswordEncoder passwordEncoder;
    private final FileRepository fileRepository;
    private final TaskRepository taskRepository;
    private final TaskService taskService;
    public List<ClusterDto> getAllClusters() {
        return getAllAsDto();
    }

    public ClusterDto getClusterById(String id) {
        Cluster cluster = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cluster not found with id: " + id));
        return toDto(cluster);
    }

    public ClusterDto saveCluster(ClusterDto clusterDto) {
        clusterDto.setHostUserPassword(passwordEncoder.encode(clusterDto.getHostUserPassword()));
        return create(clusterDto);
    }

    public void deleteCluster(String id) {
        repository.deleteById(id);
    }

    //Сейчас лог файлы перезаписываются, так делать не надо.
    public String executeRemoteCode(String fileId, String taskId, String clusterId) {
        try {
            File file = fileRepository.findById(fileId)
                    .orElseThrow(() -> new IllegalArgumentException("File not found with id: " + fileId));
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
            Cluster cluster = repository.findById(clusterId)
                    .orElseThrow(() -> new IllegalArgumentException("Cluster not found with id: " + clusterId));

            JSch js = new JSch();
            Session session = js.getSession(cluster.getHostUserName(), cluster.getHostName(), Integer.parseInt(cluster.getPort()));
            session.setPassword(cluster.getHostUserPassword());
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            // Отправка файла
            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            FileInputStream fileInputStream = new FileInputStream(fileStoragePath+file.getName());
            sftpChannel.put(fileInputStream, file.getName());
            fileInputStream.close();

            // Выполнение компиляции
            ChannelExec execChannel = (ChannelExec) session.openChannel("exec");
            execChannel.setCommand("/usr/local/cuda/bin/nvcc " + file.getName() + " -o " + file.getName().substring(0, file.getName().lastIndexOf('.')));
            execChannel.connect();
            while (!execChannel.isClosed()) {
                TimeUnit.SECONDS.sleep(1);
            }

            // Проверяем статус завершения
            if (execChannel.getExitStatus() != 0) {
                // Обработка ошибок при выполнении команды
            }

            execChannel.disconnect(); // Отключаем execChannel после компиляции

            // Выполнение скомпилированного файла
            execChannel = (ChannelExec) session.openChannel("exec");
            execChannel.setCommand("./" + file.getName().substring(0, file.getName().lastIndexOf('.')));
            execChannel.connect();
            InputStream inputStream = execChannel.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder executionLog = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                executionLog.append(line).append("\n");
            }
            int exitStatus = execChannel.getExitStatus();
            execChannel.disconnect();


            // Запись результата в файл с категорией CLUSTER_LOG
            String logFileName = "compilation_log"+fileId+".txt";
            File logFile = new File();
            logFile.setName(logFileName);
            logFile.setCategory(FileCategory.valueOf("CLUSTER_LOG"));
            File savedlogfile = fileRepository.save(logFile);
            taskService.addFileToTask(taskId,savedlogfile.getId());

            try (FileWriter writer = new FileWriter(fileStoragePath + logFileName)) {
                writer.write(executionLog.toString());
            } catch (IOException e) {
                return "Error: " + e.getMessage();
            }
            session.disconnect();

            return "Success. Execution log saved in file: " + logFileName;
        } catch (JSchException | IOException e) {
            return "Error: " + e.getMessage();
        } catch (SftpException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
