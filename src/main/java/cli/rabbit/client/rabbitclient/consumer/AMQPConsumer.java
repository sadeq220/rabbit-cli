package cli.rabbit.client.rabbitclient.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class AMQPConsumer implements MessageListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Boolean verbose;
    public AMQPConsumer(@Value("${verbose}")Boolean verbose){
        this.verbose=verbose;
    }

    @Override
    public void onMessage(Message message) {
        byte[] payload = message.getBody();
        MessageProperties messageProperties = message.getMessageProperties();
        String contentEncoding = messageProperties.getContentEncoding();
        try {
            Properties properties = new Properties();
            properties.put("payload",new String(payload, contentEncoding==null ? "UTF-8" : contentEncoding));
            properties.put("exchange",messageProperties.getReceivedExchange());
            properties.put("routing_key",messageProperties.getReceivedRoutingKey());
            if (verbose){
                MDC.put("verbose",this.constructLogMessage(messageProperties.getHeaders()));
            }
            logger.info(this.constructLogMessage(properties));
        } catch (UnsupportedEncodingException e) {
            logger.error("content encoding not supported");
        }
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

    private String constructLogMessage(Map properties){
        StringBuilder stringBuilder = new StringBuilder();
        properties.forEach((k,v)-> {
            String entry = String.format("%s=%s", k.toString(), v.toString());
            stringBuilder.append(entry).append(" ");
        });
        return stringBuilder.toString();
    }
}
