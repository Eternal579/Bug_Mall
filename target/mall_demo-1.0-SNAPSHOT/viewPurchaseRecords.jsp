<%@ page import="java.sql.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>用户浏览记录</title>
    <style>
        table {
            border-collapse: collapse;
            width: 100%;
        }

        th, td {
            text-align: left;
            padding: 8px;
        }

        th {
            background-color: #f2f2f2;
        }
    </style>
</head>
<body>
<h1>用户购买记录</h1>

<%
    // 数据库连接信息
    String DB_URL = "jdbc:mysql://localhost:3306/bug_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    String DB_USER = "root";
    String DB_PASSWORD = "bugeater";

    // 建立数据库连接
    Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

    String tableName = "allOrders";
    String query = "SELECT o.id, o.times, p.name, p.description FROM " + tableName + " o " +
            "JOIN products p ON o.id = p.id";
    // 执行查询
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery(query);

%>
<table>
    <tr>
        <th>Name</th>
        <th>Description</th>
        <th>Times</th>
    </tr>
    <%
        while (rs.next()) {
            int times = rs.getInt("times");
            String name = rs.getString("name");
            String description = rs.getString("description");
    %>
    <tr>
        <td><%= name %></td>
        <td><%= description %></td>
        <td><%= times %></td>
    </tr>
    <%
        }
    %>
</table>
<%
    conn.close();
%>
</body>
</html>