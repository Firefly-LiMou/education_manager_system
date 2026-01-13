package entity;

/**
 * 学生实体类（对应数据库student表）
 * 遵循JavaBean规范：
 * 1. 私有成员变量
 * 2. 无参构造方法（必须，反射/ORM框架需要）
 * 3. 全参构造方法（方便快速创建对象）
 * 4. getter/setter方法（JSP EL表达式/Servlet取值需要）
 * 5. toString方法（方便调试打印对象信息）
 */
public class Student extends User{
    // 1. 私有成员变量（与数据库student表字段一一对应）
    // 学号（主键，唯一标识）
    private String stuId;
    // 学生姓名
    private String stuName;
    // 性别（男/女）
    private String gender;
    // 年龄
    private Integer age;
    // 班级名称（如：计算机2023-1班）
    private String className;
    // 联系电话
    private String phone;
    // 邮箱（可选，扩展字段）
    private String email;
    // 专业（如：计算机科学与技术）
    private String major;

    // 2. 无参构造方法（必须！JSP/反射创建对象时默认调用）
    public Student() {
    }

    // 3. 全参构造方法（方便一次性赋值创建对象）
    public Student(String stuId, String stuName, String gender, Integer age,
                   String className, String phone, String email, String major) {
        this.stuId = stuId;
        this.stuName = stuName;
        this.gender = gender;
        this.age = age;
        this.className = className;
        this.phone = phone;
        this.email = email;
        this.major = major;
    }

    // 4. 简化构造方法（可选，适配常用字段场景，减少参数数量）
    public Student(String stuId, String stuName, String gender, String className) {
        this.stuId = stuId;
        this.stuName = stuName;
        this.gender = gender;
        this.className = className;
    }

    // 5. getter/setter方法（核心！外部类/Servlet/JSP获取/修改属性值）
    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    // 6. toString方法（可选但推荐，调试时打印对象信息更直观）
    @Override
    public String toString() {
        return "Student{" +
                "stuId='" + stuId + '\'' +
                ", stuName='" + stuName + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", className='" + className + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", major='" + major + '\'' +
                '}';
    }
}