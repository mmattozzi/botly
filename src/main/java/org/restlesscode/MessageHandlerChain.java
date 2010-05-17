package org.restlesscode;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created: May 16, 2010
 *
 * @author mmattozzi
 */
public class MessageHandlerChain {

    protected List<MessageListener> messageListeners =
            new ArrayList<MessageListener>();
    protected Map<MessageListener, Boolean> listenerActive =
            new HashMap<MessageListener, Boolean>();

    /**
     *
     * @param sender
     * @param message
     * @return Response message
     */
    public String handleMessage(String sender, String message) {
        MessageContext messageContext = new MessageContext(sender, message);

        for (MessageListener messageListener : messageListeners) {
            if (listenerActive.get(messageListener)) {
                if (! messageListener.handleMessage(messageContext)) {
                    break;
                }
            }
        }
        
        return messageContext.getResponse();
    }

    public void addMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
        listenerActive.put(messageListener, true);
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        for (MessageListener messageListener : messageListeners) {
            messageListener.setJdbcTemplate(jdbcTemplate);
        }
    }

    public void initListeners() {
        for (MessageListener messageListener : messageListeners) {
            messageListener.doInit();
        }
    }
}
