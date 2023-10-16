package cli.rabbit.client.rabbitclient.publisher;

import cli.rabbit.client.rabbitclient.config.GracefulShutdown;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
/**
 * Publishing messages is an asynchronous mechanism and, by default, messages that cannot be
 * routed are dropped by RabbitMQ. For successful publishing, you can receive an asynchronous
 * confirmation, as described in Correlated Publisher Confirms and Returns.
 * • Publish to an exchange but there is no matching destination queue. => publisher returns
 * • Publish to a non-existent exchange. => channel will be closed by the broker => 1.Channel listeners 2.publisher confirm
 */
public class LoggerConfirmCallback implements RabbitTemplate.ConfirmCallback {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final GracefulShutdown gracefulShutdown;

    public LoggerConfirmCallback(GracefulShutdown gracefulShutdown) {
        this.gracefulShutdown = gracefulShutdown;
    }

    @Override
    /**
     * The CorrelationData is an object supplied by the client when sending the original message. The ack
     * is true for an ack and false for a nack. For nack instances, the cause may contain a reason for the
     * nack, if it is available when the nack is generated.
     */
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String correlationId = correlationData.getId();
        if (ack){
        ReturnedMessage returned = correlationData.getReturned();
        if (returned == null){
            logger.info("message with correlation-id: {} successfully published!",correlationId);
            gracefulShutdown.successfulShutdown();
            return;//shutdown is async
        }else {
            logger.error("message with correlation-id: {} didn't route! reply-text: {}",correlationId,returned.getReplyText());
        }
    }else {
        logger.error("message with correlation-id: {} sending failed! cause: {}",correlationId,cause);
    }
        gracefulShutdown.rabbitErrorShutdown();
    }
}
