package org.restlesscode.protocols.irc;

import org.jibble.pircbot.PircBot;
import org.restlesscode.Bot;
import org.restlesscode.MessageHandlerChain;

/**
 * Created: Apr 29, 2010
 *
 * @author mmattozzi
 */
public class IrcBot extends PircBot implements Bot {

    protected String ircServer;
    protected String ircChannel;
    protected MessageHandlerChain messageHandlerChain;
    
    public IrcBot() {
        messageHandlerChain = new MessageHandlerChain();
    }

    public void setDisplayName(String name) {
        setName(name);
    }

    public String getDisplayName() {
        return getName();
    }

    public String getIrcServer() {
        return ircServer;
    }

    public void setIrcServer(String ircServer) {
        this.ircServer = ircServer;
    }

    public String getIrcChannel() {
        return ircChannel;
    }

    public void setIrcChannel(String ircChannel) {
        this.ircChannel = ircChannel;
    }

    @Override
    public MessageHandlerChain getMessageHandlerChain() {
        return this.messageHandlerChain;
    }

    @Override
	public void onJoin(String channel, String sender, String login, String hostname) {
		if (! sender.equals(this.getName())) {
			System.out.println(sender + " has joined.");
		}
	}

	@Override
	public void onMessage(String channel, String sender,
            String login, String hostname, String message) {
        String response = messageHandlerChain.handleMessage(sender, message);
        if (response != null) {
            sendMessage(channel, response);
        }
    }

    public Thread getRunner() throws IllegalStateException {
        if (this.getDisplayName() == null || this.getIrcServer() == null || this.getIrcChannel() == null) {
            throw new IllegalStateException("Irc bot not properly initialized");
        }
        return new IrcBotRunner(this, this.getIrcServer(), this.getIrcChannel());
    }

    @Override
    public String toString() {
        return this.getDisplayName() + " in " + this.getIrcChannel() + " on " + this.getIrcServer();
    }
}
