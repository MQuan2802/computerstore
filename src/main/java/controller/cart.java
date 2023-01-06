package controller;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import controller.jwt.Tokenmanager;
import controller.userloginmanage.MyUserDetail;
import model.order;
import model.orderitem;

@Controller
@RequestMapping("/cart")
public class cart {
    @Autowired
    Tokenmanager tokenmanager;

    @GetMapping("")
    public String getcart(Model model, HttpServletResponse response) {
        response.addHeader("content-type", "text/html; charset=utf-8");
        String result = "";

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof MyUserDetail) {
            String customerID = ((MyUserDetail) principal).getid();
            order order = Manageorder.getcart(customerID);
            System.out.println(order);
            int quantityorder = 1;
            if (order != null && order.getorderitem().size() > 0) {
                result = "<form id=\"update\"action=\"/computerstore/cart/updateDBitem\" style=\"width:100%\" method=\"post\"></form>\n"
                        + "<table style=\"width:100%\">\n"
                        + "<tr>\n"
                        + "<th>sản phẩm</th>\n"
                        + "<th>tên sản phẩm</th>\n"
                        + "<th>số lượng</th>\n"
                        + "<th>Gía tiền</th>\n"
                        + "<th>Xóa</th>\n"
                        + "</tr>\n";
                for (orderitem item : order.getorderitem()) {
                    result += "<input type=\"hidden\" name=\"itemnum\" value=\""
                            + order.getorderitem().size() + "\" form=\"update\">\n"
                            + "<tr>\n"
                            + "<th>" + "<img src=\"/computerstore/resources/static/g102.png\" >" + "</th>\n"
                            + "<th>" + item.getproducts().getnameonorder() + "</th>\n"
                            + "<th>"
                            + "<input type=\"number\"  name=\"quantity" + quantityorder
                            + "\" min=\"1\" max=\"100\" value=\""
                            + item.getquantity() + "\" form=\"update\">\n"
                            + "<input type=\"hidden\" name=\"prodid" + quantityorder + "\" value=\""
                            + item.getproducts().getprodid() + "\" form=\"update\">\n"
                            + "<input type=\"hidden\" name=\"orderid" + quantityorder + "\" value=\""
                            + item.getorder().getorderid() + "\" form=\"update\">\n"
                            + "</th>\n"
                            + "<th>" + item.getproducts().getprice() + "</th>\n"
                            + "<th>"
                            + "<form action=\"/computerstore/cart/deleteDBitem\" method=\"post\">"
                            + "<input type=\"hidden\" name=\"orderid\" value=\""
                            + item.getorder().getorderid() + "\">\n"
                            + "<input type=\"hidden\" name=\"prodid\" value=\"" + item.getproducts().getprodid()
                            + "\">\n"
                            + "<input type=\"submit\" value=\"xóa\">\n"
                            + "</form>\n"
                            + "</th>\n"
                            + "</tr>\n";
                    quantityorder++;
                }
                result += "</table>\n"
                        + "<input type=\"submit\" value=\"Cập nhật\" form=\"update\">\n"
                        + "<form action=\"/computerstore/cart/checkout\" method=\"post\">\n"
                        + "<input type=\"hidden\" name=\"token\" value=\""
                        + tokenmanager.generateOrderToken(order.getorderid()) + "\">\n"
                        + "<input type=\"submit\" value=\"thanh toán\">\n"
                        + "</form>";

            }
        }
        model.addAttribute("cart", result);
        return "cart";
    }

    @PostMapping("/deleteDBitem")
    public String deleteitem(HttpServletRequest req) {
        int orderid = Integer.parseInt(req.getParameter("orderid"));
        String prodid = req.getParameter("prodid");
        Manageorder.deleteitem(orderid, prodid);
        return "redirect:/cart";
    }

    @PostMapping("/updateDBitem")
    public String updateitem(HttpServletRequest req, @RequestBody String reqbody) {
        System.out.println(reqbody);
        int itemnum = Integer.parseInt(req.getParameter("itemnum"));
        for (int i = 1; i <= itemnum; i++) {
            int quantity = Integer.parseInt(req.getParameter("quantity" + i));
            String prodid = req.getParameter("prodid" + i);
            int orderid = Integer.parseInt(req.getParameter("orderid" + i));
            Manageorder.UpdateItem(orderid, prodid, quantity);
        }

        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpServletRequest req, Model model) {
        String token = req.getParameter("token");
        int orderID = tokenmanager.getOrderIDFromToken(token);
        String checkout = "";
        if (orderID > 0) {
            Long total = Long.valueOf(0);
            order order = Manageorder.getcart(orderID);
            if (order != null) {
                Set<orderitem> items = order.getorderitem();
                System.out.println(items.size());
                for (orderitem item : items) {
                    total += item.getquantity() * item.getproducts().getprice();
                }
                checkout += total
                        + "\n<form action=\"/computerstore/order/zalopayorder\" method=\"post\">"
                        + "<input type=\"hidden\" name=\"token\" value=\""
                        + tokenmanager.generateOrderToken(order.getorderid()) + "\">\n"
                        + "<input type=\"submit\" value=\"zalopay\">\n"
                        + "</form>";
                model.addAttribute("total", checkout);
                return "checkout";
            }

        }
        return "failcheckout";
    }

}
