/**
 * Created by IntelliJ IDEA
 * User: Uraka.Lee
 * Date: 2013-07-01
 *
 * Copyleft 2000 MCTLab.cn
 */
package cn.mctlab.archetype.war.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.mctlab.archetype.war.service.IServer;

/**
 *
 */
@Controller
@RequestMapping("/index")
public class IndexController {

    //-- public finals --//
    //-- private finals --//
    //-- properties --//

    @Autowired
    private IServer server;

    //-- constructors --//
    //-- destructors --//
    //-- implements --//
    //-- un-implements --//
    //-- methods --//

    public String test() {
        return "this is a test...";
    }

    @RequestMapping("/response")
    @ResponseBody
    public String response() throws Exception {
        return "Controller:" + server.response();
    }

    /**
     * /index/page
     */
    @RequestMapping("/page")
    public String page() throws Exception {
        return "index/page";
    }

    /**
     * /index/model
     */
    @RequestMapping("/model")
    public String model(Model model) throws Exception {
        model.addAttribute("msg", "This is a model!");
        return "index/model";
    }

    /**
     * /index/param?value=xyz
     */
    @RequestMapping("/param")
    public String param(Model model, @RequestParam("value") String value) throws Exception {
        model.addAttribute("value", value);
        return "index/param";
    }

    /**
     * /index/123/name/xyz
     */
    @RequestMapping("/{id}/name/{name}")
    public String idAndName(
            Model model, @PathVariable("id") long id, @PathVariable("name") String name) throws Exception {
        model.addAttribute("id", id);
        model.addAttribute("name", name);
        return "index/idAndName";
    }

    //-- functions --//
    //-- utils --//
    //-- getters & setters --//
    //-- inner classes --//
}
