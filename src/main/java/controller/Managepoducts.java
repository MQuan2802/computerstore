package controller;

import model.Customer;
import model.order;
import model.orderitem;
import model.products;

import java.util.*;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

public class Managepoducts {
    private static SessionFactory factory;

    public static List<products> getproductslist() {

        try {
            factory = new AnnotationConfiguration().configure().addAnnotatedClass(products.class)
                    .addAnnotatedClass(orderitem.class)
                    .addAnnotatedClass(order.class)
                    .addAnnotatedClass(Customer.class)
                    .buildSessionFactory();

        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
        Session session = factory.openSession();
        Transaction tx = null;
        List<products> rturn = new ArrayList<>();
        try {
            tx = session.beginTransaction();
            String hql = "FROM products";
            Query query = session.createQuery(hql);
            rturn = query.list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null)
                tx.rollback();
            rturn = null;
            e.printStackTrace();
        } finally {
            session.close();
        }
        return rturn;

    }
}
