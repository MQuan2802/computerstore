package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "orderitems")
public class orderitem {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int itemid;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private order order;

    @ManyToOne
    @JoinColumn(name = "prod_id", referencedColumnName = "prod_id")
    private products products;

    @Column(name = "quantity")
    private int quantity;

    public void setquantiy(int quantity) {
        this.quantity = quantity;
    }

    public int getquantity() {
        return this.quantity;
    }

    public void setorder(order order) {
        this.order = order;
    }

    public order getorder() {
        return this.order;
    }

    public void setitemid(int itemid) {
        this.itemid = itemid;
    }

    public int getitemid() {
        return this.itemid;
    }

    public void setproducts(products products) {
        this.products = products;
    }

    public products getproducts() {
        return this.products;
    }

}
