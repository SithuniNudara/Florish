package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Address;
import hibernate.City;
import hibernate.HibernateUtil;
import hibernate.Mobile;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet(name = "UserAddressSave", urlPatterns = {"/UserAddressSave"})
public class UserAddressSave extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        JsonObject data = gson.fromJson(request.getReader(), JsonObject.class);

        String fname = data.get("fname").getAsString();
        String lname = data.get("lname").getAsString();
        String line1 = data.get("line1").getAsString();
        String line2 = data.get("line2").getAsString();
        String cid = data.get("cid").getAsString();
        String postalCode = data.get("postalCode").getAsString();
        String mobilenum = data.get("mobile").getAsString();

        if (fname.isEmpty()) {
            responseObject.addProperty("message", "Receiver's Name is Empty");
        } else if (line1.isEmpty()) {
            responseObject.addProperty("message", "Address Line One is Empty");
        } else if (line2.isEmpty()) {
            responseObject.addProperty("message", "Address Line Two is Empty");
        } else if (cid.isEmpty() && cid.equals("0") || cid.equals(null)) {
            responseObject.addProperty("message", "Please Select City");
        } else if (postalCode.isEmpty()) {
            responseObject.addProperty("message", "Please Type Postal Code");
        } else if (mobilenum.isEmpty()) {
            responseObject.addProperty("message", "Please Enter Your Mobile Number");
        } else if (!Util.isValidPhone(mobilenum)) {
            responseObject.addProperty("message", "Please Enter Valid Mobile Number");
        } else {
            HttpSession user = request.getSession(false);

            if (user == null && user.getAttribute("user") == null) {
                responseObject.addProperty("message", "Please Login");
            } else {

                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session s = sf.openSession();

                User SessUser = (User) user.getAttribute("user");
                int id = SessUser.getId();

                Criteria userCriteria = s.createCriteria(User.class);
                userCriteria.add(Restrictions.eq("id", Integer.valueOf(id)));

                if (userCriteria.list().isEmpty()) {
                    responseObject.addProperty("message", "Invaild User");
                } else {
                    User dbUser = (User) userCriteria.uniqueResult();

                    Criteria addressCriteria = s.createCriteria(Address.class);
                    addressCriteria.add(Restrictions.eq("user", SessUser));
                    Address addressObject = (Address) addressCriteria.uniqueResult();

                    Criteria cityCriteria = s.createCriteria(City.class);
                    cityCriteria.add(Restrictions.eq("id", Integer.valueOf(cid)));
                    City cityObject = (City) cityCriteria.uniqueResult();

                    if (cityObject == null) {
                        responseObject.addProperty("message", "Invalid City");
                    } else {

                        if (addressObject == null) {
                            addressObject = new Address();
                            addressObject.setLine1(line1);
                            addressObject.setLine2(line2);
                            addressObject.setPostalCode(postalCode);
                            addressObject.setCity(cityObject);
                            addressObject.setRecieverMobile(mobilenum);
                            addressObject.setUser(dbUser);
                            addressObject.setFirstName(fname);
                            addressObject.setLastName(lname);

                            s.merge(addressObject);
                            s.beginTransaction().commit();

                            responseObject.addProperty("message", "Address Saved Successfully");
                            responseObject.addProperty("status", true);

                        } else {
                            addressObject.setLine1(line1);
                            addressObject.setLine2(line2);
                            addressObject.setPostalCode(postalCode);
                            addressObject.setCity(cityObject);
                            addressObject.setRecieverMobile(mobilenum);
                            addressObject.setUser(dbUser);
                            addressObject.setFirstName(fname);
                            addressObject.setLastName(lname);

                            s.merge(addressObject);
                            s.beginTransaction().commit();

                            responseObject.addProperty("message", "Address Saved Successfully");
                            responseObject.addProperty("status", true);
                        }
                    }
                }

                s.close();

            }
        }

        String tojson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(tojson);

    }

}
