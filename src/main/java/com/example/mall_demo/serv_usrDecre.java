package com.example.mall_demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.sql.*;

@WebServlet("/usrDecre")
public class serv_usrDecre extends HttpServlet {
    // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    static final String DB_URL = "jdbc:mysql://localhost:3306/bug_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "bugeater";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("content-type", "text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        String ori_Email = (String) session.getAttribute("loggedUser");
        String hash_Email = serv_addToCart.hashEmail(ori_Email);
        String prod_Id = request.getParameter("productId");

        System.out.println("usrDecre servlet");

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // 查询购物车中该商品的数量
            String selectQuantitySql = "SELECT quantity FROM " + hash_Email + " WHERE id = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectQuantitySql);
            selectStmt.setString(1, prod_Id);
            ResultSet resultSet = selectStmt.executeQuery();

            int quantity = 0;
            if (resultSet.next()) {
                // 如果购物车中已存在该商品，则获取当前数量并减一
                System.out.println(quantity);
                quantity = resultSet.getInt("quantity");
                quantity--;
                System.out.println(quantity);

                // 更新购物车中该商品的数量，如果数量减为0，则从数据表中删除该商品
                if (quantity == 0) {
                    String deleteProductSql = "DELETE FROM " + hash_Email + " WHERE id = ?";
                    PreparedStatement deleteStmt = conn.prepareStatement(deleteProductSql);
                    deleteStmt.setString(1, prod_Id);
                    deleteStmt.executeUpdate();
                    deleteStmt.close();
                } else {
                    String updateQuantitySql = "UPDATE " + hash_Email + " SET quantity = ? WHERE id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuantitySql);
                    updateStmt.setInt(1, quantity);
                    updateStmt.setString(2, prod_Id);
                    updateStmt.executeUpdate();
                    updateStmt.close();
                }
            }

            resultSet.close();
            selectStmt.close();
            conn.close();
            response.sendRedirect("cart.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
