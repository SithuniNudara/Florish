package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String id = request.getParameter("prId");
        String qty = request.getParameter("qty");

        if (!Util.isInteger(id)) {
            responseObject.addProperty("message", "Invalid Product Id");
        } else if (!Util.isInteger(qty)) {
            responseObject.addProperty("message", "Invalid Product Quntity");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session s = sf.openSession();
            Transaction tr = s.beginTransaction();

            Product product = (Product) s.get(Product.class, Integer.valueOf(id));

            if (product == null) {
                responseObject.addProperty("message", "Product Not Found!");
            } else {
                HttpSession ses = request.getSession();
                User user = (User) ses.getAttribute("user");

                // Merge sessionCart into DB cart if user just logged in
                if (user != null && ses.getAttribute("sessionCart") != null) {
                    ArrayList<Cart> sessionCartList = (ArrayList<Cart>) ses.getAttribute("sessionCart");

                    for (Cart sessionCart : sessionCartList) {
                        Product cartProduct = sessionCart.getProduct();
                        int qtyToAdd = sessionCart.getQty();

                        Criteria dbCartCriteria = s.createCriteria(Cart.class);
                        dbCartCriteria.add(Restrictions.eq("user", user));
                        dbCartCriteria.add(Restrictions.eq("product", cartProduct));

                        Cart existingDbCart = (Cart) dbCartCriteria.uniqueResult();

                        if (existingDbCart != null) {
                            int updatedQty = existingDbCart.getQty() + qtyToAdd;
                            if (updatedQty <= cartProduct.getQty()) {
                                existingDbCart.setQty(updatedQty);
                                s.update(existingDbCart);
                            }
                        } else {
                            if (qtyToAdd <= cartProduct.getQty()) {
                                Cart newDbCart = new Cart();
                                newDbCart.setUser(user);
                                newDbCart.setProduct(cartProduct);
                                newDbCart.setQty(qtyToAdd);
                                s.save(newDbCart);
                            }
                        }
                    }
                    ses.removeAttribute("sessionCart");
                }

                if (user != null) {
                    Criteria c1 = s.createCriteria(Cart.class);
                    c1.add(Restrictions.eq("user", user));
                    c1.add(Restrictions.eq("product", product));

                    if (c1.list().isEmpty()) {
                        if (Integer.parseInt(qty) <= product.getQty()) {
                            Cart cart = new Cart();
                            cart.setQty(Integer.parseInt(qty));
                            cart.setUser(user);
                            cart.setProduct(product);

                            s.save(cart);
                            tr.commit();

                            responseObject.addProperty("status", true);
                            responseObject.addProperty("message", "Product Addedd Successfully!");
                        } else {
                            responseObject.addProperty("message", "OOPS!  Insufficient Product Qunatity");
                        }
                    } else {
                        Cart cart = (Cart) c1.uniqueResult();
                        int newQTY = cart.getQty() + Integer.parseInt(qty);

                        if (newQTY <= product.getQty()) {
                            cart.setQty(newQTY);
                            s.update(cart);
                            tr.commit();
                            responseObject.addProperty("status", true);
                            responseObject.addProperty("message", "Product Cart Updated Successfully!");
                        } else {
                            responseObject.addProperty("message", "OOPS!  Insufficient Product Qunatity");
                        }
                    }
                } else {
                    if (ses.getAttribute("sessionCart") == null) {
                        if (Integer.parseInt(qty) <= product.getQty()) {
                            ArrayList<Cart> sesCarts = new ArrayList();

                            Cart cart = new Cart();
                            cart.setQty(Integer.parseInt(qty));
                            cart.setProduct(product);
                            cart.setUser(null);

                            sesCarts.add(cart);
                            ses.setAttribute("sessionCart", sesCarts);

                            responseObject.addProperty("status", true);
                            responseObject.addProperty("message", "Product Added Successfully!");
                        } else {
                            responseObject.addProperty("message", "OOPS!  Insufficient Product Qunatity");
                        }
                    } else {
                        ArrayList<Cart> sessionList = (ArrayList<Cart>) ses.getAttribute("sessionCart");

                        Cart foundedCart = null;
                        for (Cart cart : sessionList) {
                            if (cart.getProduct().getId() == product.getId()) {
                                foundedCart = cart;
                                break;
                            }
                        }

                        if (foundedCart != null) {
                            int newQTY = foundedCart.getQty() + Integer.parseInt(qty);

                            if (newQTY <= product.getQty()) {
                                foundedCart.setQty(newQTY);
                                responseObject.addProperty("status", true);
                                responseObject.addProperty("message", "Product Cart Updated Successfully!");
                            } else {
                                responseObject.addProperty("message", "OOPS!  Insufficient Product Qunatity");
                            }
                        } else {
                            if (Integer.parseInt(qty) <= product.getQty()) {
                                foundedCart = new Cart();
                                foundedCart.setQty(Integer.parseInt(qty));
                                foundedCart.setUser(null);
                                foundedCart.setProduct(product);

                                sessionList.add(foundedCart);

                                responseObject.addProperty("status", true);
                                responseObject.addProperty("message", "Product  Added to the Cart Successfully!");
                            } else {
                                responseObject.addProperty("message", "OOPS!  Insufficient Product Qunatity");
                            }
                        }
                    }
                }
            }
            s.close();
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }

}
