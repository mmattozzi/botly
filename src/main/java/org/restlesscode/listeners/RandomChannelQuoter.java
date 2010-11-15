package org.restlesscode.listeners;

import org.restlesscode.MessageContext;
import org.restlesscode.MessageListenerAdapter;

public class RandomChannelQuoter extends MessageListenerAdapter {

    @Override
    public boolean handleMessage(MessageContext messageContext) {

        if (messageContext.wasDirectlyAddressed()) {
            String response = simpleJdbcTemplate.queryForObject("select message from messages m join (select floor(max(id)*rand()) " +
                            "as id from messages) as x on m.id >= x.id limit 1", String.class);

            messageContext.setResponse(response);
            return false;
        } else {
            return true;
        }
    }
}
