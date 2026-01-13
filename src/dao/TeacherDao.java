package dao;

import entity.Teacher;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 教师数据访问层（DAO）
 * 负责与数据库teacher表交互，封装所有教师相关的数据库操作
 * 核心功能：查询所有教师、按教师编号查询、新增、修改、删除教师信息
 * 遵循DAO层设计规范，与Teacher实体类、DBUtil工具类无缝适配
 */
public class TeacherDao {

    /**
     * 查询所有教师信息
     * @return 教师列表（List<Teacher>），无数据时返回空列表
     * @throws SQLException 数据库操作异常（抛给上层Servlet处理）
     */
    public List<Teacher> queryAllTeachers() throws SQLException {
        // 1. 声明数据库资源和返回结果变量
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Teacher> teacherList = new ArrayList<>();

        try {
            // 2. 获取数据库连接
            conn = DBUtil.getConnection();
            // 3. 编写查询SQL（字段与Teacher实体类一一对应）
            String sql = "SELECT tea_id, tea_name, gender, age, title, college, phone, email, course_name FROM teacher";
            // 4. 创建预编译语句对象（防SQL注入）
            pstmt = conn.prepareStatement(sql);
            // 5. 执行查询并获取结果集
            rs = pstmt.executeQuery();

            // 6. 遍历结果集，封装为Teacher对象
            while (rs.next()) {
                Teacher teacher = new Teacher();
                teacher.setTeaId(rs.getString("tea_id"));
                teacher.setTeaName(rs.getString("tea_name"));
                teacher.setGender(rs.getString("gender"));
                teacher.setAge(rs.getInt("age"));
                teacher.setTitle(rs.getString("title"));
                teacher.setCollege(rs.getString("college"));
                teacher.setPhone(rs.getString("phone"));
                teacher.setEmail(rs.getString("email"));
                teacher.setCourseName(rs.getString("course_name"));
                teacherList.add(teacher);
            }
        } finally {
            // 7. 关闭资源（无论是否异常，必须关闭）
            DBUtil.close(rs, pstmt, conn);
        }

        return teacherList;
    }

    /**
     * 根据教师编号查询单个教师信息
     * @param teaId 教师编号（主键）
     * @return 对应的Teacher对象，无数据时返回null
     * @throws SQLException 数据库操作异常
     */
    public Teacher queryTeacherByTeaId(String teaId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Teacher teacher = null;

        try {
            conn = DBUtil.getConnection();
            // 主键精准查询，结果唯一
            String sql = "SELECT tea_id, tea_name, gender, age, title, college, phone, email, course_name FROM teacher WHERE tea_id = ?";
            pstmt = conn.prepareStatement(sql);
            // 设置占位符参数（第一个?对应teaId）
            pstmt.setString(1, teaId);
            rs = pstmt.executeQuery();

            // 封装查询结果为Teacher对象
            if (rs.next()) {
                teacher = new Teacher();
                teacher.setTeaId(rs.getString("tea_id"));
                teacher.setTeaName(rs.getString("tea_name"));
                teacher.setGender(rs.getString("gender"));
                teacher.setAge(rs.getInt("age"));
                teacher.setTitle(rs.getString("title"));
                teacher.setCollege(rs.getString("college"));
                teacher.setPhone(rs.getString("phone"));
                teacher.setEmail(rs.getString("email"));
                teacher.setCourseName(rs.getString("course_name"));
            }
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return teacher;
    }

    /**
     * 新增教师信息
     * @param teacher 待新增的Teacher对象（需包含所有非空字段）
     * @return true-新增成功，false-新增失败
     * @throws SQLException 数据库操作异常
     */
    public boolean addTeacher(Teacher teacher) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int affectedRows = 0; // 受影响行数（新增成功则≥1）

        try {
            conn = DBUtil.getConnection();
            // 插入SQL，占位符对应所有字段，避免SQL注入
            String sql = "INSERT INTO teacher (tea_id, tea_name, gender, age, title, college, phone, email, course_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            // 设置占位符参数（与Teacher对象字段一一对应）
            pstmt.setString(1, teacher.getTeaId());
            pstmt.setString(2, teacher.getTeaName());
            pstmt.setString(3, teacher.getGender());
            pstmt.setInt(4, teacher.getAge());
            pstmt.setString(5, teacher.getTitle());
            pstmt.setString(6, teacher.getCollege());
            pstmt.setString(7, teacher.getPhone());
            pstmt.setString(8, teacher.getEmail());
            pstmt.setString(9, teacher.getCourseName());

            // 执行更新操作（新增/修改/删除用executeUpdate）
            affectedRows = pstmt.executeUpdate();
        } finally {
            // 无ResultSet，调用重载的close方法
            DBUtil.close(pstmt, conn);
        }

        return affectedRows > 0;
    }

    /**
     * 修改教师信息（按教师编号修改）
     * @param teacher 待修改的Teacher对象（teaId为修改依据，其他字段为新值）
     * @return true-修改成功，false-修改失败
     * @throws SQLException 数据库操作异常
     */
    public boolean updateTeacher(Teacher teacher) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int affectedRows = 0;

        try {
            conn = DBUtil.getConnection();
            // 更新SQL，按tea_id修改所有可更新字段
            String sql = "UPDATE teacher SET tea_name=?, gender=?, age=?, title=?, college=?, phone=?, email=?, course_name=? WHERE tea_id=?";
            pstmt = conn.prepareStatement(sql);

            // 设置占位符参数（先改字段，最后是WHERE条件的教师编号）
            pstmt.setString(1, teacher.getTeaName());
            pstmt.setString(2, teacher.getGender());
            pstmt.setInt(3, teacher.getAge());
            pstmt.setString(4, teacher.getTitle());
            pstmt.setString(5, teacher.getCollege());
            pstmt.setString(6, teacher.getPhone());
            pstmt.setString(7, teacher.getEmail());
            pstmt.setString(8, teacher.getCourseName());
            pstmt.setString(9, teacher.getTeaId());

            affectedRows = pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt, conn);
        }

        return affectedRows > 0;
    }

    /**
     * 删除教师信息（按教师编号删除）
     * @param teaId 教师编号（主键）
     * @return true-删除成功，false-删除失败
     * @throws SQLException 数据库操作异常
     */
    public boolean deleteTeacher(String teaId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int affectedRows = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM teacher WHERE tea_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, teaId);
            affectedRows = pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt, conn);
        }

        return affectedRows > 0;
    }
}