package cli.rabbit.client.rabbitclient.config;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class GracefulShutdown {
    @Async
    public void shutdown(){
        System.exit(0);// spring has registered a JVM shutdown hook for graceful shutdown
    }
}
