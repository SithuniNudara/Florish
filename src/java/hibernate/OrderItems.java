package hibernate;

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
@Table(name="order_items")
public class OrderItems  implements  Serializable{
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name="orders_id")
    private Orders orders;
    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;
    @Column(name="qty")
    private int qty;
    @ManyToOne
    @JoinColumn(name="order_status_id")
    private OrderStatus orderStatus;
    @ManyToOne
    @JoinColumn(name="delivery_type_id")
    private DeliveryType deliveryType_id;
    @Column(name="rating")
    private int rating;
    @Column(name="rating_comment")
    private String  comment;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public DeliveryType getDeliveryType_id() {
        return deliveryType_id;
    }

    public void setDeliveryType_id(DeliveryType deliveryType_id) {
        this.deliveryType_id = deliveryType_id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
