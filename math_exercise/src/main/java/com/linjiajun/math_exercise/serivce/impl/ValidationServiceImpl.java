package com.linjiajun.math_exercise.serivce.impl;

import com.linjiajun.math_exercise.bean.Fraction;
import com.linjiajun.math_exercise.serivce.ValidationService;
import com.linjiajun.math_exercise.util.FractionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Stack;

    /**
     * @author hypocodeemia
     * 数学表达式验证服务实现类
     * 增强版本：确保所有中间计算步骤都不产生负数
     */
    @Slf4j
    @Service
    public class ValidationServiceImpl implements ValidationService {

        @Override
        public boolean isValidSubtraction(Fraction left, Fraction right) {
            Fraction result = left.subtract(right);
            // 使用新的非负验证方法
            return isNonNegative(result);
        }

        @Override
        public boolean isValidDivision(Fraction left, Fraction right) {
            if (right.getNumerator() == 0 && right.getWhole() == 0) {
                log.debug("除数为零");
                return false;
            }
            Fraction result = left.divide(right);
            // 严格验证：结果必须是真分数
            boolean isValid = result.isProperFraction();

            if (!isValid) {
                log.debug("除法结果不是真分数: {} ÷ {} = {}", left, right, result);
            }

            return isValid;
        }

        @Override
        public boolean isInRange(Fraction fraction, int range) {
            Fraction f = fraction.toImproperFraction();
            return Math.abs(f.getNumerator()) < range * f.getDenominator();
        }

        @Override
        public boolean isExpressionNonNegative(String expression) {
            try {
                String cleanExpression = expression.replace("=", "").replace(" ", "");
                return evaluateAllSteps(cleanExpression);
            } catch (Exception e) {
                log.debug("表达式验证失败: {}", expression, e);
                return false;
            }
        }

        @Override
        public boolean isNonNegative(Fraction fraction) {
            // 检查分数是否为非负数
            if (fraction.getWhole() > 0) {
                return true;
            }
            if (fraction.getWhole() < 0) {
                return false;
            }
            // 整数部分为0时，检查分子
            return fraction.getNumerator() >= 0;
        }

        /**
         * 评估表达式的所有计算步骤，确保中间步骤和最终结果都非负
         */
        private boolean evaluateAllSteps(String expression) {
            try {
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
                            if (!processOperationWithCheck(numbers, operators)) {
                                return false;
                            }
                        }
                        operators.pop();
                        i++;
                    } else if (isOperator(c)) {
                        while (!operators.isEmpty() &&
                                precedence(operators.peek()) >= precedence(String.valueOf(c))) {
                            if (!processOperationWithCheck(numbers, operators)) {
                                return false;
                            }
                        }
                        operators.push(String.valueOf(c));
                        i++;
                    } else {
                        StringBuilder sb = new StringBuilder();
                        while (i < expression.length() &&
                                (Character.isDigit(expression.charAt(i)) ||
                                        expression.charAt(i) == '/' ||
                                        expression.charAt(i) == '\'')) {
                            sb.append(expression.charAt(i));
                            i++;
                        }
                        Fraction operand = parseFraction(sb.toString());
                        // 检查操作数本身是否非负
                        if (!isNonNegative(operand)) {
                            return false;
                        }
                        numbers.push(operand);
                    }
                }

                while (!operators.isEmpty()) {
                    if (!processOperationWithCheck(numbers, operators)) {
                        return false;
                    }
                }

                // 最终结果也应该是非负的
                Fraction finalResult = numbers.pop();
                return isNonNegative(finalResult);

            } catch (Exception e) {
                return false;
            }
        }

        /**
         * 带检查的运算处理
         */
        private boolean processOperationWithCheck(Stack<Fraction> numbers, Stack<String> operators) {
            String op = operators.pop();
            Fraction right = numbers.pop();
            Fraction left = numbers.pop();
            Fraction result;

            switch (op) {
                case "+":
                    result = left.add(right);
                    break;
                case "-":
                    result = left.subtract(right);
                    // 检查减法结果是否为负
                    if (!isNonNegative(result)) {
                        return false;
                    }
                    break;
                case "×":
                    result = left.multiply(right);
                    break;
                case "÷":
                    result = left.divide(right);
                    break;
                default:
                    throw new IllegalArgumentException("未知运算符: " + op);
            }

            numbers.push(result);
            return true;
        }

        // 辅助方法保持不变...
        private boolean isOperator(char c) {
            return c == '+' || c == '-' || c == '×' || c == '÷';
        }

        private int precedence(String op) {
            switch (op) {
                case "+":
                case "-":
                    return 1;
                case "×":
                case "÷":
                    return 2;
                default:
                    return 0;
            }
        }

        private Fraction parseFraction(String str) {
            return FractionUtil.parseFraction(str);
        }
    }
