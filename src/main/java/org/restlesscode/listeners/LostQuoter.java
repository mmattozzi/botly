package org.restlesscode.listeners;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.restlesscode.MessageContext;
import org.restlesscode.MessageListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created: May 16, 2010
 *
 * @author mmattozzi
 */
public class LostQuoter implements MessageListener {

    protected SimpleJdbcTemplate simpleJdbcTemplate;
    protected SolrServer solrServer;
    protected Random random = new Random();

    protected static Pattern charQuoteRequest = Pattern.compile("!do (.*)");
    protected static Pattern epRequest = Pattern.compile("!ep (.*)");

    @Override
    public boolean handleMessage(MessageContext messageContext) {
        String message = messageContext.getMessage();
        if (message.startsWith("!do")) {
            Matcher m = charQuoteRequest.matcher(message);
            if (m.matches()) {
                String user = m.group(1);
                if (user != null) {
                    String msg = getQuote(user.toLowerCase());
                    if (msg != null) {
                        messageContext.setResponse(msg);
                        return false;
                    }
                }
            }
        }

        if (message.startsWith("!ep")) {
            Matcher m = epRequest.matcher(message);
            if (m.matches()) {
                String ep = m.group(1);
                if (ep != null) {
                    String msg = getEpisodeQuote(ep.toLowerCase());
                    if (msg != null) {
                        messageContext.setResponse(msg);
                        return false;
                    }
                }
            }
        }

        if (message.startsWith("blacksmoke")) {
            message = message.substring(10);
            message = message.replaceAll(":", "");
            String msg = getRandomQuote(message);
            messageContext.setResponse(msg);
            return false;
        }

        return true;
    }

    public String getQuote(String username) {
        if (username == null) return null;

        String quote = null;
        try {
            Map<String, Object> row = simpleJdbcTemplate.queryForMap(
                    "select episode, person, line from lost where person = ? and length(line) > 10 order by rand() limit 1", username);
            quote = String.format("%s, %s: %s", row.get("episode"), capFirstLetter(row.get("person").toString()), row.get("line"));
        } catch (Throwable t) {
            quote = null;
        }
        return quote;
    }

    public String getEpisodeQuote(String episodeName) {
        if (episodeName == null) return null;

        String quote = null;
        try {
            Map<String, Object> row = simpleJdbcTemplate.queryForMap(
                    "select episode, person, line from lost where episode = ? and length(line) > 10 order by rand() limit 1", episodeName);
            quote = String.format("%s, %s: %s", row.get("episode"), capFirstLetter(row.get("person").toString()), row.get("line"));
        } catch (Throwable t) {
            quote = null;
        }
        return quote;
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
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.simpleJdbcTemplate = new SimpleJdbcTemplate(jdbcTemplate);
    }

    @Override
    public void doFirstInit() {
        //To change body of implemented methods use File | Settings | File Templates.
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
