package com.linjiajun.math_exercise.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hypocodeemia
 * 分数类
 * 表示数学中的分数，支持自然数、真分数和带分数
 */
@Data
@EqualsAndHashCode
public class Fraction {
    // 分子
    private int numerator;
    // 分母
    private int denominator;
    // 整数部分(用于带分数)
    private int whole;

    /**
     * 构造函数：创建真分数或自然数
     * @param numerator 分子
     * @param denominator 分母
     */
    public Fraction(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.whole = 0;
        normalize();
    }

    /**
     * 构造函数：创建带分数
     * @param whole 整数部分
     * @param numerator 分子
     * @param denominator 分母
     */
    public Fraction(int whole, int numerator, int denominator) {
        this.whole = whole;
        this.numerator = numerator;
        this.denominator = denominator;
        normalize();
    }

    /**
     * 规范化分数
     * 包括：约分、处理假分数、处理符号等
     */
    private void normalize() {
        // 检查分母是否为0
        if (denominator == 0) {
            throw new IllegalArgumentException("分母不能为零");
        }

        // 处理分母为负的情况
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }

        // 约分：求分子分母的最大公约数
        int gcd = gcd(Math.abs(numerator), denominator);
        numerator /= gcd;
        denominator /= gcd;

        // 处理假分数：如果分子绝对值大于等于分母，转换为带分数
        if (Math.abs(numerator) >= denominator) {
            whole += numerator / denominator;
            numerator = Math.abs(numerator) % denominator;
        }

        // 如果分子为0，重置整数部分
        if (numerator == 0) {
            whole = 0;
        }
    }

    /**
     * 计算两个数的最大公约数
     * @param a 第一个数
     * @param b 第二个数
     * @return 最大公约数
     */
    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    /**
     * 分数加法
     * @param other 另一个分数
     * @return 加法结果
     */
    public Fraction add(Fraction other) {
        int num1 = this.toImproperFraction().numerator;
        int den1 = this.toImproperFraction().denominator;
        int num2 = other.toImproperFraction().numerator;
        int den2 = other.toImproperFraction().denominator;

        int newNum = num1 * den2 + num2 * den1;
        int newDen = den1 * den2;

        return new Fraction(newNum, newDen);
    }

    /**
     * 分数减法
     * @param other 另一个分数
     * @return 减法结果
     */
    public Fraction subtract(Fraction other) {
        int num1 = this.toImproperFraction().numerator;
        int den1 = this.toImproperFraction().denominator;
        int num2 = other.toImproperFraction().numerator;
        int den2 = other.toImproperFraction().denominator;

        int newNum = num1 * den2 - num2 * den1;
        int newDen = den1 * den2;

        return new Fraction(newNum, newDen);
    }

    /**
     * 分数乘法
     * @param other 另一个分数
     * @return 乘法结果
     */
    public Fraction multiply(Fraction other) {
        int num1 = this.toImproperFraction().numerator;
        int den1 = this.toImproperFraction().denominator;
        int num2 = other.toImproperFraction().numerator;
        int den2 = other.toImproperFraction().denominator;

        int newNum = num1 * num2;
        int newDen = den1 * den2;

        return new Fraction(newNum, newDen);
    }

    /**
     * 分数除法
     * @param other 另一个分数
     * @return 除法结果
     */
    public Fraction divide(Fraction other) {
        int num1 = this.toImproperFraction().numerator;
        int den1 = this.toImproperFraction().denominator;
        int num2 = other.toImproperFraction().numerator;
        int den2 = other.toImproperFraction().denominator;

        int newNum = num1 * den2;
        int newDen = den1 * num2;

        return new Fraction(newNum, newDen);
    }

    /**
     * 转换为假分数
     * @return 假分数表示
     */
    public Fraction toImproperFraction() {
        if (whole == 0) {
            return new Fraction(numerator, denominator);
        }
        int newNumerator = whole * denominator +
                (whole < 0 ? -numerator : numerator);
        return new Fraction(newNumerator, denominator);
    }

    /**
     * 检查分数是否为正数
     * @return 如果为正数返回true，否则返回false
     */
    public boolean isPositive() {
        if (whole > 0) {
            return true;
        }
        if (whole < 0) {
            return false;
        }
        return numerator > 0;
    }

    /**
     * 检查是否为真分数
     * @return 如果是真分数返回true，否则返回false
     */
    public boolean isProperFraction() {
        return Math.abs(numerator) < denominator && whole == 0;
    }

    /**
     * 转换为字符串表示
     * 格式：自然数、真分数或带分数
     * @return 分数的字符串表示
     */
    @Override
    public String toString() {
        if (whole == 0) {
            return numerator == 0 ? "0" : numerator + "/" + denominator;
        } else {
            return numerator == 0 ? String.valueOf(whole) : whole + "'" + numerator + "/" + denominator;
        }
    }
}