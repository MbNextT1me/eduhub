package ru.gormikle.eduhub.utils;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.gormikle.eduhub.entity.Cluster;
import ru.gormikle.eduhub.entity.File;
import ru.gormikle.eduhub.entity.FileCategory;
import ru.gormikle.eduhub.repository.FileRepository;
import ru.gormikle.eduhub.service.FileService;
import ru.gormikle.eduhub.service.TaskService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Component
public class ClusterOperations {
    @Value("${file.path}")
    private String fileStoragePath;

    private final TaskService taskService;
    private final FileRepository fileRepository;
    private final FileService fileService;

    public ClusterOperations(TaskService taskService, FileRepository fileRepository, FileService fileService) {
        this.taskService = taskService;
        this.fileRepository = fileRepository;
        this.fileService = fileService;
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


    public void sendFileToCluster(Session session, File file, String taskId,String username) throws JSchException, IOException, SftpException {
        ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
        sftpChannel.connect();

        String remoteRootDir = "./eduhub";
        String taskDir = "./task_" + taskId;
        String userDir = "./user_" + username;
        try {
            sftpChannel.cd(remoteRootDir);
        } catch (SftpException e) {
            sftpChannel.mkdir(remoteRootDir);
            sftpChannel.cd(remoteRootDir);
        }
        try {
            sftpChannel.cd(taskDir);
        } catch (SftpException e) {
            sftpChannel.mkdir(taskDir);
            sftpChannel.cd(taskDir);
        }

        try {
            sftpChannel.cd(userDir);
        } catch (SftpException e) {
            sftpChannel.mkdir(userDir);
            sftpChannel.cd(userDir);
        }

        FileInputStream fileInputStream = new FileInputStream(fileStoragePath +"user_" + file.getCreatedBy() + "/" +file.getId() + '_' + file.getName());

        sftpChannel.put(fileInputStream, file.getName());

        fileInputStream.close();
        sftpChannel.disconnect();
    }
    public void compileAndExecuteFile(Session session, File file, String taskId, String username) throws JSchException, IOException, InterruptedException, SftpException {
        String userDir = "./eduhub/task_" + taskId + "/user_" + username + "/";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ChannelExec execChannel = (ChannelExec) session.openChannel("exec");
        String compileCommand = "/usr/local/cuda/bin/nvcc " + userDir + file.getName() + " -o " + userDir + file.getName().substring(0, file.getName().lastIndexOf('.'));
        execChannel.setCommand(compileCommand);
        execChannel.setOutputStream(outputStream);
        execChannel.setErrStream(outputStream);
        execChannel.connect();

        while (!execChannel.isClosed()) {
            Thread.sleep(100);
        }
        execChannel.disconnect();

        String executable = file.getName().substring(0, file.getName().lastIndexOf('.'));
        List<File> testFiles = taskService.findTaskFilesByCategory(taskId, FileCategory.CLUSTER_TEST);
        for (File testFile : testFiles) {
            if (testFile.getName().contains("test")) {
                sendFileToCluster(session, testFile, taskId, username);

                execChannel = (ChannelExec) session.openChannel("exec");
                String inputFilePath = userDir + testFile.getName();
                String outputFilePath = userDir + "res.txt";
                String command = "cd " + userDir + " && ./" + executable + " " + inputFilePath + " " + outputFilePath;

                execChannel.setCommand(command);
                execChannel.setOutputStream(outputStream);
                execChannel.setErrStream(outputStream);
                execChannel.connect();

                while (!execChannel.isClosed()) {
                    Thread.sleep(100);
                }
                execChannel.disconnect();

                ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
                sftpChannel.connect();
                String localResFilePath = fileStoragePath + "user_" + username + "/res.txt";
                sftpChannel.get(outputFilePath, localResFilePath);
                sftpChannel.disconnect();

                for (File resTestFile : testFiles) {
                    if (resTestFile.getName().contains("res")) {
                        boolean comparisonResult = compareFirst10Values(localResFilePath, fileStoragePath + "user_" + resTestFile.getCreatedBy() + "/" + resTestFile.getId() + "_" + resTestFile.getName());
                        outputStream.write(("Comparison result: " + comparisonResult + "\n").getBytes());
                        break;
                    }
                }
            }
        }

        String logFileName = file.getName().substring(0, file.getName().lastIndexOf('.')) + ".txt";
        File logFile = new File();
        logFile.setName(logFileName);
        logFile.setCategory(FileCategory.CLUSTER_LOG);
        logFile.setCreatedBy(username);
        fileRepository.save(logFile);

        Path logFilePath = Paths.get(fileStoragePath, "user_" + username, logFile.getId() + "_" + logFileName);
        Files.write(logFilePath, outputStream.toByteArray());
        taskService.addFileToTask(taskId, logFile.getId());
    }

    private boolean compareFirst10Values(String localResFilePath, String testFilePath) throws IOException {
        try (BufferedReader resReader = new BufferedReader(new FileReader(localResFilePath));
             BufferedReader testReader = new BufferedReader(new FileReader(testFilePath))) {

            for (int i = 0; i < 10; i++) {
                String resLine = resReader.readLine();
                String testLine = testReader.readLine();
                if (resLine == null || !resLine.equals(testLine)) {
                    return false;
                }
            }
        }
        return true;
    }
}
