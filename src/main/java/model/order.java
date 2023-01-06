package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "shoporder")
public class order {
    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int orderid;

    @Column(name = "statuscode")
    private int statuscode;

    @ManyToOne
    @JoinColumn(name = "cust_id", referencedColumnName = "id")
    Customer customer = new Customer();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<orderitem> items = new HashSet<>();

    public void setcustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer getcustomer() {
        return this.customer;
    }

    public void setitems(Set<orderitem> items) {
        this.items = items;
    }

    public Set<orderitem> getorderitem() {
        return this.items;
    }

    public void setorderid(int orderid) {
        this.orderid = orderid;
    }

    public int getorderid() {
        return this.orderid;
    }

    public void setstatuscode(int statuscode) {
        this.statuscode = statuscode;
    }

    public int getstatuscode() {
        return this.statuscode;
    }
}
