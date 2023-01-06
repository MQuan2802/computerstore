package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

import java.util.Date;
import java.util.Set;

@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@DiscriminatorColumn(name = "authority")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected String id;

    @Column(name = "password")
    protected String password;

    @Column(name = "authority")
    protected String authority = "customer";

    @Column(name = "email")
    protected String email;

    @Column(name = "verify")
    protected boolean verify;

    public void setverify(boolean verify) {
        this.verify = verify;
    }

    public boolean getverify() {
        return this.verify;
    }

    public void hashpassword(PasswordEncoder passwordencoder) {
        this.password = passwordencoder.encode(this.password);
    }

    public void setemail(String mail) {
        this.email = mail;
    }

    public String getemail() {
        return this.email;
    }

    public void setid(String id) {
        this.id = id;
    }

    public String getid() {
        return this.id;
    }

    public void setauthority(String authority) {
        this.authority = authority;
    }

    public String getauthority() {
        return this.authority;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
