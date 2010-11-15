package org.restlesscode.listeners;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mortbay.util.ajax.JSON;
import org.restlesscode.MessageContext;
import org.restlesscode.MessageListenerAdapter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mmattozzi
 * Date: Jun 4, 2010
 * Time: 10:18:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class LivelockAnswerer extends MessageListenerAdapter {

    protected String apiUser = "mmattozzi";
    protected String apiPassword = ".Candidat3";
    protected String livelockHost = "http://antonym.subterfusion.net/api/public";

    protected HttpClient httpClient = new HttpClient();
    protected boolean authSet = false;

    public String getApiUser() {
        return apiUser;
    }

    public void setApiUser(String apiUser) {
        this.apiUser = apiUser;
    }

    public String getApiPassword() {
        return apiPassword;
    }

    public void setApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    @Override
    public boolean handleMessage(MessageContext messageContext) {

        if (messageContext.getMessage().startsWith("livelock")) {
            String strippedMessage = messageContext.getMessage().substring(8);
            if (strippedMessage.startsWith(":")) {
                strippedMessage = strippedMessage.substring(1);
            }

            if (! authSet) {
                try {
                    URL url = new URL(livelockHost);
                    // httpClient.getParams().setAuthenticationPreemptive(true);
                    Credentials defaultcreds = new UsernamePasswordCredentials(apiUser, apiPassword);
                    httpClient.getState().setCredentials(AuthScope.ANY, defaultcreds);
                    List authPrefs = new ArrayList(1);
                    authPrefs.add(AuthPolicy.DIGEST);
                    httpClient.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
                    authSet = true;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            String q = livelockHost + "/mixtures?q=" + URLEncoder.encode(strippedMessage);
            System.out.println("Querying url: " + q);
            GetMethod getMethod = new GetMethod(q);
            try {
                int result = httpClient.executeMethod(getMethod);
                System.out.println("Result from livelock = " + result);
                if (result == 200) {
                    Map responseMap = (Map) JSON.parse(new InputStreamReader(getMethod.getResponseBodyAsStream()), false);
                    messageContext.setResponse((String) responseMap.get("body"));
                } else {
                    System.out.println(getMethod.getResponseBodyAsString());
                    messageContext.setResponse("Livelock threw a " + result);
                }
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                messageContext.setResponse(e.getMessage());
                return false;
            }
        }

        return true;
    }
}
