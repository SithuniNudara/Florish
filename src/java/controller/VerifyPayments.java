package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.PayHere;

@WebServlet(name = "VerifyPayments", urlPatterns = {"/VerifyPayments"})
public class VerifyPayments extends HttpServlet {

    private static final int PAYHERE_SUCCESS = 2;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String merchant_id = request.getParameter("merchant_id");
        String order_id = request.getParameter("order_id");
        String payhere_amount = request.getParameter("payhere_amount");
        String payhere_currency = request.getParameter("payhere_currency");
        String status_code = request.getParameter("status_code");
        String md5sig = request.getParameter("md5sig");

        String merchantSecret = "MjMyMzIwMTk5MzMzMzkyNzY3OTEzNjc4NjIxODgyMTQzODA1ODUwMQ==";
        String merchantSecretMD5 = PayHere.generatedMD5(merchantSecret);
        String hash = PayHere.generatedMD5(merchant_id + order_id + payhere_amount + payhere_currency + merchantSecretMD5);

        if (md5sig.equals(hash) && Integer.parseInt(status_code) == VerifyPayments.PAYHERE_SUCCESS) {
            System.out.println("Payment Completed. Order Id:" + order_id);
            String orderId = order_id.substring(3);
            System.out.println(orderId); // 1
        }
    }

}
