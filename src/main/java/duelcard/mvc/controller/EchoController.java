package duelcard.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class EchoController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String sayHello(ModelMap model) {
        model.addAttribute("websocket_url", "ws://localhost:8080/ws");
        return "echo";
    }
}

