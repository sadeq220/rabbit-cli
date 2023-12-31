package cli.rabbit.client.rabbitclient.config;

import cli.rabbit.client.rabbitclient.RabbitClientApplication;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ArchivedReleasePathLocator {
    public static Path getDataDirectoryPath(){
        Path dataDirectoryPath = getRootDirectoryPath().resolve("data");
        createDirectory(dataDirectoryPath);
        return dataDirectoryPath;
    }

    public static Path getConfigDirectoryPath(){
        Path confDirectoryPath = getRootDirectoryPath().resolve("conf");
        createDirectory(confDirectoryPath);
        return confDirectoryPath;
    }

    public static Path getRootDirectoryPath(){
        URI jarFilePath = getJarFilePath();
        Path rootDataDirectory = Path.of(jarFilePath).normalize().getParent().getParent();
        return rootDataDirectory;
    }

    public static Boolean isArchivedRelease(){
      return getJarFilePath().getPath().endsWith(".jar");
    }

    public static URI getJarFilePath(){
        try {
            String jarFilePath = RabbitClientApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            if (jarFilePath.contains("!")) {
                jarFilePath = jarFilePath.substring(0, jarFilePath.indexOf('!'));
                jarFilePath = new URL(jarFilePath).getPath();
            }
            return new File(jarFilePath).toURI();// OS-agnostic file system path(windows compatible)
        }catch (IOException ex){
            throw new RuntimeException("jarFilePath couldn't be obtained!",ex);
        }
    }
    private static void createDirectory(Path requiredDirectory){
        try{
            if (Files.notExists(requiredDirectory)) {
            Files.createDirectory(requiredDirectory);
            }
        } catch (IOException e) {
        throw new RuntimeException("couldn't create a required directory!",e);
        }
    }
}
