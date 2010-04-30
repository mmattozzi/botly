package org.restlesscode.irc;

import org.jibble.pircbot.PircBot;
import org.restlesscode.Bot;
import org.restlesscode.MessageListener;

/**
 * Created: Apr 29, 2010
 *
 * @author mmattozzi
 */
public class IrcBot extends PircBot implements Bot {

    public void setDisplayName(String name) {
        setName(name);
    }

    public String getDisplayName() {
        return getName();
    }

    public void addMessageListener(MessageListener messageListener) {

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

    }

}
