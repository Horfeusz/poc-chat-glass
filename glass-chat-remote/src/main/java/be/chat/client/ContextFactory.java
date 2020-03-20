package be.chat.client;

import be.chat.model.User;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.jboss.ejb.client.ContextSelector;
import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;

import javax.ejb.Singleton;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.*;

@Singleton
public class ContextFactory {

    private static final String REMOTE_HOST_PROPERTY_NAME = "poc-chat-wildfly-host";

    private static final String REMOTE_PORT_PROPERTY_NAME = "poc-chat-wildfly-port";

    private static final String DEFAULT_REMOTE_HOST = "localhost";

    private static final String DEFAULT_REMOTE_PORT = "8090";

    private Map<User, Context> contexts = new HashMap<>();

    public Context getContext(User user) throws NamingException {
        if (contexts.containsKey(user)) {
            return contexts.get(user);
        }
        initContext(user);
        return contexts.get(user);
    }

    private String getRemoteHost() {
        String remoteHost = System.getProperties().getProperty(REMOTE_HOST_PROPERTY_NAME);
        return Strings.isNullOrEmpty(remoteHost) ? DEFAULT_REMOTE_HOST : remoteHost;
    }

    private String getRemotePort() {
        String remotePort = System.getProperties().getProperty(REMOTE_PORT_PROPERTY_NAME);
        return Strings.isNullOrEmpty(remotePort) ? DEFAULT_REMOTE_PORT : remotePort;
    }

    @SuppressWarnings("unchecked")
    private void initContext(User user) throws NamingException {
        Preconditions.checkArgument(user != null, "User is null !!!");

        Properties properties = new Properties();
        properties.put("remote.connections", "default");
        properties.put("remote.connection.default.host", getRemoteHost());
        properties.put("remote.connection.default.port", getRemotePort());
        properties.put("remote.connection.default.username", user.getLogin());
        properties.put("remote.connection.default.password", user.getPassword());
        EJBClientConfiguration ejbClientConfiguration = new PropertiesBasedEJBClientConfiguration(properties);
        ContextSelector<EJBClientContext> contextSelector = new ConfigBasedEJBClientContextSelector(ejbClientConfiguration);
        EJBClientContext.setSelector(contextSelector);

        final Hashtable jndiProperties = new Hashtable();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");

        contexts.put(user, new InitialContext(jndiProperties));
    }

}
