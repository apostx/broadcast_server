package com.ras.broadcastserver.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class BroadcastController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String sayHello(HttpServletRequest req) {
        HttpSession session = req.getSession();
        session.setAttribute("session_id", session.getId());
        return "broadcast";
    }
}

