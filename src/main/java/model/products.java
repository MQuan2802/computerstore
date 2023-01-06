package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "products")
public class products {
    @Id
    @Column(name = "prod_id")
    private String prodid;

    @Column(name = "name")
    private String nameonorder;

    @Column(name = "groupname")
    private String groupname;

    @Column(name = "Warranty")
    private int warranty;

    @Column(name = "brand")
    private String brand;

    @Column(name = "price")
    private long price;

    @Column(name = "quantity")
    private int quantity;

    @OneToMany(mappedBy = "products", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<orderitem> items = new HashSet<>();

    public void setitems(Set<orderitem> items) {
        this.items = items;
    }

    public Set<orderitem> getitems() {
        return this.items;
    }

    public void setprodid(String prodid) {
        this.prodid = prodid;
    }

    public String getprodid() {
        return this.prodid;
    }

    public void setnameonorder(String nameonorder) {
        this.nameonorder = nameonorder;
    }

    public String getnameonorder() {
        return this.nameonorder;
    }

    public void setgroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getgroupname() {
        return this.getgroupname();
    }

    public void setwarranty(int warranty) {
        this.warranty = warranty;
    }

    public int getwarranty() {
        return this.warranty;
    }

    public void setbrand(String brand) {
        this.brand = brand;
    }

    public String getbrand() {
        return this.brand;
    }

    public void setprice(int price) {
        this.price = price;
    }

    public long getprice() {
        return this.price;
    }

    public void setquantity(int quantity) {
        this.quantity = quantity;
    }

    public int getquantity() {
        return this.quantity;
    }
}
