package hibernate;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="delivery_type")
public class DeliveryType implements Serializable{
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="name", length = 20, nullable = false)
    private String name;
    @Column(name="price", nullable = false)
    private double price;

    public int getId() {
        return id;
    }

 
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

 
    public double getPrice() {
        return price;
    }


    public void setPrice(double price) {
        this.price = price;
    }
    
}
