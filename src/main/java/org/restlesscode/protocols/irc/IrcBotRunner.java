package org.restlesscode.protocols.irc;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import java.io.IOException;
import java.util.Date;

/**
 * Created: Apr 29, 2010
 *
 * @author mmattozzi
 */
public class IrcBotRunner extends Thread {

    protected IrcBot ircBot;
    protected String server;
    protected String channel;

    public IrcBotRunner(IrcBot ircBot, String server, String channel) {
        this.ircBot = ircBot;
        this.server = server;
        this.channel = channel;
    }
    
    @Override
    public void run() {
        while (true) {
			if (! ircBot.isConnected()) {
				try {
					System.err.println(new Date() + " Not connected, joining...");
					joinChannel();
				} catch (NickAlreadyInUseException e) {
                    String replacementName = ircBot.getDisplayName() + "_";

					if (replacementName.endsWith("___")) {
						replacementName.replaceAll("_", "");
					}

                    ircBot.setDisplayName(replacementName);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (IrcException e) {
					e.printStackTrace();
				}
			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    }

    public void joinChannel() throws NickAlreadyInUseException, IOException, IrcException {
		ircBot.connect(server);
		ircBot.joinChannel("#" + this.channel);
	}

}
