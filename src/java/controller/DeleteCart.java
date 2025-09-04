package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "DeleteCart", urlPatterns = {"/DeleteCart"})
public class DeleteCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String productId = request.getParameter("id");
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        HttpSession sessionUser = request.getSession(false);

        if (sessionUser != null) {

            User user = (User) sessionUser.getAttribute("user");

            if (user != null) {

                Product product = (Product) s.get(Product.class, Integer.parseInt(productId));

                Criteria c1 = s.createCriteria(Cart.class);
                c1.add(Restrictions.eq("user", user));
                c1.add(Restrictions.eq("product", product));

                Cart cart = (Cart) c1.uniqueResult();

                if (cart == null) {
                    responseObject.addProperty("message", "Product not found in cart!");
                } else {
                    s.beginTransaction();
                    s.delete(cart);
                    s.getTransaction().commit();
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Product deleted successfully!");
                }

            } else {
                List<Cart> sessionCart = (List<Cart>) sessionUser.getAttribute("sessionCart");
//                for (Cart cart : sessionCart) {
//                    if (cart.getProduct().getId() == Integer.parseInt(productId)) {
//                        System.out.println(cart.getProduct().getTitle());
//                        System.out.println(cart.getQty());
//                    }
//                    
//                }
                Iterator<Cart> iterator = sessionCart.iterator();
                int pid = Integer.parseInt(productId);

                while (iterator.hasNext()) {
                    Cart cart = iterator.next();
                    if (cart.getProduct().getId() == pid) {
                        iterator.remove();
                    }
                }

                sessionUser.setAttribute("sessionCart", sessionCart);

                responseObject.addProperty("message", "Delete Item!");
                responseObject.addProperty("status", true);
            }

        } else {

            responseObject.addProperty("message", "Session not found.");

        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }

}
