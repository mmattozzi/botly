package org.restlesscode;

/**
 * Created: Apr 29, 2010
 *
 * @author mmattozzi
 */
public interface MessageListener {

    /**
     *
     * @param sender - Sender of message
     * @param message - Message overheard
     * @return true to continue handling message, false to finish off message
     */
    public boolean handleMessage(String sender, String message);

}
