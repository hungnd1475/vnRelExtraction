package vn.edu.hcmut.emrre.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index() {
        return new ModelAndView("fragments/home");
    }

    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public ModelAndView about() {
        return new ModelAndView("fragments/about");
    }

    @RequestMapping(value = "/vn-extractor", method = RequestMethod.GET)
    public ModelAndView extrator() {
        return new ModelAndView("fragments/extractor");
    }


    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        return "index";
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public
    @ResponseBody
    String error() {
        return new String("error");
    }
}
