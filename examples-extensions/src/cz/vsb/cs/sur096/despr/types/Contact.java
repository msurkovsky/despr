
package cz.vsb.cs.sur096.despr.types;

public class Contact {
    
    private String email;
    private String phoneNumber;

    public Contact() { }
    public Contact(String email, String phoneNumber) {
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmail() { return email;}
    public void setEmail(String email) { this.email = email;}
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}