package be.chat;

import be.chat.dto.MessageDTO;
import org.glassfish.internal.api.ORBLocator;

import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;

public class App {

    private static final String REMOTE_HOST = "127.0.0.1";

    private static ChatRemote lookupRemoteChat() throws NamingException {
        final Properties props = new Properties();;

        props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.enterprise.naming.SerialInitContextFactory");
        props.setProperty(Context.URL_PKG_PREFIXES,
                "com.sun.enterprise.naming");
        props.setProperty(Context.STATE_FACTORIES,
                "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
        props.setProperty(ORBLocator.OMG_ORB_INIT_HOST_PROPERTY, REMOTE_HOST);
        props.setProperty(ORBLocator.OMG_ORB_INIT_PORT_PROPERTY, ORBLocator.DEFAULT_ORB_INIT_PORT);

        props.put(Context.SECURITY_PRINCIPAL, "frank");
        props.put(Context.SECURITY_CREDENTIALS, "password123");

        final Context context = new InitialContext(props);
        return (ChatRemote) context.lookup(ChatRemote.class.getName());
    }

    public static void main(String[] args) throws NamingException {
        final ChatRemote chat = lookupRemoteChat();

        // invoke on the remote bean

        //chat.sendMessage("Oficjalne słowo Testowe");

        chat.sendMessageDTO(MessageDTO.builder()
                .owner("App-GlassFish-Client")
                .time(LocalDateTime.now())
                .message("Wiadmość numer 10 ze świata GlassFisza")
                .build());
    }

}
