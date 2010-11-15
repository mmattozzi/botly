package org.restlesscode.admin;

import org.restlesscode.Botly;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminServlet {

    protected Botly botly;

    public void setBotly(Botly botly) {
        this.botly = botly;
    }

    public void init() {

    }

    @RequestMapping(value="/", method= RequestMethod.GET)
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @RequestMapping(value="/bots", method = RequestMethod.GET)
    public ModelAndView bots() {
        ModelMap modelMap = new ModelMap();
        modelMap.put("bots", botly.getBots());
        return new ModelAndView("bots", modelMap);
    }

}
