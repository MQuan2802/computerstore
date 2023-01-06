package model;

public class Customerwithconfirmpassword extends Customer {

    private String confirmpassword;

    public void setconfirmpassword(String confirmpassword) {
        this.confirmpassword = confirmpassword;
    }

    public String getconfirmpassword() {
        return this.confirmpassword;
    }
}
