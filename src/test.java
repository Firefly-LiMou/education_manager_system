import dao.StudentDao;
import entity.Student;

import java.sql.SQLException;

public class test {

    public static void main(String[] args) throws SQLException {
        Student student = new Student("086124201", "wutiandi", "男", "0861242", "大数据");
        StudentDao studentDao = new StudentDao();
        studentDao.addStudent(student);
    }
}
