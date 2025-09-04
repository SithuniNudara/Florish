package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.Mobile;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "MyCart", urlPatterns = {"/MyCart"})
public class MyCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();

        HttpSession session = request.getSession(false);
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        if (session != null) {
            User user = (User) session.getAttribute("user");

            if (user != null) {
                // Logged-in user's cart
                Criteria cartCriteria = s.createCriteria(Cart.class);
                cartCriteria.add(Restrictions.eq("user", user));
                List<Cart> cartList = cartCriteria.list();

                if (cartList.isEmpty()) {
                    responseObject.addProperty("message", "No Items in the Cart");
                } else {
                    for (Cart cart : cartList) {
                        cart.setUser(null); 
                    }

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Cart items Successfully Loaded!");
                    responseObject.addProperty("itemsCount", cartList.size());
                    responseObject.add("cartItems", gson.toJsonTree(cartList));
                }

            } else {
                // Guest session cart
                ArrayList<Cart> sessionList = (ArrayList<Cart>) session.getAttribute("sessionCart");

                if (sessionList == null || sessionList.isEmpty()) {
                    responseObject.addProperty("message", "No Items in the Cart");
                } else {
                    for (Cart cart : sessionList) {
                        cart.setUser(null); // avoid sending user data
                    }

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Cart items Successfully Loaded!");
                    responseObject.addProperty("itemsCount", sessionList.size());
                    responseObject.add("cartItems", gson.toJsonTree(sessionList));
                }
            }
        } else {
            responseObject.addProperty("message", "Session not available.");
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }

}
