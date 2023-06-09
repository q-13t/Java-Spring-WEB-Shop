package com.test.learn.godbless.controllers;

import java.util.HashMap;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.test.learn.godbless.dao.UserDAO;
import com.test.learn.godbless.dao.FruitDAO;

@Controller
public class MainController {
    @Autowired
    private FruitDAO fruitDAO;
    @Autowired
    private UserDAO userDAO;

    @GetMapping(value = "/")
    public String getIndex(@ModelAttribute(value = "action") String action,
            @ModelAttribute(value = "offset") String off, Model model) {
        if (!userDAO.testConnection()) {
            return "redirect:/error/notConnected";
        }
        Integer offset = Integer.valueOf(off.isEmpty() ? "0" : off);
        switch (action) {
            case "Next": {
                offset += 10;
                break;
            }
            case "Previous": {
                if (offset - 10 < 0) {
                    offset = 0;
                } else {
                    offset -= 10;
                }
                break;
            }
        }

        model.addAttribute("fruits", fruitDAO.getTenFruitsByOffset(offset));
        model.addAttribute("offset", offset);

        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        if (user != "anonymousUser") {
            model.addAttribute("username", user);
        }
        return "index";
    }

    @PostMapping(value = "/")
    public String postIndex(@RequestParam("order") String order, Model model) {
        if (!userDAO.testConnection()) {
            return "redirect:/error/notConnected";
        }

        System.out.println(order);

        model.addAttribute("order",
                Stream.of(order.replaceAll("\\{|\\}", "").split(", ")).map(x -> x.split("=")).collect(
                        Collectors.toMap(x -> Integer.parseInt(x[0]), x -> Integer.parseInt(x[1]),
                                (x, y) -> x,
                                HashMap::new)));
        model.addAttribute("fruits", fruitDAO.getAllFruits());

        try {
            String user = userDAO.getCurrentUsername();
            if (user != "anonymousUser") {
                model.addAttribute("username", user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "index";
    }

}
