package be.chat;

import be.chat.dto.MessageDTO;
import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Singleton
public class ChatDb {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Getter
    private List<MessageDTO> messages;

    @PostConstruct
    private void init() {
        messages = new ArrayList<>();
    }

    public void addMessage(MessageDTO message) {
        if (message != null) {
            messages.add(message);
            logger.info("Added a message to DB: " + message);
        }
    }

}