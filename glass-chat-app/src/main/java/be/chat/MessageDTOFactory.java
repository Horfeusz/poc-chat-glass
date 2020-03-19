package be.chat;

import be.chat.dto.MessageDTO;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import java.util.Date;

@Stateless
@LocalBean
public class MessageDTOFactory {

    private static final String GLASSFISH_PRINCIPAL_NAME = "Default_GlassFish_Principal";

    @Resource
    private SessionContext sessionContext;

    public MessageDTO create(String message) {
        return MessageDTO.builder()
                .message(message)
                .time(new Date())
                .owner(getOwner())
                .build();
    }

    private String getOwner() {
        String principalName = sessionContext.getCallerPrincipal().getName();
        if ("anonymous".equals(principalName.toLowerCase())) {
            return GLASSFISH_PRINCIPAL_NAME;
        } else {
            return principalName;
        }
    }
}
