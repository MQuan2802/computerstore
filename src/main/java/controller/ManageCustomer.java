package controller;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

import model.*;

public class ManageCustomer {

    private static SessionFactory factory;

    public static String createCustomer(Customer customer) {
        try {
            factory = new AnnotationConfiguration().configure().addAnnotatedClass(Customer.class).buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }

        Session session = factory.openSession();
        Transaction tx = null;
        String userID = null;
        try {
            tx = session.beginTransaction();
            userID = (String) session.save(customer);
            ;
            session.getTransaction().commit();

        } catch (HibernateException e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return userID;
    }

}
