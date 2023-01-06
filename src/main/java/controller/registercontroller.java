package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import controller.jwt.Tokenmanager;
import controller.userloginmanage.MyUserDetail;

import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import model.*;

@Controller
@RequestMapping("/register")
public class registercontroller {
    @Autowired
    private Tokenmanager tokenManager;

    @Autowired
    private PasswordEncoder passwordencoder;

    @Autowired
    @Qualifier("CustomerValidator")
    private CustomerValidator validator;

    @InitBinder("user")
    public void customizeBinding(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @GetMapping("/registerform")
    public String getregisterform(Model model) {
        model.addAttribute("registerpath", "/computerstore/register/submitregister");
        return "register";
    }

    @RequestMapping(value = "/submitregister", method = RequestMethod.POST)
    public String register(@Valid @ModelAttribute("user") Customerwithconfirmpassword customer,
            BindingResult bindingResult,
            Model model, HttpServletRequest req) {
        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("firstname"))
                model.addAttribute("firstname", bindingResult.getFieldError("firstname").getCode());
            if (bindingResult.hasFieldErrors("lastname"))
                model.addAttribute("lastname", bindingResult.getFieldError("lastname").getCode());
            if (bindingResult.hasFieldErrors("phone"))
                model.addAttribute("phone", bindingResult.getFieldError("phone").getCode());
            if (bindingResult.hasFieldErrors("email"))
                model.addAttribute("email", bindingResult.getFieldError("email").getCode());
            if (bindingResult.hasFieldErrors("password"))
                model.addAttribute("password", bindingResult.getFieldError("password").getCode());
            if (bindingResult.hasFieldErrors("confirmpassword"))
                model.addAttribute("confirmpassword", bindingResult.getFieldError("confirmpassword").getCode());
            model.addAttribute("registerpath", "/computerstore/register/submitregister");
            return "register";
        } else {

            Customer customerwithoutconfirmpassword = new Customer(customer);
            customerwithoutconfirmpassword.hashpassword(passwordencoder);
            ManageCustomer.createCustomer(customerwithoutconfirmpassword);
            final MyUserDetail userDetails = new MyUserDetail(customer);
            final String token = tokenManager.generateJwtToken(userDetails);
            mail mail = new mail();
            String mess = "<!DOCTYPE html>\n"
                    + "<html>\n"
                    + "<head>\n"
                    + "<meta charset=\"UTF-8\">\n"
                    + "<title>INIT</title>\n"
                    + "</head>\n"
                    + "<body>\n"
                    + "<h3>Verification</h3>\n"
                    + "<form action=\"http://localhost:8080/computerstore/register/verification?token="
                    + URLEncoder.encode(token, StandardCharsets.UTF_8)
                    + "\" method=\"GET\" accept-charset=\"utf-8\">"
                    + "<input type=\"submit\" value=\"verify\">"
                    + "</form>"
                    + "</body>"
                    + "</html>";
            mail.send(mess, customerwithoutconfirmpassword.getemail());
            model.addAttribute("result", "an email had been sent to verify");
            return "register";

        }
    }

    @GetMapping("/verification")
    @ResponseBody
    public String verification(HttpServletRequest req, @RequestParam("token") String token) {
        String email = tokenManager.getEmailFromToken(token);
        if (!ManageUser.verifyuser(email))
            return "fail to verification";
        HttpSession session = req.getSession();
        session.invalidate();
        return "successful verification";
    }

}
