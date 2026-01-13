import dao.StudentDao;
import entity.Student;

import java.sql.SQLException;

public class test {

    public static void main(String[] args) throws SQLException {
        Student student = new Student("086124203","wutiandi","man",20,"cccc","111","1111","111");
        StudentDao studentDao = new StudentDao();
        studentDao.addStudent(student);
    }
}
