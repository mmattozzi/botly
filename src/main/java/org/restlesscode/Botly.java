package org.restlesscode;

import org.apache.commons.dbcp.BasicDataSource;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.restlesscode.bots.Blacksmoke;
import org.restlesscode.bots.Sonofcim;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.ArrayList;
import java.util.List;

public class Botly {

    protected List<Bot> bots = new ArrayList<Bot>();
    protected JdbcTemplate jdbcTemplate;

    public static void main(String args[]) throws Exception {
        Botly botly = new Botly();

        botly.startAdminServlet();
        botly.setupDatasource();
        botly.loadBots();
        botly.startBots();
    }

    public Botly() {
        bots.add(new Blacksmoke());
    }

    public void startAdminServlet() throws Exception {
        Server server = new Server(8080);
        Context adminRoot = new Context(server, "/admin", Context.SESSIONS);
        ServletHolder adminHolder = new ServletHolder();
        adminHolder.setName("AdminServlet");
        adminHolder.setInitParameter("contextConfigLocation", "classpath:/WEB-INF/admin-spring.xml");
        adminHolder.setServlet(new DispatcherServlet());
        adminHolder.setDisplayName("AdminServlet");
        adminRoot.addServlet(adminHolder, "/*");
        server.start();
    }

    public void setupDatasource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUsername("mike");
        dataSource.setPassword("");
        dataSource.setUrl("jdbc:mysql://192.168.0.150/irc");
        jdbcTemplate = new JdbcTemplate(dataSource);
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

