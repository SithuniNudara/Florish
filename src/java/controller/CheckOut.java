package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.Cart;
import hibernate.City;
import hibernate.DeliveryType;
import hibernate.HibernateUtil;
import hibernate.Mobile;
import hibernate.OrderItems;
import hibernate.OrderStatus;
import hibernate.Orders;
import hibernate.Product;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.PayHere;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "CheckOut", urlPatterns = {"/CheckOut"})
public class CheckOut extends HttpServlet {

    private static final int SELECTOR_DEFAULT_VALUE = 0;
    private static final int ORDER_PENDING = 5;
    private static final int WITHIN_COLOMBO = 1;
    private static final int OUT_OF_COLOMBO = 2;
    private static final int RATING_DEFAULT_VALUE = 0;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        JsonObject requJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        boolean isCurrentAddress = requJsonObject.get("isCurrentAddress").getAsBoolean();
        String firstName = requJsonObject.get("firstName").getAsString();
        String lastName = requJsonObject.get("lastName").getAsString();
        String citySelect = requJsonObject.get("citySelect").getAsString();
        String lineOne = requJsonObject.get("lineOne").getAsString();
        String lineTwo = requJsonObject.get("lineTwo").getAsString();
        String postalCode = requJsonObject.get("postalCode").getAsString();
        String mobile = requJsonObject.get("mobile").getAsString();

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();
        Transaction tr = s.beginTransaction();

        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            responseObject.addProperty("message", "Session expired! Please log in again");
        } else {

            if (isCurrentAddress) {
                Criteria c1 = s.createCriteria(Address.class);
                c1.add(Restrictions.eq("user", user));
                c1.addOrder(Order.desc("id"));
                if (c1.list().isEmpty()) {
                    responseObject.addProperty("message",
                            "You current address is not found. Please add a new address");
                } else {
                    Address address = (Address) c1.list().get(0);
                    processCheckout(s, tr, user, address, responseObject);
                }
            } else {
                if (firstName.isEmpty()) {
                    responseObject.addProperty("message", "First Name is required.");
                } else if (lastName.isEmpty()) {
                    responseObject.addProperty("message", "Last Name is required.");
                } else if (!Util.isInteger(citySelect)) {
                    responseObject.addProperty("message", "Invalid city");
                } else if (Integer.parseInt(citySelect) == CheckOut.SELECTOR_DEFAULT_VALUE) {
                    responseObject.addProperty("message", "Invalid city");
                } else {
                    City city = (City) s.get(City.class, Integer.valueOf(citySelect));
                    if (city == null) {
                        responseObject.addProperty("message", "Invalid city name");
                    } else {
                        if (lineOne.isEmpty()) {
                            responseObject.addProperty("message", "Address line one is required");
                        } else if (lineTwo.isEmpty()) {
                            responseObject.addProperty("message", "Address line two is required");
                        } else if (postalCode.isEmpty()) {
                            responseObject.addProperty("message", "Your postal code is required");
                        } else if (!Util.isCodeValid(postalCode)) {
                            responseObject.addProperty("message", "Invalid postal code number");
                        } else if (mobile.isEmpty()) {
                            responseObject.addProperty("message", "Mobile number is required");
                        } else if (!Util.isValidPhone(mobile)) {
                            responseObject.addProperty("message", "Invalid mobile number");
                        } else {
                            Address address = new Address();
                            address.setFirstName(firstName);
                            address.setLastName(lastName);
                            address.setLine1(lineOne);
                            address.setLine2(lineTwo);
                            address.setCity(city);
                            address.setPostalCode(postalCode);
                            address.setRecieverMobile(mobile);
                            address.setUser(user);
                            s.save(address);

                            processCheckout(s, tr, user, address, responseObject);
                        }
                    }
                }
            }
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }

    private void processCheckout(Session s,
            Transaction tr,
            User user,
            Address address,
            JsonObject responseObject) {

        try {

            Criteria mobileCriteria = s.createCriteria(Mobile.class);
            mobileCriteria.add(Restrictions.eq("user", user));
            List<Mobile> mobileList = mobileCriteria.list();
            Mobile mobileNumber = mobileList.get(0);

            if (mobileList.isEmpty()) {
                responseObject.addProperty("message", "Mobile number not found for the user.");
                return;
            } else {
                Orders orders = new Orders();
                orders.setAddress(address);
                orders.setCreatedAt(new Date());
                orders.setUser(user);
                orders.setMobile(mobileNumber);

                int orderId = (int) s.save(orders);

                Criteria c1 = s.createCriteria(Cart.class);
                c1.add(Restrictions.eq("user", user));
                List<Cart> cartList = c1.list();

                OrderStatus orderStatus = (OrderStatus) s.get(OrderStatus.class, CheckOut.ORDER_PENDING);
                DeliveryType withInColombo = (DeliveryType) s.get(DeliveryType.class, CheckOut.WITHIN_COLOMBO);
                DeliveryType outOfColombo = (DeliveryType) s.get(DeliveryType.class, CheckOut.OUT_OF_COLOMBO);

                double amount = 0;
                String items = "";

                for (Cart cart : cartList) {
                    amount += cart.getQty() * cart.getProduct().getPrice();

                    OrderItems orderItems = new OrderItems();

                    if (address.getCity().getName().equalsIgnoreCase("Colombo")) { // within colombo
                        amount += cart.getQty() * withInColombo.getPrice();
                        orderItems.setDeliveryType_id(withInColombo);
                    } else {// out of colombo
                        amount += cart.getQty() * outOfColombo.getPrice();
                        orderItems.setDeliveryType_id(outOfColombo);
                    }
                    items += cart.getProduct().getTitle() + " x " + cart.getQty() + ", ";

                    Product product = cart.getProduct();
                    orderItems.setOrderStatus(orderStatus);
                    orderItems.setOrders(orders);
                    orderItems.setProduct(product);
                    orderItems.setQty(cart.getQty());
                    orderItems.setRating(CheckOut.RATING_DEFAULT_VALUE); // 0

                    s.save(orderItems);

                    //update product qty
                    product.setQty(product.getQty() - cart.getQty());
                    s.update(product);

                    // delete cart item
                    s.delete(cart);
                }

                tr.commit();

                //PayHere process
                String merahantID = "1222243";
                String merchantSecret = "MjMyMzIwMTk5MzMzMzkyNzY3OTEzNjc4NjIxODgyMTQzODA1ODUwMQ==";
                String orderID = "#000" + orderId;
                String currency = "LKR";
                String formattedAmount = new DecimalFormat("0.00").format(amount);
                String merchantSecretMD5 = PayHere.generatedMD5(merchantSecret);

                String hash = PayHere.generatedMD5(merahantID + orderID + formattedAmount + currency + merchantSecretMD5);

                JsonObject payHereJson = new JsonObject();
                payHereJson.addProperty("sandbox", true);
                payHereJson.addProperty("merchant_id", merahantID);

                payHereJson.addProperty("return_url", "");
                payHereJson.addProperty("cancel_url", "");
                payHereJson.addProperty("notify_url", "https://55954bec92ee.ngrok-free.app/Smarttrade/VerifyPayments");

                payHereJson.addProperty("order_id", orderID);
                payHereJson.addProperty("items", items);
                payHereJson.addProperty("amount", formattedAmount);
                payHereJson.addProperty("currency", currency);
                payHereJson.addProperty("hash", hash);

                payHereJson.addProperty("first_name", user.getFirstName());
                payHereJson.addProperty("last_name", user.getLastName());
                payHereJson.addProperty("email", user.getEmail());

                payHereJson.addProperty("phone", address.getRecieverMobile());
                payHereJson.addProperty("address", address.getLine1() + ", " + address.getLine2());
                payHereJson.addProperty("city", address.getCity().getName());
                payHereJson.addProperty("country", "Sri Lanka");

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Checkout completed");
                responseObject.add("payhereJson", new Gson().toJsonTree(payHereJson));
            }

        } catch (Exception e) {
            tr.rollback();
        }
    }

}
