package be.chat;

import be.chat.dto.MessageDTO;
import com.sun.enterprise.security.ee.auth.login.ProgrammaticLogin;
import org.glassfish.internal.api.ORBLocator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.time.LocalDateTime;
import java.util.Properties;

public class App {

    private static final String REMOTE_HOST = "127.0.0.1";

    private static final String AUTH_CONF_PATH = "C:\\tmp\\auth.conf";

    private static ChatRemote lookupRemoteChat() throws NamingException {
        final Properties props = new Properties();

        props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.enterprise.naming.SerialInitContextFactory");
        props.setProperty(Context.URL_PKG_PREFIXES,
                "com.sun.enterprise.naming");
        props.setProperty(Context.STATE_FACTORIES,
                "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
        props.setProperty(ORBLocator.OMG_ORB_INIT_HOST_PROPERTY, REMOTE_HOST);
        props.setProperty(ORBLocator.OMG_ORB_INIT_PORT_PROPERTY, ORBLocator.DEFAULT_ORB_INIT_PORT);

        System.setProperty("java.security.auth.login.config", AUTH_CONF_PATH);
        new ProgrammaticLogin().login("ejbuser", "password123".toCharArray());

        final Context context = new InitialContext(props);
        return (ChatRemote) context.lookup(ChatRemote.class.getName());
    }

    public static void main(String[] args) throws NamingException {
        final ChatRemote chat = lookupRemoteChat();
        chat.sendMessageDTO(MessageDTO.builder()
                .owner("App-GlassFish-Client")
                .time(LocalDateTime.now())
                .message("Wiadmość numer 11 ze świata GlassFisza")
                .build());
    }

}
