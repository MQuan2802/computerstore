package controller;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import model.User;
import model.Customer;
import model.Customerwithconfirmpassword;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.internet.*;
import java.util.Calendar;
import org.hibernate.SessionFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;

import java.io.UnsupportedEncodingException;
import java.util.*;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;

public class CustomerValidator implements Validator {

    private static SessionFactory factory;

    @Override
    public boolean supports(Class<?> paramClass) {
        return Customerwithconfirmpassword.class.equals(paramClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "null email");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "null firstname");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "null lastname");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "null password");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmpassword", "null confirm password");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "null phone");
        rejectEmail(obj, errors);
        rejectfirstname(obj, errors);
        rejectlastname(obj, errors);
        rejectconfirmpassword(obj, errors);
        rejectpassword(obj, errors);
        rejectphone(obj, errors);
    }

    public static void rejectpassword(Object obj, Errors errors) {
        Customer customer = (Customer) obj;
        String pass = customer.getPassword();
        System.out.println(pass);
        Pattern pattern1 = Pattern
                .compile("^[A-Za-z0-9\\^\\-\\]\\.\\$\\*\\+\\?\\(\\)\\[\\{\\}\\|\\_\\/\\~`@%&:\"';,\\\\]+$");
        Pattern pattern2 = Pattern
                .compile("[\\^\\-\\]\\.\\$\\*\\+\\?\\(\\)\\[\\]\\{\\}\\|\\_\\/\\~`@%&:\"';,\\\\]{1,}");
        Pattern pattern3 = Pattern.compile("[A-Z]+");
        Pattern pattern4 = Pattern.compile("[0-9]+");
        int newpasssize = pass.length();
        Matcher matcher1 = pattern1.matcher(pass);
        Matcher matcher2 = pattern2.matcher(pass);
        Matcher matcher3 = pattern3.matcher(pass);
        Matcher matcher4 = pattern4.matcher(pass);
        boolean matchFound1 = matcher1.find();
        boolean matchFound2 = matcher2.find();
        boolean matchFound3 = matcher3.find();
        boolean matchFound4 = matcher4.find();
        if (!matchFound1)
            errors.rejectValue("password", "only alphabet , numeric and special characters are allowed");
        if (!matchFound2)
            errors.rejectValue("password", "must contain at least 1 special character and uppercase");
        if (!matchFound3)
            errors.rejectValue("password", "must contain upper case");
        if (!matchFound4)
            errors.rejectValue("password", "must contain numeric");
        if (newpasssize < 8)
            errors.rejectValue("password", "new password must have 8 characters at least");

    }

    public static void rejectconfirmpassword(Object obj, Errors errors) {

        Customerwithconfirmpassword confirm = (Customerwithconfirmpassword) obj;
        if (!(confirm.getPassword().compareTo(confirm.getconfirmpassword()) == 0))
            errors.rejectValue("confirmpassword", "confirm password not match");

    }

    public static void rejectEmail(Object obj, Errors errors) {
        try {
            Customer customer = (Customer) obj;

            InternetAddress emailAddr = new InternetAddress(customer.getemail());
            emailAddr.validate();
            System.out.println(customer.getemail());
            rejectduplicate("email", obj, errors, "email", customer.getemail());

        } catch (

        Exception e) {
            errors.rejectValue("email", e.getMessage());
        }
    }

    public static void rejectfirstname(Object obj, Errors errors) {

        Customer customer = (Customer) obj;
        Pattern pattern = Pattern.compile(
                "^[\\x{41}-\\x{5a}\\x{61}-\\x{7a}\\x{20}\\x{c0}-\\x{1ef9}]{6,}$",
                Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = pattern.matcher(customer.getfirstname());
        boolean matchFound = matcher.find();
        System.out.println(matchFound);
        if (!matchFound) {
            errors.rejectValue("firstname", "insufficient first name");
        }

    }

    public static void rejectlastname(Object obj, Errors errors) {
        Customer customer = (Customer) obj;
        Pattern pattern = Pattern.compile(
                "^[\\x{41}-\\x{5a}\\x{61}-\\x{7a}\\x{20}\\x{c0}-\\x{1ef9}]{6,}$",
                Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = pattern.matcher(customer.getlastname());
        boolean matchFound = matcher.find();
        System.out.println(matchFound);
        if (!matchFound) {
            errors.rejectValue("lastname", "insufficient last name");
        }

    }

    public static void rejectphone(Object obj, Errors errors) {
        Customer customer = (Customer) obj;
        Pattern pattern = Pattern.compile("^0[98753][0-9]{8,10}$");
        Matcher matcher = pattern.matcher(customer.getphone());
        boolean matchFound = matcher.find();
        if (!matchFound) {
            errors.rejectValue("phone", "insufficient phone");
        } else {
            rejectduplicate("phone", obj, errors, "phone", customer.getphone());
        }
    }

    public static void rejectduplicate(String errorfield, Object obj, Errors errors, String duplicatefield,
            String userfieldvalue) {
        try {
            factory = new AnnotationConfiguration().configure().addAnnotatedClass(Customer.class).
            // addPackage("com.xyz") //add package if used.
                    buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
        Session session = factory.openSession();
        Transaction tx = null;
        User rturn = new User();
        try {
            tx = session.beginTransaction();
            String hql = "FROM Customer E WHERE E." + duplicatefield + " = :Value";
            Query query = session.createQuery(hql);
            query.setParameter("Value", userfieldvalue);
            List results = query.list();
            // List users = session.createQuery("FROM User").list();

            if (results.size() > 0) {
                errors.rejectValue(errorfield, errorfield + " duplicate");
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
