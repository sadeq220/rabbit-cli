package cli.rabbit.client.rabbitclient.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class ConsumerUtils {
    private static Logger logger= LoggerFactory.getLogger(ConsumerUtils.class);

    public static String payloadAsString(Message message){
        byte[] payload = message.getBody();
        MessageProperties messageProperties = message.getMessageProperties();
        String contentEncoding = messageProperties.getContentEncoding();
        try {
            return new String(payload, contentEncoding==null ? "UTF-8" : contentEncoding);
        } catch (UnsupportedEncodingException e) {
            logger.error("content encoding not supported");
            return new String(payload, StandardCharsets.UTF_8);
        }
    }

    public static String constructIniStyleString(String sectionName,Map entries){
        StringBuilder stringBuilder = new StringBuilder();
        String section = String.format("[%s] ", sectionName);
        stringBuilder.append(section);
        entries.forEach((k,v)->{
            String entry = String.format("%s=%s", k.toString(), v.toString());
            stringBuilder.append(entry).append(" ");
        });
        return stringBuilder.toString();
    }
}
