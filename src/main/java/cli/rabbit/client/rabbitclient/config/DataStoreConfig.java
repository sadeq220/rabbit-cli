package cli.rabbit.client.rabbitclient.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataStoreConfig {
    @Bean
    public DataSource connectionConfiguration(){
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.sqlite.JDBC");
        dataSourceBuilder.url("jdbc:sqlite:rabbit-consumed-messages.sqlite");
        dataSourceBuilder.username("sa");
        dataSourceBuilder.password("sa");
        return dataSourceBuilder.build();
    }


}
