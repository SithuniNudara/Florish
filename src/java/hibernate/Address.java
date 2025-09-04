package hibernate;


import hibernate.City;
import hibernate.User;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "address")
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "line1", nullable = false)
    private String line1;

    @Column(name = "line2", nullable = false)
    private String line2;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @Column(name = "postalcode", length = 5, nullable = false)
    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name="receiver_mobile", length = 10, nullable = false)
    private String recieverMobile;
    
    @Column(name="first_name",length = 20)
    private String firstName;
    
    @Column(name="last_name",length = 20)
    private String lastName;

    public Address() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPostalCode() {
        return postalCode;
    }


    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }


    public String getRecieverMobile() {
        return recieverMobile;
    }

    public void setRecieverMobile(String recieverMobile) {
        this.recieverMobile = recieverMobile;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

   
}