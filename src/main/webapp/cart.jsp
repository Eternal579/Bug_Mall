<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>购物车</title>
    <link rel="stylesheet" href="./styles/css_index.css">
</head>
<body>
<h2>购物车商品展示</h2>
<form action="clearCart" method="post">
    <button type="submit">清空购物车</button>
</form>
<div id="show"></div>
<script>
    fetch('showCarts')
        .then(response => response.text())
        .then(data => {
            document.getElementById('show').innerHTML = data;
        });
</script>
</body>
</html>
