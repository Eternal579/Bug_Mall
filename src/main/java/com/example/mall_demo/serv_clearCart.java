package com.example.mall_demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet("/clearCart")
public class serv_clearCart extends HttpServlet {
    static final String DB_URL = "jdbc:mysql://localhost:3306/bug_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "bugeater";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("content-type", "text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String ori_Email = (String) request.getSession().getAttribute("loggedUser");
        String hash_Email = serv_addToCart.hashEmail(ori_Email);

        boolean tableExists = false;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT * FROM " + hash_Email;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            tableExists = true;
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            // 如果发生异常，说明表不存在
        }
        if(tableExists){
            try {
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                String sql = "DROP TABLE " + hash_Email;
                PreparedStatement statement = connection.prepareStatement(sql);

                // 执行删除购物车表的语句
                statement.executeUpdate();

                // 关闭连接和释放资源
                statement.close();
                connection.close();
                response.sendRedirect("cart.jsp");
            } catch (SQLException e) {
                e.printStackTrace();
                // 返回错误响应
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("清空数据库错误");
            }
        }
        else{
            response.sendRedirect("cart.jsp");
        }
    }
}