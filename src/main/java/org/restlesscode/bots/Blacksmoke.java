package org.restlesscode.bots;

import org.restlesscode.listeners.ChannelLogger;
import org.restlesscode.listeners.ChannelQuoter;
import org.restlesscode.listeners.LostQuoter;
import org.restlesscode.protocols.irc.IrcBot;

/**
 * Created: May 16, 2010
 *
 * @author mmattozzi
 */
public class Blacksmoke extends IrcBot {

    public Blacksmoke() {
        super();
        setDisplayName("blacksmoke");
        setIrcChannel("milhouse");
        setIrcServer("irc.freenode.net");
        getMessageHandlerChain().addMessageListener(new ChannelLogger());
        getMessageHandlerChain().addMessageListener(new LostQuoter());
        getMessageHandlerChain().addMessageListener(new ChannelQuoter());

    }

}
