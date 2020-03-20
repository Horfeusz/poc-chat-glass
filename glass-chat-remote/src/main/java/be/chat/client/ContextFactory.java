package be.chat.client;

import be.chat.model.User;
import org.jboss.ejb.client.ContextSelector;
import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;

import javax.ejb.Singleton;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

@Singleton
public class ContextFactory {

    private static final String REMOTE_HOST = "localhost";

    private static final String REMOTE_PORT = "8090";

    private Map<User, Context> contexts = new HashMap<>();

    public Context getContext(User user) throws NamingException {
        if (contexts.containsKey(user)) {
            return contexts.get(user);
        }
        initContext(user);
        return contexts.get(user);
    }

    @SuppressWarnings("unchecked")
    private void initContext(User user) throws NamingException {
        Properties properties = new Properties();
        properties.put("remote.connections", "default");
        properties.put("remote.connection.default.port", REMOTE_PORT);
        properties.put("remote.connection.default.host", REMOTE_HOST);
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
