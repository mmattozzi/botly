package org.restlesscode;

import org.apache.commons.dbcp.BasicDataSource;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.restlesscode.admin.AdminServlet;
import org.restlesscode.bots.Blacksmoke;
import org.restlesscode.bots.Livelock;
import org.restlesscode.bots.Sonofcim;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Botly {

    protected List<Bot> bots = new ArrayList<Bot>();
    protected JdbcTemplate jdbcTemplate;
    protected Properties properties;

    public static void main(String args[]) throws Exception {
        Botly botly = new Botly();

        if (args.length > 0) {
            botly.loadConfig(args[0]);
        } else {
            botly.loadDefaultConfig();
        }

        botly.startAdminServlet();
        botly.setupDatasource();
        botly.loadBots();
        botly.startBots();
    }

    protected void loadConfig(String configFile) {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(configFile));
        } catch (IOException e) {
            e.printStackTrace();
            loadDefaultConfig();
        }
    }

    protected void loadDefaultConfig() {
        properties = new Properties();
        properties.setProperty("dbUrl", "jdbc:derby:botlyDB;create=true");
    }

    public Botly() {
        bots.add(new Sonofcim());
        // bots.add(new Livelock());
    }

    public void startAdminServlet() throws Exception {
        Server server = new Server(Integer.parseInt(properties.getProperty("adminPort", "9090")));
        Context adminRoot = new Context(server, "/admin", Context.SESSIONS);
        ServletHolder adminHolder = new ServletHolder();
        adminHolder.setName("AdminServlet");
        adminHolder.setInitParameter("contextConfigLocation", "classpath:/WEB-INF/admin-spring.xml");
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        adminHolder.setServlet(dispatcherServlet);
        adminHolder.setDisplayName("AdminServlet");
        adminRoot.addServlet(adminHolder, "/*");
        server.start();
        
        AdminServlet adminServlet = (AdminServlet) dispatcherServlet.getWebApplicationContext().getBean("adminServlet");
        adminServlet.setBotly(this);
    }

    public void setupDatasource() {
        BasicDataSource dataSource = new BasicDataSource();
        String dbUrl = properties.getProperty("dbUrl", "jdbc:derby:botlyDB;create=true");
        if (dbUrl.startsWith("jdbc:mysql")) {
            dataSource.setDriverClassName(properties.getProperty("dbDriver", "com.mysql.jdbc.Driver"));
            dataSource.setUsername(properties.getProperty("dbUser"));
            dataSource.setPassword(properties.getProperty("dbPassword"));
        } else if (dbUrl.startsWith("jdbc:derby")) {
            dataSource.setDriverClassName(properties.getProperty("dbDriver", "org.apache.derby.jdbc.EmbeddedDriver"));
        }
        dataSource.setUrl(dbUrl);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Bot> getBots() {
        return this.bots;
    }

    public void loadBots() {
        for (Bot bot : bots) {
            bot.getMessageHandlerChain().setJdbcTemplate(jdbcTemplate);
            bot.getMessageHandlerChain().initListeners();
        }
    }

    public void startBots() {
        for (Bot bot : bots) {
            bot.getRunner().start();
        }
    }
}

