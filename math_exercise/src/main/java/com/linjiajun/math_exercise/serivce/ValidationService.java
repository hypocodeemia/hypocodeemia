package com.linjiajun.math_exercise.serivce;

import com.linjiajun.math_exercise.bean.Fraction;

/**
 * @author hypocodeemia
 * 负责验证数学运算的合法性，确保符合小学数学要求
 */
public interface ValidationService {
    /**
     * 验证减法运算是否合法（结果不为负数）
     */
    boolean isValidSubtraction(Fraction left, Fraction right);

    /**
     * 验证除法运算是否合法（结果为真分数）
     */
    boolean isValidDivision(Fraction left, Fraction right);

    /**
     * 验证数值是否在指定范围内
     */
    boolean isInRange(Fraction fraction, int range);

    /**
     * 验证整个表达式是否满足非负要求（包括中间步骤和最终结果）
     */
    boolean isExpressionNonNegative(String expression);

    /**
     * 验证分数是否非负
     */
    boolean isNonNegative(Fraction fraction);
}