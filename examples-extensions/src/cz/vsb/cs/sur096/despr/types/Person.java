
package cz.vsb.cs.sur096.despr.types;

public class Person {
    
    private String name;
    private String lastname;
    private ESex sex;
    private int age;
    private Contact contact;

    public Person() { }
    
    public Person(String name, String lastname, ESex sex, int age, Contact contact) {
        this.name = name;
        this.lastname = lastname;
        this.sex = sex;
        this.age = age;
        this.contact = contact;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public ESex getSex() { return sex; }
    public void setSex(ESex sex) { this.sex = sex; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public Contact getContact() { return contact; }
    public void setContact(Contact contact) { this.contact = contact; }
    
    @Override
    public String toString() {
        return String.format("(%s, %s, %d)", name, lastname, age);
    }
}