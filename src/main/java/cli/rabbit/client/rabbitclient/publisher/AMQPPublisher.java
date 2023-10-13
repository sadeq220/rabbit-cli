package cli.rabbit.client.rabbitclient.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@ConditionalOnProperty(name = "application.mode",havingValue = "producer")
public class AMQPPublisher implements ApplicationRunner {
    private final RabbitTemplate rabbitTemplate;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String exchangeOption="exchange";
    private final String routingKeyOption="routing-key";

    public AMQPPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
    logger.info("producer mode is enabled!");
    if (args.containsOption(exchangeOption) && args.containsOption(routingKeyOption) && !args.getNonOptionArgs().isEmpty()){
        String exchangeName = args.getOptionValues(exchangeOption).get(0);
        String routingKey = args.getOptionValues(routingKeyOption).get(0);
        List<String> nonOptionArgs = args.getNonOptionArgs();
        Message message = this.constructAMQPMessage(nonOptionArgs.get(0));
        logger.info("sending a message to exchange: {} with routing-key: {}",exchangeName,routingKey);
        CorrelationData correlationData = new CorrelationData();
        rabbitTemplate.send(exchangeName,routingKey,message,correlationData);
    } else {
        logger.error("please provide message payload with options: {} and {}",exchangeOption,routingKeyOption);
    }

    }
    private Message constructAMQPMessage(String payload){
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
        return MessageBuilder.withBody(payloadBytes).build();
    }
}
