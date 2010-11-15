package org.restlesscode.bots;

import org.restlesscode.listeners.*;
import org.restlesscode.protocols.irc.IrcBot;

/**
 * Created: May 16, 2010
 *
 * @author mmattozzi
 */
public class Sonofcim extends IrcBot {

    public Sonofcim() {
        super();
        setDisplayName("sonofcim");
        setIrcChannel("milhouse");
        setIrcServer("irc.freenode.net");
        getMessageHandlerChain().addMessageListener(new ChannelLogger());
        getMessageHandlerChain().addMessageListener(new ChannelQuoter());
        getMessageHandlerChain().addMessageListener(new LostQuoter());
        // getMessageHandlerChain().addMessageListener(new LivelockAnswerer());
        getMessageHandlerChain().addMessageListener(new LostContextQuoter(), 0.33f);
        YahooAnswerer yahooAnswerer = new YahooAnswerer();
        yahooAnswerer.setYahooId("sjaCBTfV34G0r7eTZhywNQ9b8WBrWpk6CY.3G4jZDP3JGeXJHPWfU40rKc4B2p_CLoM7");
        getMessageHandlerChain().addMessageListener(yahooAnswerer, 0.75f);
        getMessageHandlerChain().addMessageListener(new RandomChannelQuoter());

    }

}
