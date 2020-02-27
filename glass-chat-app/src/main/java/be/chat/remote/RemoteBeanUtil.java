package be.chat.remote;

import com.google.common.base.Throwables;
import org.wildfly.security.WildFlyElytronProvider;
import org.wildfly.security.auth.client.AuthenticationConfiguration;
import org.wildfly.security.auth.client.AuthenticationContext;
import org.wildfly.security.auth.client.MatchRule;
import org.wildfly.security.sasl.SaslMechanismSelector;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.security.Principal;
import java.security.Provider;
import java.util.Hashtable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class RemoteBeanUtil {

    private static final String REMOTE_HOST = "localhost";

    private static final String REMOTE_PORT = "8090";

    private Logger logger = Logger.getLogger(RemoteBeanUtil.class.getName());

    @Resource
    private SessionContext sessionContext;

    private String getCallerPrincipalName() {
        return Optional.ofNullable(sessionContext)
                .map(SessionContext::getCallerPrincipal)
                .map(Principal::getName).orElseThrow(IllegalStateException::new);
    }

    private String getCallerPrincipalPassword() {
        //FIXME Password get from DB
        return "password123";
    }

    public <T> void lookup(Class<T> remoteClass, Consumer<T> consumer) {
        AuthenticationConfiguration adminConfig =
                AuthenticationConfiguration
                        .empty()
                        .useProviders(() -> new Provider[]{new WildFlyElytronProvider()})
                        .setSaslMechanismSelector(SaslMechanismSelector.NONE.addMechanism("DIGEST-MD5"))
                        .useName(getCallerPrincipalName())
                        .usePassword(getCallerPrincipalPassword());
        AuthenticationContext context = AuthenticationContext.empty();
        context = context.with(MatchRule.ALL, adminConfig);
        context.run(() -> {
            try {
                lookup(remoteClass).ifPresent(remote -> {
                    logger.info("Obtained a remote object");
                    consumer.accept(remote);
                });
            } catch (Exception e) {
                logger.throwing("RemoteBeanUtil", "lookup", e);
            }
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> Optional<T> lookup(Class<T> remoteClass) {
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
            Context context = new InitialContext(jndiProperties);

            String appName = "";
            String moduleName = "wildfly-chat";

            // The EJB name which by default is the simple class name of the bean implementation class
            String beanName = "ChatBean";

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
