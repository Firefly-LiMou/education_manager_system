package servlet;

import entity.SysUser;
import dao.SysUserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 注册Servlet
 * 处理用户注册请求，完成注册后跳转回登录页
 */
@WebServlet("/register") // 注册页面表单提交的action路径需对应此值
public class RegisterServlet extends HttpServlet {
    // 实例化Dao对象（实际开发建议用依赖注入，此处简化）


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 解决请求参数中文乱码问题（必须放在最前面）
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 2. 获取注册表单提交的参数
        String username = request.getParameter("username").trim(); // 注册账号
        String password = request.getParameter("password").trim(); // 密码
        String repassword = request.getParameter("repassword").trim(); // 确认密码
        String userType = request.getParameter("userType"); // 用户角色

        // 3. 后端数据验证（前端验证可被绕过，后端必须二次验证）
//        try {
            // 3.1 非空验证
            if (username.isEmpty()) {
//                throw new Exception("请输入注册账号！");
            }
            if (password.isEmpty()) {
//                throw new Exception("请输入登录密码！");
            }
            if (repassword.isEmpty()) {
//                throw new Exception("请输入确认密码！");
            }
            if (userType == null) {
//                throw new Exception("请选择用户角色！");
            }

            // 3.2 密码一致性验证
            if (!password.equals(repassword)) {
//                throw new Exception("两次输入的密码不一致！");
            }

            // 3.3 账号是否已存在验证
            SysUserDao userDao = new SysUserDao();
            SysUser existUser = userDao.getSysUserByAccount(username);
            if (existUser != null) {
//                throw new Exception("该账号已存在，请更换账号注册！");
            }

            // 4. 调用Dao完成注册
            SysUser newUser = new SysUser(username, password, userType);
            boolean registerSuccess = userDao.addSysUser(newUser);
            if (!registerSuccess) {
//                throw new Exception("注册失败，请稍后重试！");
            }

            // 5. 注册成功：设置提示信息，跳转登录页
            request.setAttribute("errorMsg", "注册成功！请登录");
            request.getRequestDispatcher("/pages/login/login.jsp").forward(request, response);

//        } catch (Exception e) {
            // 6. 注册失败：捕获异常，设置错误提示，跳转回登录页
//            request.setAttribute("errorMsg", "注册失败：" + e.getMessage());
            request.getRequestDispatcher("/pages/login/login.jsp").forward(request, response);
//        }
    }

    // 处理GET请求（防止用户直接访问RegisterServlet）
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 直接重定向到登录页
        response.sendRedirect(request.getContextPath() + "/pages/login/login.jsp");
    }
}
