package cloudify.widget.website.controllers;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/23/14
 * Time: 11:59 AM
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class MonitorController {

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public
    @ResponseBody
    String index() {
        return "hello world";
    }

}
