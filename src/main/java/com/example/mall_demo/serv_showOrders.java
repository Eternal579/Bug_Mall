package com.example.mall_demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/showOrders")
public class serv_showOrders extends HttpServlet {
    static final String DB_URL = "jdbc:mysql://localhost:3306/bug_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "bugeater";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("loggedUser");

        if (email == null) {
            response.sendRedirect("login.jsp");
        } else {
            try {
                // 建立数据库连接
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                String tableName = "order_" + getEmailPrefix(email);
                String query = "SELECT o.id, o.quantity, o.orderTime, p.image, p.name FROM " + tableName + " o " +
                        "JOIN products p ON o.id = p.id ORDER BY o.orderTime DESC";
                // 执行查询
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                // 使用HTML构建器构造结果表格
                StringBuilder htmlBuilder = new StringBuilder();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    int quantity = rs.getInt("quantity");
                    String orderTime = rs.getString("orderTime");
                    byte[] imageBytes = rs.getBytes("image");
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    String name = rs.getString("name");

                    htmlBuilder.append("<div class=\"product\">");
                    htmlBuilder.append("<img src=\"data:image/png;base64,").append(base64Image).append("\" alt=\"").append(name).append("\">");
                    htmlBuilder.append("<h3>").append(name).append("</h3>");
                    htmlBuilder.append("<p>数量: ").append(quantity).append("</p>");
                    htmlBuilder.append("<p>订单时间: ").append(orderTime).append("</p>");
                    htmlBuilder.append("</div>");
                }

                // 输出结果到前端
                out.println(htmlBuilder.toString());

                // 关闭数据库连接
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                out.println("<h2>目前还没有订单</h2>");
            }
        }

        out.close();
    }

    private String getEmailPrefix(String email) {
        if (email != null && email.contains("@")) {
            int atIndex = email.indexOf("@");
            return email.substring(0, atIndex);
        }
        return null;
    }
}