package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库工具类（DBUtil）
 * 封装MySQL数据库连接获取、资源关闭等核心操作，适配小型教务信息管理系统
 * 特点：配置集中管理、资源安全释放、异常友好处理、使用简单
 */
public class DBUtil {
    // -------------------------- 数据库连接配置（集中管理，便于修改） --------------------------
    /**
     * 数据库驱动类名（MySQL 8.0+ 驱动类名）
     */
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    /**
     * 数据库连接URL
     */
    private static final String DB_URL = "jdbc:mysql://localhost:3306/education_manage_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false";

    /**
     * 数据库用户名（根据自己的MySQL配置修改）
     */
    private static final String DB_USER = "root";

    /**
     * 数据库密码（根据自己的MySQL配置修改）
     */
    private static final String DB_PASSWORD = "123456";

    // -------------------------- 静态代码块：加载数据库驱动（仅加载一次） --------------------------
    static {
        try {
            Class.forName(DRIVER_CLASS);
            System.out.println("数据库驱动加载成功！");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("数据库驱动加载失败，请检查驱动包是否引入！", e);
        }
    }

    // -------------------------- 获取数据库连接 --------------------------
    /**
     * 获取数据库连接（静态方法，无需创建对象即可调用）
     * @return Connection 数据库连接对象
     * @throws RuntimeException 连接失败时抛出运行时异常
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // 小型系统可关闭自动提交，便于手动事务控制（可选）
            // conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException("数据库连接失败！请检查：1.数据库服务是否启动 2.连接参数是否正确", e);
        }
        return conn;
    }

    // -------------------------- 关闭数据库资源（重载方法，适配不同场景） --------------------------
    /**
     * 关闭ResultSet、Statement/PreparedStatement、Connection
     * 最完整的资源关闭方法，适配查询操作（有结果集）
     * @param rs 结果集对象
     * @param stmt 执行语句对象（Statement/PreparedStatement）
     * @param conn 连接对象
     */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
                System.out.println("数据库连接已关闭！");
            }
        }catch (SQLException e){
            System.err.println("数据库资源关闭失败：" + e.getMessage());
        }
    }

    /**
     * 关闭Statement/PreparedStatement、Connection（适配增删改操作，无结果集）
     * @param stmt 执行语句对象
     * @param conn 连接对象
     */
    public static void close(Statement stmt, Connection conn) {
        // 复用上面的方法，ResultSet传null
        close(null, stmt, conn);
    }

    /**
     * 仅关闭Connection（适配单独关闭连接的场景）
     * @param conn 连接对象
     */
    public static void close(Connection conn) {
        // 复用上面的方法，ResultSet和Statement传null
        close(null, null, conn);
    }
}