package entity;

/**
 * 教师实体类（对应数据库teacher表）
 * 遵循JavaBean核心规范：
 * 1. 私有成员变量（与数据库字段一一映射）
 * 2. 无参构造（反射/页面取值必备）
 * 3. getter/setter（Servlet/JSP操作属性）
 * 4. 多构造方法（适配不同赋值场景）
 * 5. toString（调试便捷）
 */
public class Teacher extends User{
    // 1. 私有成员变量（核心字段+扩展字段）
    private String teaId;     // 教师编号（主键，如：T2023001）
    private String teaName;   // 教师姓名
    private String gender;    // 性别（男/女）
    private Integer age;      // 年龄
    private String title;     // 职称（讲师/副教授/教授/助教）
    private String college;   // 所属学院（如：计算机学院）
    private String phone;     // 联系电话
    private String email;     // 邮箱
    private String courseName;// 主讲课程（可选，如：Java程序设计）

    // 2. 无参构造方法（必须！JSP/反射创建对象默认调用）
    public Teacher() {
    }

    // 3. 全参构造方法（一次性赋值创建对象）
    public Teacher(String teaId, String teaName, String gender, Integer age,
                   String title, String college, String phone, String email, String courseName) {
        this.teaId = teaId;
        this.teaName = teaName;
        this.gender = gender;
        this.age = age;
        this.title = title;
        this.college = college;
        this.phone = phone;
        this.email = email;
        this.courseName = courseName;
    }

    // 4. 简化构造方法（适配常用字段场景，减少参数）
    public Teacher(String teaId, String teaName, String title, String college) {
        this.teaId = teaId;
        this.teaName = teaName;
        this.title = title;
        this.college = college;
    }

    // 5. getter/setter方法（核心！外部类操作属性的唯一方式）
    public String getTeaId() {
        return teaId;
    }

    public void setTeaId(String teaId) {
        this.teaId = teaId;
    }

    public String getTeaName() {
        return teaName;
    }

    public void setTeaName(String teaName) {
        this.teaName = teaName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    // 6. toString方法（调试时直观打印对象信息，避免输出内存地址）
    @Override
    public String toString() {
        return "Teacher{" +
                "teaId='" + teaId + '\'' +
                ", teaName='" + teaName + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", title='" + title + '\'' +
                ", college='" + college + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", courseName='" + courseName + '\'' +
                '}';
    }
}