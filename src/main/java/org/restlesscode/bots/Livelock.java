package org.restlesscode.bots;

import org.restlesscode.listeners.*;
import org.restlesscode.protocols.irc.IrcBot;

/**
 * Created by IntelliJ IDEA.
 * User: mmattozzi
 * Date: Jun 5, 2010
 * Time: 2:20:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class Livelock extends IrcBot {

    public Livelock() {
        super();
        setDisplayName("livelock");
        setIrcChannel("milhouse");
        setIrcServer("irc.freenode.net");
        getMessageHandlerChain().addMessageListener(new LivelockAnswerer());
    }
}
