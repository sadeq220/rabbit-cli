package cli.rabbit.client.rabbitclient.consumer;

import cli.rabbit.client.rabbitclient.persistence.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class AMQPConsumer implements MessageListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Boolean verbose;
    private final PersistenceService persistenceService;

    @Autowired
    public AMQPConsumer(@Value("${verbose}")Boolean verbose,PersistenceService persistenceService){
        this.verbose=verbose;
        this.persistenceService=persistenceService;
    }

    @Override
    public void onMessage(Message message) {
        MessageProperties messageProperties = message.getMessageProperties();
            Properties properties = new Properties();
            properties.put("payload",ConsumerUtils.payloadAsString(message));
            properties.put("exchange",messageProperties.getReceivedExchange());
            properties.put("routing_key",messageProperties.getReceivedRoutingKey());
            if (verbose){
                MDC.put("verbose",ConsumerUtils.constructIniStyleString("headers",messageProperties.getHeaders()));
            }
            logger.info(ConsumerUtils.constructIniStyleString("message",properties));
            persistenceService.persistMessage(message);
    }

    @Override
    public void containerAckMode(AcknowledgeMode mode) {
        MessageListener.super.containerAckMode(mode);
    }

    @Override
    public boolean isAsyncReplies() {
        return MessageListener.super.isAsyncReplies();
    }

    @Override
    public void onMessageBatch(List<Message> messages) {
        MessageListener.super.onMessageBatch(messages);
    }

}
