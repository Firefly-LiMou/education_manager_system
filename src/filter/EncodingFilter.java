package filter;

import jakarta.servlet.*;

import java.io.IOException;

// 统一字符编码过滤器（所有请求先经过此过滤器）
public class EncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 设置请求和响应的编码为UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        // 放行请求（继续执行后续Servlet/页面）
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}