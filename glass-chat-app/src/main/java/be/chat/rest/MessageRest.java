package be.chat.rest;

import be.chat.ChatRemote;
import be.chat.MessageDTOFactory;
import be.chat.client.RemoteUtil;
import be.chat.dto.MessageDTO;
import be.chat.util.Consumer;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/message")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Stateless
@PermitAll
public class MessageRest {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Context
    private SecurityContext securityContext;

    @EJB
    private MessageDTOFactory messageDTOFactory;

    @EJB
    private ChatRemote chat;

    @EJB
    private RemoteUtil remoteUtil;

    @RolesAllowed("manager")
    @PUT
    public void sendMessage(MessagesContainer messagesContainer) {
        if (messagesContainer == null) {
            return;
        }
        if (messagesContainer.getMessages() == null) {
            return;
        }
        if (messageDTOFactory == null) {
            logger.warning("messageDTOFactory is null");
            return;
        }
        if (chat == null) {
            logger.warning("chat is null");
            return;
        }

        for (String message : messagesContainer.getMessages()) {
            final MessageDTO messageDTO = messageDTOFactory.create(message);
            chat.sendMessageDTO(messageDTO);
            remoteUtil.lookup(ChatRemote.class, new Consumer<ChatRemote>() {
                @Override
                public void accept(ChatRemote chatRemote) {
                    chatRemote.sendMessageDTO(messageDTO);
                }
            });
        }
    }

    @GET
    public List<MessageDTO> getDtoMessages() {
        if (chat == null) {
            logger.warning("Chat bean is null !!!");
            return new ArrayList<>();
        }
        return chat.getDTOMessages();
    }

}
