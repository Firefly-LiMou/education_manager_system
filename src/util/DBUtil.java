package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// 数据库连接工具类（简化JDBC操作，新手友好）
public class DBUtil {
    // 数据库配置（硬编码，课程设计足够，无需配置文件）
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/education?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai";
    private static final String USER = "root"; // 你的MySQL用户名
    private static final String PASSWORD = "123456"; // 你的MySQL密码

    // 加载驱动（静态代码块，仅执行一次）
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 获取数据库连接
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            if (conn == null) {
                throw new SQLException("conn为空！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    // 关闭资源（ResultSet+PreparedStatement+Connection）
    public static void close(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 重载：关闭PreparedStatement+Connection（无ResultSet时）
    public static void close(PreparedStatement pstmt, Connection conn) {
        close(null, pstmt, conn);
    }
}