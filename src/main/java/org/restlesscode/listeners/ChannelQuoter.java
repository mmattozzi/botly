package org.restlesscode.listeners;

import org.restlesscode.MessageContext;
import org.restlesscode.MessageListener;
import org.restlesscode.MessageListenerAdapter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created: May 16, 2010
 *
 * @author mmattozzi
 */
public class ChannelQuoter extends MessageListenerAdapter {

    protected static Pattern userQuoteRequest = Pattern.compile("!do (.*)");
    protected static Pattern messageRequest = Pattern.compile("!msg (.*)");

    @Override
    public boolean handleMessage(MessageContext messageContext) {
        String message = messageContext.getMessage();
        if (message.startsWith("!do")) {
            Matcher m = userQuoteRequest.matcher(message);
            if (m.matches()) {
                String user = m.group(1);
                if (user != null) {
                    String msg = getQuote(user);
                    if (msg != null) {
                        messageContext.setResponse(msg);
                        return false;
                    }
                }
            }
        }

        if (message.startsWith("!msg")) {
            Matcher m = messageRequest.matcher(message);
            if (m.matches()) {
                String user = m.group(1);
                if (user != null) {
                    String msg = getMessage(user);
                    if (msg != null) {
                        messageContext.setResponse(msg);
                    }
                }
            }
        }

        return true;
    }

    public String getQuote(String username) {
        if (username == null) return null;

        Map<String, Object> row = simpleJdbcTemplate.queryForMap(
                "select id, message, nick from messages where nick = ? order by rand() limit 1", username);
        String quote = String.format("#%d %s: %s", row.get("id"), row.get("nick"), row.get("message"));
        return quote;
    }

    public String getMessage(String msgGuid) {
        if (msgGuid == null) return null;

        Map<String, Object> row = simpleJdbcTemplate.queryForMap("select nick, message from messages where id = ?", msgGuid);
        String quote = row.get("nick") + ": " + row.get("message");

        return quote;
    }

}
