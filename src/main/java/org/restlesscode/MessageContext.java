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
    protected String botName;

    public MessageContext(String sender, String message, String botName) {
        this.sender = sender;
        this.message = message;
        this.botName = botName;
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

    public String getBotName() {
        return botName;
    }

    public boolean wasDirectlyAddressed() {
        return message.contains(botName);
    }
}
