package be.chat.rest.config;

import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.text.SimpleDateFormat;

/**
 * Jackson configuration
 */
@Provider
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class JacksonConfig implements ContextResolver<ObjectMapper> {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private ObjectMapper objectMapper;

    public JacksonConfig() {
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
    }


    @Override
    public ObjectMapper getContext(Class<?> aClass) {
        return objectMapper;
    }

}
