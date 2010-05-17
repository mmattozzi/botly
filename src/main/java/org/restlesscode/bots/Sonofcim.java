package org.restlesscode.bots;

import org.restlesscode.listeners.ChannelQuoter;
import org.restlesscode.protocols.irc.IrcBot;

/**
 * Created: May 16, 2010
 *
 * @author mmattozzi
 */
public class Sonofcim extends IrcBot {

    public Sonofcim() {
        super();
        setDisplayName("blacksmoke");
        setIrcChannel("milhouse2");
        setIrcServer("irc.freenode.net");
        getMessageHandlerChain().addMessageListener(new ChannelQuoter());
    }

}
