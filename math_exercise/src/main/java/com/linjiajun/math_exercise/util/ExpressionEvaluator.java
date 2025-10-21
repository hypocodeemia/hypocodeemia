package com.linjiajun.math_exercise.util;

import com.linjiajun.math_exercise.bean.Fraction;

import java.util.Stack;


/**
 * @author hypocodeemia
 * 数学表达式求值器
 * 支持加减乘除运算和括号，处理运算符优先级
 */
public class ExpressionEvaluator {

    /**
     * 计算数学表达式的结果
     * 使用操作数栈和运算符栈，按照运算符优先级进行计算
     * @param expression 数学表达式字符串
     * @return 计算结果（分数形式）
     * @throws IllegalArgumentException 如果表达式格式错误或包含不支持的操作
     */
    public static Fraction evaluate(String expression) {
        // 移除空格和等号
        expression = expression.replace(" ", "").replace("=", "");

        Stack<Fraction> numbers = new Stack<>();
        Stack<String> operators = new Stack<>();

        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);

            if (c == '(') {
                operators.push("(");
                i++;
            } else if (c == ')') {
                while (!"(".equals(operators.peek())) {
                    processOperation(numbers, operators);
                }
                // 移除 "("
                operators.pop();
                i++;
            } else if (isOperator(c)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(String.valueOf(c))) {
                    processOperation(numbers, operators);
                }
                operators.push(String.valueOf(c));
                i++;
            } else {
                // 解析数字或分数
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i)) ||
                                expression.charAt(i) == '/' ||
                                expression.charAt(i) == '\'')) {
                    sb.append(expression.charAt(i));
                    i++;
                }
                numbers.push(parseFraction(sb.toString()));
            }
        }

        while (!operators.isEmpty()) {
            processOperation(numbers, operators);
        }

        return numbers.pop();
    }

    /**
     * 处理单个运算操作
     * @param numbers 操作数栈
     * @param operators 运算符栈
     */
    private static void processOperation(Stack<Fraction> numbers, Stack<String> operators) {
        String op = operators.pop();
        Fraction right = numbers.pop();
        Fraction left = numbers.pop();

        switch (op) {
            case "+":
                numbers.push(left.add(right));
                break;
            case "-":
                numbers.push(left.subtract(right));
                break;
            case "×":
            case "*":
                numbers.push(left.multiply(right));
                break;
            case "÷":
            case "/":
                numbers.push(left.divide(right));
                break;
        }
    }

    /**
     * 获取运算符的优先级
     * 乘除优先级为2，加减优先级为1
     * @param op 运算符
     * @return 优先级数值，数值越大优先级越高
     */
    private static int precedence(String op) {
        switch (op) {
            case "+":
            case "-":
                return 1;
            case "×":
            case "*":
            case "÷":
            case "/":
                return 2;
            default:
                return 0;
        }
    }

    /**
     * 判断字符是否为运算符
     * @param c 要判断的字符
     * @return 如果是运算符返回true，否则返回false
     */
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '×' || c == '*' || c == '÷' || c == '/';
    }

    /**
     * 解析分数字符串为Fraction对象
     * 支持自然数、真分数和带分数格式
     * @param str 分数字符串
     * @return 解析后的Fraction对象
     * @throws NumberFormatException 如果字符串格式不正确
     */
    private static Fraction parseFraction(String str) {
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
