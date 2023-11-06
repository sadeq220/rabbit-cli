package cli.rabbit.client.rabbitclient.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@Component
public class ExternalConfFileLoader implements ApplicationContextInitializer<GenericApplicationContext> {
    @Override
    public void initialize(GenericApplicationContext applicationContext) {
        ConfigurableEnvironment configurableEnvironment = applicationContext.getEnvironment();
        MutablePropertySources mutablePropertySources = configurableEnvironment.getPropertySources();
        if (ArchivedReleasePathLocator.isArchivedRelease())
        mutablePropertySources.addFirst(this.externalPropertySource());
    }
    private PropertySource externalPropertySource(){
        if (!ArchivedReleasePathLocator.isArchivedRelease()){
            return null;
        }
        Path configDirectoryPath = ArchivedReleasePathLocator.getConfigDirectoryPath();
        Properties externalProperties = new Properties();
        try {
            Files.list(configDirectoryPath)
                 .filter(path->path.getFileName().toString().endsWith(".conf"))
                 .sorted((p1,p2)->p2.getFileName().compareTo(p1.getFileName()))
                 .forEach(path -> {
                try (InputStream inStream=new FileInputStream(path.toAbsolutePath().toString())){
                    externalProperties.load(inStream);
                } catch (IOException e) {
                    throw new RuntimeException("conf file can not be read!",e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("conf directory can not be listed!",e);
        }
        PropertiesPropertySource propertySource = new PropertiesPropertySource("external", externalProperties);
        return propertySource;
    }
}
