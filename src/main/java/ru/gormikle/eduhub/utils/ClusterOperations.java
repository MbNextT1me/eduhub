package ru.gormikle.eduhub.utils;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.gormikle.eduhub.entity.Cluster;
import ru.gormikle.eduhub.entity.File;
import ru.gormikle.eduhub.entity.FileCategory;
import ru.gormikle.eduhub.entity.Task;
import ru.gormikle.eduhub.repository.FileRepository;
import ru.gormikle.eduhub.service.TaskService;

import java.io.*;
import java.util.Properties;
import java.util.UUID;

@Component
@Slf4j
public class ClusterOperations {
    @Value("${file.path}")
    private String fileStoragePath;

    private final TaskService taskService;
    private final FileRepository fileRepository;

    public ClusterOperations(TaskService taskService, FileRepository fileRepository) {
        this.taskService = taskService;
        this.fileRepository = fileRepository;
    }

    public Session connectToCluster(Cluster cluster) throws JSchException {
        JSch js = new JSch();
        Session session = js.getSession(cluster.getHostUserName(), cluster.getHostName(), Integer.parseInt(cluster.getPort()));
        session.setPassword(cluster.getHostUserPassword());
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        return session;
    }

    public void sendFileToCluster(Session session, File file) throws JSchException, IOException, SftpException {
        ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
        sftpChannel.connect();
        FileInputStream fileInputStream = new FileInputStream(fileStoragePath + file.getName());
        sftpChannel.put(fileInputStream, file.getName());
        fileInputStream.close();
        sftpChannel.disconnect();
    }

    public void compileAndExecuteFile(Session session, File file, Task task) throws JSchException, IOException {
        ChannelExec execChannel = (ChannelExec) session.openChannel("exec");
        String compileCommand = "/usr/local/cuda/bin/nvcc " + file.getName() + " -o " + file.getName().substring(0, file.getName().lastIndexOf('.'));
        String executeCommand = "./" + file.getName().substring(0, file.getName().lastIndexOf('.'));

        execChannel.setCommand(compileCommand + " && " + executeCommand);
        execChannel.connect();

        InputStream inputStream = execChannel.getInputStream();
        InputStream errorStream = execChannel.getErrStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));

        StringBuilder executionLog = new StringBuilder();
        StringBuilder errorLog = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            executionLog.append(line).append("\n");
        }

        while ((line = errorReader.readLine()) != null) {
            errorLog.append(line).append("\n");
        }

        int exitStatus = execChannel.getExitStatus();
        execChannel.disconnect();

        String logFileName = "compilation_log" + UUID.randomUUID().toString() + ".txt";
        //String logFileName = "compilation_log" + file.getId() + ".txt";
        File logFile = new File();
        logFile.setName(logFileName);
        logFile.setCategory(FileCategory.valueOf("CLUSTER_LOG"));
        File savedLogFile = fileRepository.save(logFile);
        taskService.addFileToTask(task.getId(), savedLogFile.getId());

        try (FileWriter writer = new FileWriter(fileStoragePath + logFileName)) {
            writer.write(executionLog.toString());
            if (!errorLog.isEmpty()) {
                writer.write("Errors:\n");
                writer.write(errorLog.toString());
            }
        } catch (IOException e) {
            log.error("Error writing execution log: {}", e.getMessage());
        }
    }

}
