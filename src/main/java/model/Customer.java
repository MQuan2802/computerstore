package model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Table(name = "customer")
@Entity
@DiscriminatorValue("customer")
public class Customer extends User {
    @Column(name = "first_name")
    protected String firstname;

    @Column(name = "last_name")
    protected String lastname;

    @Column(name = "phone")
    protected String phone;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<order> orders = new HashSet<>();

    public void setorders(Set<order> orders) {
        this.orders = orders;
    }

    public Set<order> getorders() {
        return this.orders;
    }

    public Customer() {
        // default constructor
    }

    public Customer(Customerwithconfirmpassword Customerwithconfirmpassword) {
        this.email = Customerwithconfirmpassword.email;
        this.authority = Customerwithconfirmpassword.authority;
        this.firstname = Customerwithconfirmpassword.firstname;
        this.lastname = Customerwithconfirmpassword.lastname;
        this.phone = Customerwithconfirmpassword.phone;
        this.verify = Customerwithconfirmpassword.verify;
        this.password = Customerwithconfirmpassword.password;
        this.id = Customerwithconfirmpassword.id;
    }

    public void setfirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getfirstname() {
        return this.firstname;
    }

    public void setlastname(String lastname) {
        this.lastname = lastname;
    }

    public String getlastname() {
        return this.lastname;
    }

    public void setphone(String phone) {
        this.phone = phone;
    }

    public String getphone() {
        return this.phone;
    }
}
