package cli.rabbit.client.rabbitclient.publisher;

import cli.rabbit.client.rabbitclient.config.GracefulShutdown;
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
    private final GracefulShutdown gracefulShutdown;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String exchangeOption="exchange";
    private final String routingKeyOption="routing-key";

    public AMQPPublisher(RabbitTemplate rabbitTemplate,GracefulShutdown gracefulShutdown) {
        this.rabbitTemplate = rabbitTemplate;
        this.gracefulShutdown=gracefulShutdown;
    }

    @Override
    public void run(ApplicationArguments args) {
    logger.info("producer mode is enabled!");
    if (args.containsOption(exchangeOption) && args.containsOption(routingKeyOption) && !args.getNonOptionArgs().isEmpty()){
        String exchangeName = args.getOptionValues(exchangeOption).get(0);
        String routingKey = args.getOptionValues(routingKeyOption).get(0);
        String payload = args.getNonOptionArgs().get(0);
        Message message = this.constructAMQPMessage(payload);
        CorrelationData correlationData = new CorrelationData();
        logger.info("sending a message with correlation-id: {} to exchange: {} with routing-key: {} and payload: {}",correlationData.getId(),exchangeName,routingKey,payload);
        rabbitTemplate.send(exchangeName,routingKey,message,correlationData);
    } else if (args.containsOption("queue") && !args.getNonOptionArgs().isEmpty()) {
        String queueName = args.getOptionValues("queue").get(0);
        String payload = args.getNonOptionArgs().get(0);
        Message message = this.constructAMQPMessage(payload);
        CorrelationData correlationData = new CorrelationData();
        logger.info("sending a message with correlation-id: {} to queue: {} with payload: {}",correlationData.getId(),queueName,payload);
        rabbitTemplate.send("",queueName,message,correlationData);
    } else {
        logger.error("please provide message payload with options: {} and {}",exchangeOption,routingKeyOption);
        gracefulShutdown.unprovidedParameterShutdown();
    }

    }
    private Message constructAMQPMessage(String payload){
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
        return MessageBuilder
                .withBody(payloadBytes)
                .setContentEncoding("UTF-8")
                .build();
    }
}
