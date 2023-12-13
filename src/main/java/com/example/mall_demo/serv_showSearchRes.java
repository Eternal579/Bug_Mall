package com.example.mall_demo;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Base64;

@WebServlet("/searchResults")
public class serv_showSearchRes extends HttpServlet {
    // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    static final String DB_URL = "jdbc:mysql://localhost:3306/bug_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "bugeater";
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("content-type", "text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String searchItem = request.getParameter("search");

        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "SELECT * FROM products WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, searchItem);
            ResultSet resultSet = statement.executeQuery();

            resultSet.next();
            StringBuilder htmlBuilder = new StringBuilder();
            int prod_id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            double price = resultSet.getDouble("price");
            int stock = resultSet.getInt("stock");
            String description = resultSet.getString("description");
            byte[] imageBytes = resultSet.getBytes("image");
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            htmlBuilder.append("<div class=\"product\">");
            htmlBuilder.append("<img src=\"data:image/png;base64,").append(base64Image).append("\" alt=\"").append(name).append("\">");
            htmlBuilder.append("<h3>").append(name).append("</h3>");
            htmlBuilder.append("<p>价格: $").append(price).append("</p>");
            htmlBuilder.append("<p>数量: ").append(stock).append("</p>");
            htmlBuilder.append("<form action=\"buyNow\" method=\"post\">");
            htmlBuilder.append("<input type=\"hidden\" name=\"productId\" value=\"").append(prod_id).append("\">");
            htmlBuilder.append("<input type=\"hidden\" name=\"timestamp\" id=\"timestampInput\" value=\"\">");
            htmlBuilder.append("<button id=\"buyButton\" type=\"submit\">立即购买</button>");
            htmlBuilder.append("</form>");
            htmlBuilder.append("</div>");

            htmlBuilder.append("<script>");
            htmlBuilder.append("var buyButton = document.querySelector('#buyButton');");
            htmlBuilder.append("buyButton.addEventListener('click', function() {");
            htmlBuilder.append("var currentTime = new Date().getTime();");
            htmlBuilder.append("var timestampInput = document.querySelector('#timestampInput');");
            htmlBuilder.append("timestampInput.value = currentTime;");
            htmlBuilder.append("});");
            htmlBuilder.append("</script>");

            statement.close();
            connection.close();

            // 设置响应的内容类型为HTML
            response.setContentType("text/html");
            // 将生成的HTML片段写入响应
            PrintWriter out = response.getWriter();
            out.println("<html><head><title>搜索结果</title>\n" +
                    "    <link rel=\"stylesheet\" href=\"./styles/css_index.css\"></head><body>");
            out.println(htmlBuilder.toString());
            out.println("</body></html>");
        } catch (SQLException e) {
            e.printStackTrace();
            // 返回错误响应
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("从数据库中检索商品时发生错误");
        }
    }
}