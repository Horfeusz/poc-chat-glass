package be.chat.remote;

import com.google.common.base.Throwables;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.Optional;
import java.util.logging.Logger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RemoteBeanUtil {

    private static final String REMOTE_HOST = "localhost";

    private static final String REMOTE_PORT = "8090";

    private static Logger logger = Logger.getLogger(RemoteBeanUtil.class.getName());

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Optional<T> lookup(Class<T> remoteClass) {
        final Hashtable jndiProperties = new Hashtable();

        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");

        //TODO Host and port get from parameters
        jndiProperties.put(Context.PROVIDER_URL,
                new StringBuilder("http-remoting://")
                        .append(REMOTE_HOST)
                        .append(":")
                        .append(REMOTE_PORT)
                        .toString());

        Object remoteObject = null;
        String jndiRemoteAddress = null;
        try {
            final Context context = new InitialContext(jndiProperties);

            final String appName = "";

            //TODO module name get from parameters
            final String moduleName = "wildfly-chat";

            // The EJB name which by default is the simple class name of the bean implementation class
            final String beanName = "ChatBean";

            // the remote view fully qualified class name
            final String viewClassName = remoteClass.getName();

            jndiRemoteAddress = "ejb:" + appName + "/" + moduleName + "/" + beanName + "!" + viewClassName;
            logger.info("I am looking bean: " + jndiRemoteAddress);

            // let's do the lookup
            remoteObject = context.lookup(jndiRemoteAddress);

        } catch (NamingException e) {
            logger.throwing("RemoteBeanUtil", "lookup", e);

            logger.warning(Throwables.getStackTraceAsString(e));
            logger.warning("I not found remote bean: " + jndiRemoteAddress);
        }

        return Optional.ofNullable((T) remoteObject);
    }

}
