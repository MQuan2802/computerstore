package controller;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import controller.userloginmanage.MyUserDetail;
import model.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.hibernate.Hibernate;

@Controller()
@RequestMapping("/")
public class homecontroller {
    @GetMapping
    @RequestMapping("/getindex")
    // @ResponseBody
    public String getindex(HttpServletResponse response, Model model) {
        List<products> products = Managepoducts.getproductslist();
        String rturn = "";
        String form;
        response.addHeader("content-type", "text/html; charset=utf-8");
        for (products product : products) {
            form = "<form action=\"/computerstore/order/createDBorder\" method=\"post\">" +
                    "<p>" + product.getnameonorder() + "</p>" +
                    "<input type=\"hidden\" name=\"prodid\" value=\"" + product.getprodid() + "\">" +
                    "<input type=\"submit\" value=\""
                    + "đặt hàng"// new String("đặt hàng".getBytes(), StandardCharsets.UTF_8)
                    + "\">" +
                    "</form>";
            rturn += form;
        }
        model.addAttribute("result", rturn);
        return "index";

    }

    @GetMapping("/logincomputerstore")
    public String login() {
        return "redirect:/getindex";

    }

}
