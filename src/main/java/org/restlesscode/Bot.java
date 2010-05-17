package org.restlesscode;

/**
 * Created: Apr 29, 2010
 *
 * @author mmattozzi
 */
public interface Bot {

    public void setDisplayName(String name);
    public String getDisplayName();
    
    public MessageHandlerChain getMessageHandlerChain();
    public Thread getRunner() throws IllegalStateException;

}
