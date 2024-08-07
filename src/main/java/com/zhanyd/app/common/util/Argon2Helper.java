package com.zhanyd.app.common.util;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Argon2算法
 */
public class Argon2Helper {

    /**
     * 生成密码的哈希字符串
     * @param password
     * @return
     */
    public static String hashPassword(String password) {
        // 设置 Argon2 参数
        int memoryCost = 65536;
        int parallelism = 1;
        int iterations = 4;
        int saltLength = 16;
        int outputLength = 32;

        // 生成盐值
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltLength];
        random.nextBytes(salt);

        // 设置 Argon2 参数
        Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withParallelism(parallelism)
                .withMemoryAsKB(memoryCost)
                .withIterations(iterations)
                .build();

        // 生成 Argon2 哈希
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(params);

        byte[] hash = new byte[outputLength];
        generator.generateBytes(password.toCharArray(), hash);

        // 将哈希和盐值拼接，以便存储
        byte[] combined = new byte[saltLength + outputLength];
        System.arraycopy(salt, 0, combined, 0, saltLength);
        System.arraycopy(hash, 0, combined, saltLength, outputLength);

        // 返回 Base64 编码的哈希值
        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * 验证密码
     * @param password
     * @param storedHash
     * @return
     */
    public static boolean verifyPassword(String password, String storedHash) {
        // 从存储的哈希中提取盐值和哈希值
        byte[] decodedHash = Base64.getDecoder().decode(storedHash);
        byte[] salt = Arrays.copyOfRange(decodedHash, 0, 16);
        byte[] storedPasswordHash = Arrays.copyOfRange(decodedHash, 16, decodedHash.length);

        // 设置 Argon2 参数
        int memoryCost = 65536;
        int parallelism = 1;
        int iterations = 4;
        int outputLength = 32;

        // 设置 Argon2 参数
        Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withParallelism(parallelism)
                .withMemoryAsKB(memoryCost)
                .withIterations(iterations)
                .build();

        // 生成 Argon2 哈希
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(params);

        byte[] newPasswordHash = new byte[outputLength];
        generator.generateBytes(password.toCharArray(), newPasswordHash);

        // 比较生成的哈希与存储的哈希是否匹配
        return Arrays.equals(storedPasswordHash, newPasswordHash);
    }

}
