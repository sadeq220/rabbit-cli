package cli.rabbit.client.rabbitclient.config;

import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.stereotype.Component;

@Component
public class AMQPConnectionListener implements ConnectionListener {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void onCreate(Connection connection) {
        if (connection.isOpen())
        logger.info("connection to AMQP broker has established.");
    }

    @Override
    public void onClose(Connection connection) {
        ConnectionListener.super.onClose(connection);
    }

    @Override
    public void onShutDown(ShutdownSignalException signal) {
        ConnectionListener.super.onShutDown(signal);
    }

    @Override
    public void onFailed(Exception exception) {
        logger.error("connection to AMQP broker failed! exception message: {}",exception.getMessage());
    }
}
