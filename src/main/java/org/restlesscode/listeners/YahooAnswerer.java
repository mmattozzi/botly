package org.restlesscode.listeners;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.restlesscode.MessageContext;
import org.restlesscode.MessageListenerAdapter;
import org.restlesscode.util.SimpleNamespaceContext;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class YahooAnswerer extends MessageListenerAdapter {

    protected String yahooId = null;
    protected String resourcePath = "http://answers.yahooapis.com/AnswersService/V1/questionSearch";
    protected XPathExpression xpathExpression = null;
    protected HashSet<String> stopwords = new HashSet<String>();

    @Override
    public boolean handleMessage(MessageContext messageContext) {
        String response = null;
        if (messageContext.wasDirectlyAddressed()) {
            String message = messageContext.getMessage();
            message = message.replace(messageContext.getBotName(), "");
            response = getAnswer(message);

            if (response != null) {
                messageContext.setResponse(response);
                return false;
            }
        }

        return true;
    }

    public String getYahooId() {
        return yahooId;
    }

    public void setYahooId(String yahooId) {
        this.yahooId = yahooId;
    }

    @Override
    public void doInit() {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        xpath.setNamespaceContext(new SimpleNamespaceContext("yans", "urn:yahoo:answers"));

        try {
            xpathExpression = xpath.compile("//yans:ChosenAnswer");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        HttpClient httpClient = new HttpClient();

        try {
            GetMethod get = new GetMethod("http://cloud.github.com/downloads/mmattozzi/sonofcim/stopwords.txt");
            httpClient.executeMethod(get);
            loadWords(stopwords, get.getResponseBodyAsStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    public String getAnswer(String question) {
        try {
            question = question.replaceAll("[,|':]", "");
            String answer = queryAnswersAPI(question);
            if (answer == null) System.out.println("Couldn't find direct answer to question");

            if (answer == null) {
                answer = queryAnswersAPI(getKeywords(question, 2));
            }

            return answer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String getKeywords(String question, int num) {
        String keywordString = "";
        String[] words = question.split(" ");
        for (int i = 0; i < num; i++)
            keywordString += " " + popLongestWord(words);
        return keywordString;
    }

    protected String popLongestWord(String[] words) {
        int bestLength = 0;
        String bestWord = "";
        int bestIndex = 0;
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > bestLength && !stopwords.contains(words[i].toLowerCase())) {
                bestLength = words[i].length();
                bestWord = words[i];
                bestIndex = i;
            }
        }
        words[bestIndex] = "";
        return bestWord;
    }

    protected String queryAnswersAPI(String question) throws MalformedURLException,
            UnsupportedEncodingException, ParserConfigurationException,
            SAXException, IOException, XPathExpressionException {
        System.out.println("Checking for yahoo answer to: [" + question + "]");

        URL url = new URL(resourcePath + "?appid=" + yahooId +
                "&type=resolved&query=" + URLEncoder.encode(question, "utf-8"));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(url.openStream());

        NodeList nodeSet = (NodeList) xpathExpression.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nodeSet.getLength(); i++) {
            String chosenAnswer = nodeSet.item(i).getTextContent();
            if (chosenAnswer != null && !chosenAnswer.equals("")) {
                chosenAnswer = trimAnswer(chosenAnswer);
                return chosenAnswer;
            }
        }
        return null;
    }

    protected String trimAnswer(String chosenAnswer) {
        if (chosenAnswer.length() < 400) {
            return chosenAnswer;
        } else {
            String trimmedAnswer = chosenAnswer.substring(0, 400);
            int lastPeriod = trimmedAnswer.lastIndexOf('.');
            return trimmedAnswer.substring(0, lastPeriod);
        }
    }

    public static void loadWords(Collection<String> wordSet, InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            while (line != null) {
                wordSet.add(line.toLowerCase());
                line = reader.readLine();
            }
            System.out.println("MoraleScale: Added " + wordSet.size() + " words.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
