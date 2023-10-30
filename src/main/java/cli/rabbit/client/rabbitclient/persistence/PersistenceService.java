package cli.rabbit.client.rabbitclient.persistence;

import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersistenceService {
    private final StorableMessageRepository storableMessageRepository;
    @Autowired
    public PersistenceService(StorableMessageRepository storableMessageRepository) {
        this.storableMessageRepository = storableMessageRepository;
    }
    public void persistMessage(Message message){
        StorableMessage storableMessage = StorableMessage.valueOf(message);
        storableMessageRepository.save(storableMessage);
    }
}
