package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 加密工具类（通用）
 * 封装密码加密、密码校验逻辑，支持SHA256+盐值（可选BCrypt）
 */
public class CryptoUtil {
    // 加密算法（SHA256/MD5/BCrypt）
    private static final String ALGORITHM = "SHA-256";
    // 盐值长度（增强安全性，避免彩虹表破解）
    private static final int SALT_LENGTH = 16;

    /**
     * 生成随机盐值（可选，提升密码安全性）
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 密码加密（原始密码 + 盐值）
     */
    public static String encryptPassword(String rawPassword, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            // 先加盐值，再加密
            md.update(salt.getBytes());
            byte[] hashedBytes = md.digest(rawPassword.getBytes());
            // 转Base64便于存储（避免二进制乱码）
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败：不支持" + ALGORITHM + "算法", e);
        }
    }

    /**
     * 密码校验（原始密码 + 盐值 + 已加密密码）
     */
    public static boolean verifyPassword(String rawPassword, String salt, String encryptedPassword) {
        String newEncrypted = encryptPassword(rawPassword, salt);
        return newEncrypted.equals(encryptedPassword);
    }

    // 简化版：无盐值加密（适合小型系统快速实现）
    public static String encryptPassword(String rawPassword) {
        return encryptPassword(rawPassword, "default_salt_123456"); // 固定盐值（生产环境建议用随机盐）
    }

    // 简化版：无盐值校验
    public static boolean verifyPassword(String rawPassword, String encryptedPassword) {
        return verifyPassword(rawPassword, "default_salt_123456", encryptedPassword);
    }
}
