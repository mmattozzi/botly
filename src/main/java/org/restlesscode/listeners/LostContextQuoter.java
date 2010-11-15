package org.restlesscode.listeners;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.restlesscode.MessageContext;
import org.restlesscode.MessageListenerAdapter;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LostContextQuoter extends MessageListenerAdapter {

    protected SolrServer solrServer;
    protected Random random = new Random();

    protected static Pattern charQuoteRequest = Pattern.compile("!do (.*)");
    protected static Pattern epRequest = Pattern.compile("!ep (.*)");

    @Override
    public boolean handleMessage(MessageContext messageContext) {
        String message = messageContext.getMessage();

        if (messageContext.wasDirectlyAddressed()) {
            message = message.substring(10);
            message = message.replaceAll(":", "");
            String msg = getRandomQuote(message);
            messageContext.setResponse(msg);
            return false;
        }

        return true;
    }

    public String getRandomQuote(String message) {
        String quote = null;

        try {
            SolrQuery solrQuery = new SolrQuery(message);
            solrQuery.setRows(5);
            QueryResponse response = solrServer.query(solrQuery);
            if (response.getResults().size() > 0) {
                SolrDocument matchingDoc = response.getResults().get(random.nextInt(response.getResults().size()));
                quote = String.format("%s, %s: %s", matchingDoc.getFieldValue("episode"), capFirstLetter((String) matchingDoc.getFieldValue("person")), matchingDoc.getFieldValue("line"));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        if (quote == null) {
            System.err.println("Pulling random quote from db instead.");
            quote = getRandomQuoteFromDB();
        }

        return quote;
    }

    public String getRandomQuoteFromDB() {
        String quote = null;
        try {
            Map<String, Object> row = simpleJdbcTemplate.queryForMap(
                    "select episode, person, line from lost where length(line) > 10 order by rand() limit 1");
            quote = String.format("%s, %s: %s", row.get("episode"), capFirstLetter(row.get("person").toString()), row.get("line"));
        } catch (Throwable t) {
            quote = "Fate.";
        }
        return quote;
    }

    public String capFirstLetter(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @Override
    public void doInit() {
        try {
            solrServer = new CommonsHttpSolrServer("http://192.168.0.111:8080/solr");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
