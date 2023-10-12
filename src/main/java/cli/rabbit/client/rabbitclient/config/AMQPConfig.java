package cli.rabbit.client.rabbitclient.config;

import cli.rabbit.client.rabbitclient.consumer.AMQPConsumer;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
public class AMQPConfig {

    @Bean
    public ConnectionFactory connectionAndChannelsToRabbitmqMessageBroker(){
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        /**
         * A ConnectionFactory implementation that (when the cache mode is CachingConnectionFactory.CacheMode.CHANNEL (default) returns the same Connection from all createConnection() calls,
         * and ignores calls to Connection.close() and caches Channel.
         * CachingConnectionFactory, by default, establishes a single connection proxy that can be shared by the application.
         * Sharing of the connection is possible since the “unit of work” for messaging with AMQP is actually a “channel”
         *
         * default cache size is one
         */
        cachingConnectionFactory.setHost("localhost");
        cachingConnectionFactory.setPort(5672);
        cachingConnectionFactory.setUsername("guest");
        cachingConnectionFactory.setPassword("guest");
        cachingConnectionFactory.setConnectionNameStrategy(connectionFactory->"connection-name");
        return cachingConnectionFactory;
    }

    /**
     * retry-backoff-deadLettering
     * DLX (Dead Letter Exchanges) and DLQ (Dead-Letter-Queues) are both something you can configure as a policy for all queues or manually per queue.
     * Dead-Lettering defines what should happen with messages that get rejected by a consumer ( basic.reject or basic.nack with requeue parameter set to false)
     * we want to first retry a failed messages (Poison Messages are messages that can not get consumed) and then deadLetter them
     * RejectAndDontRequeueRecoverer : MessageRecover that causes the listener container to reject the message without requeuing. This enables failed messages to be sent to a Dead Letter Exchange/Queue, if the broker is so configured.
     *
     * Unfortunately, basic.reject provides no support for negatively acknowledging messages in bulk.
     * To solve this, RabbitMQ supports the basic.nack method that provides all the functionality of basic.reject whilst also allowing for bulk processing of messages.
     *
     * read more about <a href="https://www.rabbitmq.com/dlx.html">DLX</a>
     *                 <a href="https://www.rabbitmq.com/nack.html">nack</a>
     */
    @Bean
    public RetryOperationsInterceptor amqpRetryBackoff(){
        return RetryInterceptorBuilder.stateless()
                //.backOffOptions(100,3.0,1000) // 100ms wait time (delay) to retry
                .maxAttempts(1)
                .recoverer(new RejectAndDontRequeueRecoverer()) // Callback for message that was consumed but failed all retry attempts.
                .build();
    }
    @Bean
    /**
     * define messageListenerContainer
     */
    @ConditionalOnProperty(name = "application.mode",havingValue = "consumer")
    public SimpleMessageListenerContainer createMessageListenerContainer(ConnectionFactory connectionFactory,
                                                                         @Value("${amqp.queue.listener}") String queueName,
                                                                         AMQPConsumer messageListener,
                                                                         RetryOperationsInterceptor retryOperationsInterceptor){
        SimpleMessageListenerContainer messageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        messageListenerContainer.addQueueNames(queueName);
        messageListenerContainer.setMessageListener(messageListener);
        messageListenerContainer.setAdviceChain(retryOperationsInterceptor);
        messageListenerContainer.setDefaultRequeueRejected(false);
        return messageListenerContainer;
    }

}
