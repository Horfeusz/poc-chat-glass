package be.chat.client;

import be.chat.ChatException;
import be.chat.dao.UserDao;
import be.chat.model.User;
import be.chat.util.Consumer;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import java.util.logging.Logger;

@Stateless
@LocalBean
@PermitAll
public class RemoteUtil {

    private Logger logger = Logger.getLogger(RemoteUtil.class.getName());

    @Resource
    private SessionContext sessionContext;

    @EJB
    private ContextFactory contextFactory;

    @EJB
    private UserDao userDao;

    private <T> String getJndiRemoteAddress(Class<T> remoteInterfaceClass) {
        //TODO take the JNDI name from the DB
        String appName = "";
        String moduleName = "wildfly-chat";
        String beanName = "ChatBean";
        final String viewClassName = remoteInterfaceClass.getName();
        return "ejb:" + appName + "/" + moduleName + "/" + beanName + "!" + viewClassName;
    }

    @SuppressWarnings("unchecked")
    private <T> T lookup(Class<T> remoteInterfaceClass) throws NamingException {
        String jndiRemoteAddress = getJndiRemoteAddress(remoteInterfaceClass);
        User user = userDao.findByLogin(sessionContext.getCallerPrincipal().getName());
        logger.info("I am looking remote bean: " + jndiRemoteAddress + " for " + user);
        return (T) contextFactory.getContext(user).lookup(jndiRemoteAddress);
    }

    public <T> void lookup(Class<T> remoteClass, Consumer<T> consumer) {
        try {
            T remoteObject = lookup(remoteClass);
            if (remoteObject != null) {
                consumer.accept(remoteObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ChatException(e);
        }
    }
}
