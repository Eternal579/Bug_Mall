<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>注册</title>
    <link rel="stylesheet" href="./styles/css_login_register.css">
</head>
<body>
<div class="container">
    <h2>注册</h2>
    <form action="register" method="POST">
        <input type="email" name="email" placeholder="邮箱" required>
        <input type="password" name="password" placeholder="密码" required>
        <button type="submit">注册</button>
    </form>
</div>
</body>
</html>