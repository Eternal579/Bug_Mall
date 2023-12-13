package com.example.mall_demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;

@WebServlet("/emailConfirm")
public class serv_emailConfirm extends HttpServlet {
    static final String DB_URL = "jdbc:mysql://localhost:3306/bug_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "bugeater";
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("content-type", "text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();

        String tableName = (String) session.getAttribute("tableName");
        String productId = (String) session.getAttribute("productId");
        String quantity = (String) session.getAttribute("quantity");
        String right_email = (String) session.getAttribute("loggedUser");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // 检查邮箱和密码是否满足您的条件，如果满足则执行添加到订单表的操作
        if (email.equals(right_email) && isOK(password, right_email)) {
            addToUserOrderTable(tableName, productId, quantity);
            System.out.println("order completed!");

            EmailSender.sendEmail(right_email,"Confirm", "Your order just confirmed!");

            if(!isTableExists("allOrders")){
                try {
                    Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    System.out.println(tableName);
                    String createTableSql = "CREATE TABLE " + "allOrders" + " (id INT, times INT) ";
                    PreparedStatement stmt = conn.prepareStatement(createTableSql);
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                // 查询购物车中该商品的数量
                String selectQuantitySql = "SELECT * FROM " + "allOrders" + " WHERE id = ?";
                PreparedStatement selectStmt = conn.prepareStatement(selectQuantitySql);
                selectStmt.setString(1, productId);
                ResultSet resultSet = selectStmt.executeQuery();

                if (resultSet.next()) {
                    int times = resultSet.getInt("times");
                    times += Integer.parseInt(quantity);
                    String updateQuantitySql = "UPDATE " + "allOrders" + " SET times = ? WHERE id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuantitySql);
                    updateStmt.setInt(1, times);
                    updateStmt.setString(2, productId);
                    updateStmt.executeUpdate();
                    updateStmt.close();
                }else{
                    int times = Integer.parseInt(quantity);
                    String updateQuantitySql = "INSERT " + "allOrders" + "(id, times) VALUES(?, ?)";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuantitySql);
                    updateStmt.setInt(1, Integer.parseInt(productId));
                    updateStmt.setInt(2, times);
                    updateStmt.executeUpdate();
                    updateStmt.close();
                }

                resultSet.close();
                selectStmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                String query = "SELECT * FROM products WHERE id = ?";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, productId);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                int stock = resultSet.getInt("stock");

                String updateQuantitySql = "UPDATE " + "products" + " SET stock = ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuantitySql);
                updateStmt.setInt(1, stock - Integer.parseInt(quantity));
                updateStmt.setString(2, productId);
                updateStmt.executeUpdate();
                updateStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            response.sendRedirect("order.jsp"); // 重定向到订单完成页面
        } else {
            // 邮箱或密码不满足条件，显示错误消息或跳转回确认页面
            response.sendRedirect("confirm.jsp");
        }
    }

    private boolean isOK(String password, String right_email) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?")) {
            statement.setString(1, right_email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 检查表是否存在
    private boolean isTableExists(String tableName) {
        boolean tableExists = false;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT * FROM " + tableName;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            tableExists = true;
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            // 如果发生异常，说明表不存在
        }
        return tableExists;
    }

    private void addToUserOrderTable(String tableName, String productId, String quantity) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String insertDataSql = "INSERT INTO " + tableName + " (id, quantity, orderTime) VALUES (?, ?, NOW())";
            PreparedStatement insertStmt = conn.prepareStatement(insertDataSql);
            insertStmt.setString(1, productId);
            insertStmt.setString(2, quantity);
            insertStmt.executeUpdate();
            insertStmt.close();

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
