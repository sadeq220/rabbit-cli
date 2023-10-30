package cli.rabbit.client.rabbitclient.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorableMessageRepository extends JpaRepository<StorableMessage,Long> {
}
