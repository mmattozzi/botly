package org.restlesscode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created: May 16, 2010
 *
 * @author mmattozzi
 */
public class MessageContext {

    protected String message;
    protected String response;
    protected String sender;
    protected Map<String, Object> properties = new HashMap<String, Object>();

    public MessageContext(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getSender() {
        return sender;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
