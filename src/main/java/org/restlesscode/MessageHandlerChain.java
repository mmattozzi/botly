package org.restlesscode;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

/**
 * Created: May 16, 2010
 *
 * @author mmattozzi
 */
public class MessageHandlerChain {

    protected List<MessageListener> messageListeners =
            new ArrayList<MessageListener>();

    protected Random random = new Random();

    /**
     *
     * @param sender
     * @param message
     * @return Response message
     */
    public String handleMessage(String sender, String message, String botName) {
        MessageContext messageContext = new MessageContext(sender, message, botName);

        for (MessageListener messageListener : messageListeners) {
            if (messageListener.getActive() && random.nextFloat() < messageListener.getProbability()) {
                try {
                    if (! messageListener.handleMessage(messageContext)) {
                        System.out.println("Message handled by " + messageListener.getClass().getSimpleName());
                        break;
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        
        return messageContext.getResponse();
    }

    public void addMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    public void addMessageListener(MessageListener messageListener, float probability) {
        messageListener.setProbability(probability);
        messageListeners.add(messageListener);
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

    public List<MessageListener> getMessageListeners() {
        return messageListeners;
    }

}
