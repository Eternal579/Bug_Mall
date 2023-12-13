<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>确认订单</title>
    <link rel="stylesheet" href="./styles/css_login_register.css">
</head>
<body>
<div class="container">
    <h2>确认订单</h2>
    <form action="emailConfirm" method="POST">
        <input type="email" name="email" placeholder="邮箱" required>
        <input type="password" name="password" placeholder="密码" required>
        <button type="submit">确认</button>
    </form>
</div>
</body>
</html>
