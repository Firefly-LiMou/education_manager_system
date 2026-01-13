package servlet;

import dao.SysUserDao;
import entity.SysUser;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 登录请求处理Servlet
 * 核心功能：接收登录表单参数、调用UserDao验证账号密码、处理多角色跳转、传递错误提示
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    // 重写doPost方法（适配login.jsp的POST提交方式）
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }


    // 兼容GET请求（防止用户直接访问LoginServlet的GET方式）
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // GET请求直接重定向到登录页
        response.sendRedirect(request.getContextPath() + "/pages/login/login.jsp");
    }
}