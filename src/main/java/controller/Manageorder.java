package controller;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.springframework.jdbc.object.SqlQuery;

import java.util.*;
import model.Customer;
import model.order;
import model.orderitem;
import model.products;

public class Manageorder {
    private static SessionFactory factory;

    public static int createorder(String customerID, String prodid) {
        try {
            factory = new AnnotationConfiguration().configure()
                    .addAnnotatedClass(Customer.class)
                    .addAnnotatedClass(orderitem.class)
                    .addAnnotatedClass(order.class)
                    .addAnnotatedClass(products.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            return 0;
        }

        Session session = factory.openSession();
        Transaction tx = null;
        int orderID = 0;
        try {
            tx = session.beginTransaction();
            String hql = "SELECT E FROM order E inner join E.customer D WHERE D.id=:ID AND E.statuscode=:CODE";
            Query query = session.createQuery(hql);
            query.setParameter("ID", customerID);
            query.setParameter("CODE", 2);
            Customer customer = (Customer) session.get(Customer.class, customerID);
            orderitem item = new orderitem();
            System.out.println(prodid);
            products product = (products) session.get(products.class, prodid);
            item.setproducts(product);
            List<order> orders = query.list();
            System.out.println(orders.size());
            if (orders.size() == 0) {
                order order = new order();
                order.setcustomer(customer);
                Set<orderitem> items = new HashSet<>();
                items.add(item);
                order.setitems(items);
                order.setstatuscode(2);
                item.setorder(order);
                item.setquantiy(1);
                session.save(item);
                orderID = (int) session.save(order);
            } else if (orders.size() > 0) {

                for (orderitem orderitem : orders.get(0).getorderitem()) {
                    if (orderitem.getproducts().getprodid() == prodid) {
                        orderitem.setquantiy(orderitem.getquantity() + 1);
                        break;
                    }
                }
                item.setorder(orders.get(0));
                // session.save(item);
                session.update(orders.get(0));
                orderID = orders.get(0).getorderid();
            }
            session.getTransaction().commit();

        } catch (HibernateException e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return orderID;
    }

    public static boolean UpdateItem(int orderID, String prodid, int quantity) {
        try {
            factory = new AnnotationConfiguration().configure()
                    .addAnnotatedClass(Customer.class)
                    .addAnnotatedClass(orderitem.class)
                    .addAnnotatedClass(order.class)
                    .addAnnotatedClass(products.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            return false;
        }

        Session session = factory.openSession();
        Transaction tx = null;
        // int orderID = 0;
        try {
            tx = session.beginTransaction();
            String hql = "SELECT E FROM orderitem E inner join E.order D inner join E.products F WHERE D.id=:ID AND D.statuscode=:CODE AND F.prodid=:prodID";
            Query query = session.createQuery(hql);
            query.setParameter("ID", orderID);
            query.setParameter("CODE", 2);
            query.setParameter("prodID", prodid);
            List<orderitem> items = query.list();
            System.out.println(items.size());
            if (items.size() == 0) {
                return false;
            } else if (items.size() > 0) {
                items.get(0).setquantiy(quantity);
                session.update(items.get(0));
                ;
            }
            session.getTransaction().commit();
            return true;
        } catch (HibernateException e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }

    }

    public static order getcart(String cust_id) {
        try {
            factory = new AnnotationConfiguration().configure()
                    .addAnnotatedClass(Customer.class)
                    .addAnnotatedClass(orderitem.class)
                    .addAnnotatedClass(order.class)
                    .addAnnotatedClass(products.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            return null;
        }

        Session session = factory.openSession();
        Transaction tx = null;
        // int orderID = 0;
        try {
            tx = session.beginTransaction();
            String hql = " SELECT E from order E inner join E.customer D  WHERE D.id=:ID AND E.statuscode=:CODE";
            Query query = session.createQuery(hql);
            query.setParameter("ID", cust_id);
            query.setParameter("CODE", 2);
            List<order> orders = query.list();
            session.getTransaction().commit();
            if (orders.size() > 0) {
                orders.get(0).getorderitem().size();
                return orders.get(0);
            } else

                return null;

        } catch (HibernateException e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }

    }

    public static order getcart(int orderID) {
        try {
            factory = new AnnotationConfiguration().configure()
                    .addAnnotatedClass(Customer.class)
                    .addAnnotatedClass(orderitem.class)
                    .addAnnotatedClass(order.class)
                    .addAnnotatedClass(products.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            return null;
        }

        Session session = factory.openSession();
        Transaction tx = null;
        // int orderID = 0;
        try {
            tx = session.beginTransaction();
            String hql = "from order E WHERE E.orderid=:ID AND E.statuscode=:CODE";
            Query query = session.createQuery(hql);
            query.setParameter("ID", orderID);
            query.setParameter("CODE", 2);
            List<order> orders = query.list();
            session.getTransaction().commit();
            if (orders.size() > 0) {
                orders.get(0).getorderitem().size();
                return orders.get(0);
            } else

                return null;

        } catch (HibernateException e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    public static boolean deleteitem(int orderid, String prodid) {
        try {
            factory = new AnnotationConfiguration().configure()
                    .addAnnotatedClass(Customer.class)
                    .addAnnotatedClass(orderitem.class)
                    .addAnnotatedClass(order.class)
                    .addAnnotatedClass(products.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            return false;
        }

        Session session = factory.openSession();
        Transaction tx = null;
        // int orderID = 0;
        try {
            tx = session.beginTransaction();
            String sqldeleteitem = "DELETE FROM orderitems WHERE order_id=" + orderid + " AND prod_id='" + prodid + "'";
            String hqlselectorder = "FROM order E WHERE E.orderid=:ID";
            String sqldeleteorder = "DELETE FROM shoporder WHERE order_id='" + orderid + "'";
            Query selectorderquery = session.createQuery(hqlselectorder);
            selectorderquery.setParameter("ID", orderid);
            SQLQuery deleteitemquery = session.createSQLQuery(sqldeleteitem);
            SQLQuery deleteorderquery = session.createSQLQuery(sqldeleteorder);
            int result = deleteitemquery.executeUpdate();
            List<order> orders = selectorderquery.list();
            if (orders.size() != 0) {
                if (orders.get(0).getorderitem().size() == 0)
                    deleteorderquery.executeUpdate();
            }
            tx.commit();
            return result == 1;
        } catch (HibernateException e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    public static boolean isPaid(int OrderID) throws Exception {
        try {
            factory = new AnnotationConfiguration().configure()
                    .addAnnotatedClass(Customer.class)
                    .addAnnotatedClass(orderitem.class)
                    .addAnnotatedClass(order.class)
                    .addAnnotatedClass(products.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw ex;
        }

        Session session = factory.openSession();
        Transaction tx = null;
        // int orderID = 0;
        try {
            tx = session.beginTransaction();
            String hql = "FROM order E WHERE E.orderid=:ID AND E.statuscode=1";
            Query query = session.createQuery(hql);
            query.setParameter("ID", OrderID);
            List<order> orders = query.list();
            if (orders.size() == 0) {
                return false;
            } else if (orders.size() == 1) {
                return true;
            } else
                throw new Exception("duplicate orderid");
        } catch (HibernateException e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
    }

    public static boolean UpdateOrderStatus(int orderID, int prestatus, int newstatus) {
        try {
            factory = new AnnotationConfiguration().configure()
                    .addAnnotatedClass(Customer.class)
                    .addAnnotatedClass(orderitem.class)
                    .addAnnotatedClass(order.class)
                    .addAnnotatedClass(products.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            return false;
        }

        Session session = factory.openSession();
        Transaction tx = null;
        // int orderID = 0;
        try {
            tx = session.beginTransaction();
            String hql = "FROM order E WHERE E.orderid=:ID AND E.statuscode=:CODE";
            Query query = session.createQuery(hql);
            query.setParameter("ID", orderID);
            query.setParameter("CODE", prestatus);

            List<order> orders = query.list();

            if (orders.size() == 0) {
                return false;
            } else if (orders.size() == 1) {
                orders.get(0).setstatuscode(newstatus);
                ;
                session.update(orders.get(0));
                ;
            }
            session.getTransaction().commit();
            return true;
        } catch (HibernateException e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }
}
