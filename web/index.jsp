<%--
  Created by IntelliJ IDEA.
  User: 84813
  Date: 2026/1/12
  Time: 下午1:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>教务管理系统 - 首页</title>
  <!-- 引入自定义样式（放在static/css目录下） -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/index.css">
</head>
<body>
<!-- 页面容器：居中布局，简洁美观 -->
<div class="container">
  <!-- 系统标题区域 -->
  <div class="title-box">
    <h1>教务管理系统</h1>
    <p>基于JavaWeb的课程设计实现</p>
  </div>

  <!-- 跳转提示区域 -->
  <div class="jump-box">
    <p>系统将在 <span id="countDown">3</span> 秒后自动跳转到登录页面...</p>
    <p>如果没有自动跳转，请点击下方链接：</p>
    <a href="${pageContext.request.contextPath}/pages/login/login.jsp" class="login-btn">立即登录</a>
  </div>

  <!-- 底部版权信息 -->
  <div class="footer">
    <p>© 2026 教务管理系统 - JavaWeb课程设计</p>
  </div>
</div>

<!-- 自动跳转JS逻辑 -->
<script>
  // 倒计时跳转逻辑
  let count = 3; // 倒计时秒数
  const countDownEle = document.getElementById("countDown");

  // 定时器：每秒更新倒计时
  const timer = setInterval(() => {
    count--;
    countDownEle.innerText = count;
    // 倒计时结束，跳转登录页
    if (count <= 0) {
      clearInterval(timer);
      window.location.href = "${pageContext.request.contextPath}/pages/login/login.jsp";
    }
  }, 1000);
</script>
</body>
</html>
