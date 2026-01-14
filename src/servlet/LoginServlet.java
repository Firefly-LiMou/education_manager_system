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
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        // 1. 获取登录表单参数（EncodingFilter已统一设置UTF-8编码）
        String account = request.getParameter("userId");
        String rawPassword = request.getParameter("password");

        // 2. 非空校验
        if (account == null || account.trim().isEmpty() || rawPassword == null || rawPassword.trim().isEmpty()) {
            request.setAttribute("errorMsg", "登录失败：账户和密码不能为空！");
            request.getRequestDispatcher("/pages/login/login.jsp").forward(request, response);
            return;
        }
        SysUserDao sysUserDao = new SysUserDao();
        try {
            // 3. 调用DAO层登录验证方法
            SysUser loginUser = sysUserDao.login(account.trim(), rawPassword.trim());

            // 4. 验证结果处理
            if (loginUser != null) {
                // 登录成功：将用户信息存入Session，用于后续权限控制
                HttpSession session = request.getSession();
                session.setAttribute("loginUser", loginUser);
                session.setMaxInactiveInterval(3600); // 设置Session有效期1小时

                // 5. 根据用户角色跳转对应页面
                String role = loginUser.getRole();
                switch (role.toLowerCase()) {
                    case "admin":
                        // 管理员页面
                        response.sendRedirect(request.getContextPath() + "/pages/admin_page.jsp");
                        break;
                    case "teacher":
                        // 教师页面
                        response.sendRedirect(request.getContextPath() + "/pages/teacher_page.jsp");
                        break;
                    case "student":
                        // 学生页面
                        response.sendRedirect(request.getContextPath() + "/pages/student_page.jsp");
                        break;
                    default:
                        // 未知角色，返回登录页提示
                        request.setAttribute("errorMsg", "登录失败：未知的用户角色！");
                        request.getRequestDispatcher("/pages/login/login.jsp").forward(request, response);
                        break;
                }
            } else {
                // 登录失败：账户或密码错误
                request.setAttribute("errorMsg", "登录失败：账户或密码错误！");
                request.getRequestDispatcher("/pages/login/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            // 捕获数据库异常等系统错误
            e.printStackTrace();
            request.setAttribute("errorMsg", "登录失败：系统异常，请联系管理员！");
            request.getRequestDispatcher("/pages/login/login.jsp").forward(request, response);
        }
    }


    // 兼容GET请求（防止用户直接访问LoginServlet的GET方式）
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // GET请求直接重定向到登录页
        response.sendRedirect(request.getContextPath() + "/pages/login/pages/login/login.jsp");
    }
}