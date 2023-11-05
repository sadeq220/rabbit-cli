package cli.rabbit.client.rabbitclient.config;

import cli.rabbit.client.rabbitclient.RabbitClientApplication;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class DataStoreConfig {
    @Bean
    public DataSource connectionConfiguration(){
        Path dataDirectoryPath = this.getDataDirectoryPath();
        Path sqliteDatabasePath = dataDirectoryPath.resolve("rabbit-consumed-messages.sqlite").toAbsolutePath();

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.sqlite.JDBC");
        dataSourceBuilder.url("jdbc:sqlite:"+sqliteDatabasePath);
        dataSourceBuilder.username("sa");
        dataSourceBuilder.password("sa");
        return dataSourceBuilder.build();
    }
    private Path getDataDirectoryPath(){
        try {
            String jarFilePath = RabbitClientApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            if (jarFilePath.contains("!")) {
                jarFilePath = jarFilePath.substring(0, jarFilePath.indexOf('!'));
                jarFilePath=new URL(jarFilePath).getPath();
            }
            Path dataDirectoryPath = Path.of(jarFilePath).normalize().getParent().getParent().resolve("data");
            if (Files.notExists(dataDirectoryPath)) {
                Files.createDirectory(dataDirectoryPath);
            }
            return dataDirectoryPath;
        } catch (IOException e) {
            throw new RuntimeException("couldn't create a data directory!",e);
        }
    }


}
