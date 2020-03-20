package be.chat;

import be.chat.dto.MessageDTO;
import com.sun.enterprise.security.ee.auth.login.ProgrammaticLogin;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Date;
import java.util.Properties;

public class StandaloneClientToGlassFish {

    private static final String REMOTE_HOST = "localhost";

    private static final String AUTH_CONF_PATH = "C:\\tmp\\auth.conf";

    private static final String USER = "Hendrik";

    private static final String PASSWORD = "Hendrik123";


    private static ChatRemote lookupRemoteChat() throws NamingException {
        final Properties props = new Properties();

        props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.enterprise.naming.SerialInitContextFactory");
        props.setProperty(Context.URL_PKG_PREFIXES,
                "com.sun.enterprise.naming");
        props.setProperty(Context.STATE_FACTORIES,
                "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
        props.setProperty("org.omg.CORBA.ORBInitialHost",
                REMOTE_HOST);
        props.setProperty("org.omg.CORBA.ORBInitialPort",
                "3700");

        System.setProperty("java.security.auth.login.config", AUTH_CONF_PATH);
        new ProgrammaticLogin().login(USER, PASSWORD.toCharArray());

        final Context context = new InitialContext(props);
        return (ChatRemote) context.lookup(ChatRemote.class.getName());
    }

    public static void main(String[] args) throws NamingException {
        final ChatRemote chat = lookupRemoteChat();
        chat.sendMessageDTO(MessageDTO.builder()
                .owner("App-GlassFish-Client")
                .time(new Date())
                .message("Message from GlassFish word (Standalone client).")
                .build());
    }

}
