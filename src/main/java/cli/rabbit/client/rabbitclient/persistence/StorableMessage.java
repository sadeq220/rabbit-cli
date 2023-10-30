package cli.rabbit.client.rabbitclient.persistence;

import cli.rabbit.client.rabbitclient.consumer.ConsumerUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "RABBIT_MESSAGE")
public class StorableMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime consumedTime;
    private String payload;
    private String exchange;
    private String routingKey;
    private String queue;
    private String headers;

    public static StorableMessage valueOf(Message message){
        MessageProperties messageProperties = message.getMessageProperties();
        StorableMessage storableMessage = new StorableMessage();
        storableMessage.consumedTime=LocalDateTime.now();
        storableMessage.exchange= messageProperties.getReceivedExchange();
        storableMessage.routingKey= messageProperties.getReceivedRoutingKey();
        storableMessage.queue= messageProperties.getConsumerQueue();
        storableMessage.payload= ConsumerUtils.payloadAsString(message);
        storableMessage.headers= ConsumerUtils.constructIniStyleString(null,messageProperties.getHeaders());
        return storableMessage;
    }
}
