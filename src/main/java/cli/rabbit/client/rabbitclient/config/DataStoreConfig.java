package cli.rabbit.client.rabbitclient.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.nio.file.Path;

@Configuration
public class DataStoreConfig {
    @Bean
    public DataSource connectionConfiguration(){
        Path dataDirectoryPath = ArchivedReleasePathLocator.getDataDirectoryPath();
        Path sqliteDatabasePath = dataDirectoryPath.resolve("rabbit-consumed-messages.sqlite").toAbsolutePath();

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.sqlite.JDBC");
        dataSourceBuilder.url("jdbc:sqlite:"+sqliteDatabasePath);
        dataSourceBuilder.username("sa");
        dataSourceBuilder.password("sa");
        return dataSourceBuilder.build();
    }


}
