package com.linjiajun.math_exercise.util;

import com.linjiajun.math_exercise.bean.Fraction;

import java.util.Random;

/**
 * @author hypocodeemia
 * 分数工具类
 * 提供生成随机分数和解析分数字符串的方法
 */
public class FractionUtil {
    private static final Random RANDOM = new Random();

    /**
     * 生成随机分数（自然数或真分数）
     * 有1/3的概率生成自然数，2/3的概率生成真分数
     * @param range 数值范围（不包括该值）
     * @return 生成的随机分数
     */
    public static Fraction generateFraction(int range) {
        // 生成真分数或自然数
        if (RANDOM.nextInt(3) == 0) {
            // 生成自然数：1到range-1（确保正数）
            return new Fraction(RANDOM.nextInt(range - 1) + 1, 1);
        } else {
            // 生成真分数：分母2到range-1，分子1到分母-1（确保正数）
            int denominator = RANDOM.nextInt(range - 2) + 2;
            int numerator = RANDOM.nextInt(denominator - 1) + 1;
            return new Fraction(numerator, denominator);
        }
    }

    /**
     * 生成随机分数（自然数、真分数或带分数）
     * 有50%的概率生成带分数
     * @param range 数值范围（不包括该值）
     * @return 生成的随机分数
     */
    public static Fraction generateMixedNumber(int range) {
        // 生成带分数或普通分数
        if (RANDOM.nextBoolean()) {
            return generateFraction(range);
        } else {
            // 生成带分数：整数部分1到range-1，分数部分为真分数
            int whole = RANDOM.nextInt(range - 1) + 1;
            int denominator = RANDOM.nextInt(range - 2) + 2;
            int numerator = RANDOM.nextInt(denominator);
            return new Fraction(whole, numerator, denominator);
        }
    }

    /**
     * 解析分数字符串为Fraction对象
     * 支持自然数、真分数和带分数格式
     * @param str 分数字符串
     * @return 解析后的Fraction对象
     * @throws NumberFormatException 如果字符串格式不正确
     * @throws IllegalArgumentException 如果字符串为空或null
     */
    public static Fraction parseFraction(String str) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException("分数字符串不能为空");
        }

        str = str.trim();

        if (str.contains("'")) {
            // 带分数
            String[] parts = str.split("'");
            int whole = Integer.parseInt(parts[0]);
            String[] fractionParts = parts[1].split("/");
            int numerator = Integer.parseInt(fractionParts[0]);
            int denominator = Integer.parseInt(fractionParts[1]);
            return new Fraction(whole, numerator, denominator);
        } else if (str.contains("/")) {
            // 真分数
            String[] parts = str.split("/");
            int numerator = Integer.parseInt(parts[0]);
            int denominator = Integer.parseInt(parts[1]);
            return new Fraction(numerator, denominator);
        } else {
            // 自然数
            return new Fraction(Integer.parseInt(str), 1);
        }
    }

}
