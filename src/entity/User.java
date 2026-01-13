package entity;

/**
 * 用户实体类（对应数据库user表）
 * 适配多角色登录：管理员/教师/学生共用一张用户表，通过userType区分角色
 * 遵循JavaBean核心规范，保证与Servlet（登录验证）、JSP（登录页）、JDBC（数据库操作）适配
 */
public class User {
    // 1. 私有成员变量（核心登录字段+权限字段+扩展字段）
    private String userId;     // 用户账号（主键，与学生学号/教师编号/管理员账号一致）
    private String password;   // 登录密码（建议课程设计中可明文存储，实际开发需加密）
    private Integer userType;  // 用户类型：1-管理员，2-教师，3-学生（便于权限判断）
    private String realName;   // 真实姓名（登录后展示）
    private Integer status;    // 账号状态：1-启用，0-禁用（防止无效账号登录）

    // 2. 无参构造方法（必须！登录Servlet反射创建对象、JSP EL取值依赖）
    public User() {
    }

    // 3. 核心构造方法（登录验证常用字段，减少参数）
    public User(String userId, String password, Integer userType) {
        this.userId = userId;
        this.password = password;
        this.userType = userType;
    }

    // 4. 全参构造方法（适配用户信息新增/修改场景）
    public User(String userId, String password, Integer userType,
                String realName, Integer status, String remark) {
        this.userId = userId;
        this.password = password;
        this.userType = userType;
        this.realName = realName;
        this.status = status;
    }

    // 5. getter/setter方法（核心！外部类操作属性的唯一入口）
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    // 6. toString方法（调试时直观打印用户信息，便于排查登录问题）
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                ", userType=" + userType +
                ", realName='" + realName + '\'' +
                ", status=" + status +
                '}';
    }

    // 扩展：便捷方法（可选，简化权限判断）
    /**
     * 判断是否为管理员
     * @return true-是管理员，false-否
     */
    public boolean isAdmin() {
        return this.userType != null && this.userType == 1;
    }

    /**
     * 判断是否为教师
     * @return true-是教师，false-否
     */
    public boolean isTeacher() {
        return this.userType != null && this.userType == 2;
    }

    /**
     * 判断是否为学生
     * @return true-是学生，false-否
     */
    public boolean isStudent() {
        return this.userType != null && this.userType == 3;
    }

    /**
     * 判断账号是否启用
     * @return true-启用，false-禁用
     */
    public boolean isEnabled() {
        return this.status != null && this.status == 1;
    }
}