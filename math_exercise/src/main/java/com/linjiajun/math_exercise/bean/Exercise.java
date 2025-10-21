package com.linjiajun.math_exercise.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;



/**
 * @author hypocodeemia
 * 题目模型类
 * 表示一个四则运算题目，包含题目表达式、答案和题目编号
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class Exercise {
    // 题目表达式字符串
    private String expression;
    // 题目答案
    private Fraction answer;
    // 题目编号
    private int index;

    /**
     * 获取规范化后的表达式
     * 用于题目去重比较，移除空格并统一运算符表示
     * @return 规范化后的表达式字符串
     */
    public String getNormalizedExpression() {
        return expression.replace(" ", "").replace("×", "*").replace("÷", "/");
    }
}
