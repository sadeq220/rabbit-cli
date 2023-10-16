package cli.rabbit.client.rabbitclient.config;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
/**
 * async call the shutdown to prevent deadlock
 * spring has registered a JVM shutdown hook for graceful shutdown
 * System.exit() starts shutdown hooks
 */
public class GracefulShutdown {
    @Async
    public void successfulShutdown(){
        System.exit(0);
    }
    @Async
    public void unprovidedParameterShutdown(){
        System.exit(5);
    }
    @Async
    public void rabbitErrorShutdown(){
        System.exit(1);
    }
}
