package dao;

import entity.Student;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生数据访问层（DAO）
 * 负责与数据库student表交互，封装所有学生相关的数据库操作
 * 核心功能：查询所有学生、按学号查询、新增、修改、删除学生信息
 */
public class StudentDao {

    /**
     * 查询所有学生信息
     * @return 学生列表（List<Student>），无数据时返回空列表
     * @throws SQLException 数据库操作异常（抛给上层Servlet处理）
     */
    public List<Student> queryAllStudents() throws SQLException {
        // 1. 声明变量：连接、预编译语句、结果集、学生列表
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Student> studentList = new ArrayList<>();

        try {
            // 2. 获取数据库连接
            conn = DBUtil.getConnection();
            // 3. 编写SQL语句（查询所有字段，适配Student实体类）
            String sql = "SELECT stu_id, stu_name, gender, age, class_name, phone, email, major FROM student";
            // 4. 创建预编译语句对象
            pstmt = conn.prepareStatement(sql);
            // 5. 执行查询，获取结果集
            rs = pstmt.executeQuery();

            // 6. 遍历结果集，封装为Student对象并加入列表
            while (rs.next()) {
                Student student = new Student();
                student.setStuId(rs.getString("stu_id"));
                student.setStuName(rs.getString("stu_name"));
                student.setGender(rs.getString("gender"));
                student.setAge(rs.getInt("age"));
                student.setClassName(rs.getString("class_name"));
                student.setPhone(rs.getString("phone"));
                student.setEmail(rs.getString("email"));
                student.setMajor(rs.getString("major"));
                studentList.add(student);
            }
        } finally {
            // 7. 关闭资源（无论是否异常，都要关闭）
            DBUtil.close(rs, pstmt, conn);
        }

        // 8. 返回学生列表
        return studentList;
    }

    /**
     * 根据学号查询单个学生信息
     * @param stuId 学生学号（主键）
     * @return 对应的Student对象，无数据时返回null
     * @throws SQLException 数据库操作异常
     */
    public Student queryStudentByStuId(String stuId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Student student = null;

        try {
            conn = DBUtil.getConnection();
            // SQL：按学号精准查询（主键查询，结果唯一）
            String sql = "SELECT stu_id, stu_name, gender, age, class_name, phone, email, major FROM student WHERE stu_id = ?";
            pstmt = conn.prepareStatement(sql);
            // 设置占位符参数（?）：第一个参数索引为1，值为stuId
            pstmt.setString(1, stuId);
            rs = pstmt.executeQuery();

            // 若查询到结果，封装为Student对象
            if (rs.next()) {
                student = new Student();
                student.setStuId(rs.getString("stu_id"));
                student.setStuName(rs.getString("stu_name"));
                student.setGender(rs.getString("gender"));
                student.setAge(rs.getInt("age"));
                student.setClassName(rs.getString("class_name"));
                student.setPhone(rs.getString("phone"));
                student.setEmail(rs.getString("email"));
                student.setMajor(rs.getString("major"));
            }
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return student;
    }

    /**
     * 新增学生信息
     * @param student 待新增的Student对象（需包含所有非空字段）
     * @return true-新增成功，false-新增失败
     * @throws SQLException 数据库操作异常
     */
    public boolean addStudent(Student student) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int affectedRows = 0; // 受影响行数（新增成功则≥1）

        try {
            conn = DBUtil.getConnection();
            // SQL：插入所有字段，使用占位符避免SQL注入
            String sql = "INSERT INTO student (stu_id, stu_name, gender, age, class_name, phone, email, major) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            // 设置占位符参数（与Student对象字段一一对应）
            pstmt.setString(1, student.getStuId());
            pstmt.setString(2, student.getStuName());
            pstmt.setString(3, student.getGender());
            pstmt.setInt(4, student.getAge());
            pstmt.setString(5, student.getClassName());
            pstmt.setString(6, student.getPhone());
            pstmt.setString(7, student.getEmail());
            pstmt.setString(8, student.getMajor());

            // 执行更新操作（新增/修改/删除用executeUpdate，返回受影响行数）
            affectedRows = pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt, conn); // 无ResultSet，调用重载的close方法
        }

        // 受影响行数>0则表示新增成功
        return affectedRows > 0;
    }

    /**
     * 修改学生信息（按学号修改）
     * @param student 待修改的Student对象（stuId为修改依据，其他字段为新值）
     * @return true-修改成功，false-修改失败
     * @throws SQLException 数据库操作异常
     */
    public boolean updateStudent(Student student) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int affectedRows = 0;

        try {
            conn = DBUtil.getConnection();
            // SQL：按学号修改所有可更新字段
            String sql = "UPDATE student SET stu_name=?, gender=?, age=?, class_name=?, phone=?, email=?, major=? WHERE stu_id=?";
            pstmt = conn.prepareStatement(sql);

            // 设置占位符参数（注意顺序：先改的字段，最后是WHERE条件的学号）
            pstmt.setString(1, student.getStuName());
            pstmt.setString(2, student.getGender());
            pstmt.setInt(3, student.getAge());
            pstmt.setString(4, student.getClassName());
            pstmt.setString(5, student.getPhone());
            pstmt.setString(6, student.getEmail());
            pstmt.setString(7, student.getMajor());
            pstmt.setString(8, student.getStuId());

            affectedRows = pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt, conn);
        }

        return affectedRows > 0;
    }

    /**
     * 删除学生信息（按学号删除）
     * @param stuId 学生学号（主键）
     * @return true-删除成功，false-删除失败
     * @throws SQLException 数据库操作异常
     */
    public boolean deleteStudent(String stuId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int affectedRows = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM student WHERE stu_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, stuId);
            affectedRows = pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt, conn);
        }

        return affectedRows > 0;
    }
}