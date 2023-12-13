<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>修改商品</title>
</head>
<body>
<h1>修改商品</h1>

<form action="addPros" id="productForm" enctype="multipart/form-data" method="POST">
    <label for="image">商品图片:</label>
    <input type="file" id="image" name="image" accept="image/*" required><br><br>

    <label for="name">商品名称:</label>
    <input type="text" id="name" name="name" required><br><br>

    <label for="price">商品价格:</label>
    <input type="number" id="price" name="price" required><br><br>

    <label for="stock">商品数目:</label>
    <input type="number" id="stock" name="stock" required><br><br>

    <label for="description">商品介绍:</label>
    <textarea id="description" name="description" rows="4" required></textarea><br><br>

    <button type="submit">添加商品</button>
</form>
</body>
</html>