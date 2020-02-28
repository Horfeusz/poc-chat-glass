package be.chat.rest;

import be.chat.ChatRemote;
import be.chat.dto.MessageDTO;
import be.chat.dto.MessageDTOFactory;
import be.chat.remote.WildflyRemoteUtil;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Path("/message")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@PermitAll
public class MessageRest {
    
    @Context
    private SecurityContext securityContext;

    @EJB
    private MessageDTOFactory messageDTOFactory;

    @EJB
    private ChatRemote chat;

    @EJB
    private WildflyRemoteUtil wildflyRemoteUtil;

    @RolesAllowed("admin")
    @PUT
    public Response sendMessage(MessagesContainer messagesContainer) {
        Optional.of(messagesContainer)
                .map(MessagesContainer::getMessages)
                .filter(CollectionUtils::isNotEmpty)
                .ifPresent(messages -> messages.stream()
                        .map(messageDTOFactory::create)
                        .forEach(messageDTO -> {
                            chat.sendMessageDTO(messageDTO);

                            //I try send messages to WildFly
                            wildflyRemoteUtil.lookup(ChatRemote.class,
                                    chatRemote -> chatRemote.sendMessageDTO(messageDTO));
                        }));

        return Response.ok().build();
    }

    @GET
    public List<MessageDTO> getDtoMessages() {
        return chat.getDTOMessages();
    }

}
