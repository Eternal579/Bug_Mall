package com.example.mall_demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

@WebServlet("/addToCart")
public class serv_addToCart extends HttpServlet {
    static final String DB_URL = "jdbc:mysql://localhost:3306/bug_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "bugeater";
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("content-type", "text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("loggedUser");
        if (email == null) {
            // 未登录，重定向到登录页面
            response.sendRedirect("login.jsp");
            return;
        }
        // 创建或获取用户数据表
        String tableName = getOrCreateUserTable(email);
        String productId = request.getParameter("productId");
        addToUserTable(tableName, productId);
        response.sendRedirect("index.jsp");
    }

    // 创建或获取用户数据表
    private String getOrCreateUserTable(String email) {
        String tableName = hashEmail(email); // 根据邮箱进行哈希映射得到表名
        // 检查表是否存在，如果不存在则创建表
        if (!isTableExists(tableName)) {
            createTable(tableName);
        }
        return tableName;
    }

    // 根据邮箱进行哈希映射
    public static String hashEmail(String email) {
        String tableName = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(email.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(Integer.toHexString(b & 0xff));
            }
            tableName = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return tableName;
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

    // 创建用户数据表
    private void createTable(String tableName) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String createTableSql = "CREATE TABLE " + tableName + " (id INT PRIMARY KEY, quantity INT)";
            PreparedStatement stmt = conn.prepareStatement(createTableSql);
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 将商品ID添加到用户数据表中
    private void addToUserTable(String tableName, String productId) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // 查询购物车中该商品的数量
            String selectQuantitySql = "SELECT quantity FROM " + tableName + " WHERE id = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectQuantitySql);
            selectStmt.setString(1, productId);
            ResultSet resultSet = selectStmt.executeQuery();

            int quantity = 0;
            if (resultSet.next()) {
                // 如果购物车中已存在该商品，则获取当前数量并加一
                quantity = resultSet.getInt("quantity");
                quantity++;

                // 更新购物车中该商品的数量
                String updateQuantitySql = "UPDATE " + tableName + " SET quantity = ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuantitySql);
                updateStmt.setInt(1, quantity);
                updateStmt.setString(2, productId);
                updateStmt.executeUpdate();
                updateStmt.close();
            } else {
                // 如果购物车中不存在该商品，则插入新记录，并将数量设置为1
                String insertDataSql = "INSERT INTO " + tableName + " (id, quantity) VALUES (?, 1)";
                PreparedStatement insertStmt = conn.prepareStatement(insertDataSql);
                insertStmt.setString(1, productId);
                insertStmt.executeUpdate();
                insertStmt.close();
            }

            resultSet.close();
            selectStmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
