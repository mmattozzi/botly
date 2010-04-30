package org.restlesscode;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.restlesscode.irc.IrcBot;
import org.restlesscode.irc.IrcBotRunner;
import org.springframework.web.servlet.DispatcherServlet;

public class Botly {

    public static void main(String args[]) throws Exception {
        Botly botly = new Botly();
        botly.startAdminServlet();
        botly.startBots();
    }

    public Botly() {
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
    
    public void startBots() {
        IrcBot sonofcim = new IrcBot();
        sonofcim.setNick("sonofcim");
        new IrcBotRunner(sonofcim, "irc.freenode.net", "milhouse").start();
    }
}

