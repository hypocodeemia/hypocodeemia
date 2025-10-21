package com.linjiajun.math_exercise.serivce.impl;

import com.linjiajun.math_exercise.bean.Exercise;
import com.linjiajun.math_exercise.bean.Fraction;
import com.linjiajun.math_exercise.serivce.ExerciseService;
import com.linjiajun.math_exercise.serivce.ValidationService;

import com.linjiajun.math_exercise.util.ExpressionEvaluator;
import com.linjiajun.math_exercise.util.FractionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author hypocodeemia
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {

    private final ValidationService validationService;
    private final Random random = new Random();
    // 支持的运算符
    private final String[] operators = {"+", "-", "×", "÷"};

    @Override
    public List<Exercise> generateExercises(int count, int range) {
        Set<String> normalizedExpressions = new HashSet<>(); // 用于去重
        Set<Exercise> exercises = new HashSet<>();
        int attempts = 0;
        int maxAttempts = count * 20;

        while (exercises.size() < count && attempts < maxAttempts) {
            Exercise exercise = generateAndValidateExercise(range, exercises.size() + 1);
            if (exercise != null &&
                    !normalizedExpressions.contains(exercise.getNormalizedExpression())) {
                exercises.add(exercise);
                normalizedExpressions.add(exercise.getNormalizedExpression());
            }
            attempts++;
        }

        if (exercises.size() < count) {
            log.warn("只成功生成了 {} 道题目，目标数量为 {}", exercises.size(), count);
        }

        return new ArrayList<>(exercises);
    }

    /**
     * 生成单个题目，根据运算符数量选择不同的生成策略
     * @param range 数值范围
     * @param index 题目编号
     * @return 生成的题目，如果生成失败返回null
     */
    private Exercise generateSingleExercise(int range, int index) {
        int operatorCount = random.nextInt(3) + 1;

        try {
            switch (operatorCount) {
                case 1:
                    return generateTwoOperandExercise(range, index);
                case 2:
                    return generateThreeOperandExercise(range, index);
                case 3:
                    return generateFourOperandExercise(range, index);
                default:
                    return null;
            }
        } catch (Exception e) {
            log.debug("生成题目失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 生成两个操作数的题目（一个运算符）
     * @param range 数值范围
     * @param index 题目编号
     * @return 生成的题目
     */
    private Exercise generateTwoOperandExercise(int range, int index) {
        Fraction left = FractionUtil.generateMixedNumber(range);
        Fraction right = FractionUtil.generateMixedNumber(range);
        String operator = operators[random.nextInt(4)];

        // 先生成表达式
        String expression = formatExpression(left) + " " + operator + " " +
                formatExpression(right) + " =";

        // 验证运算合法性（传递完整表达式）
        if (!isOperationValid(left, right, operator, expression)) {
            return null;
        }

        // 使用 ExpressionEvaluator 计算答案
        Fraction result;
        try {
            result = ExpressionEvaluator.evaluate(expression);
        } catch (Exception e) {
            log.debug("计算表达式失败: {}", expression, e);
            return null;
        }

        // 额外检查：确保最终结果非负
        if (!validationService.isNonNegative(result)) {
            return null;
        }

        if (!validationService.isInRange(result, range)) {
            return null;
        }

        return new Exercise(expression, result, index);
    }

    /**
     * 生成三个操作数的题目（两个运算符）
     * @param range 数值范围
     * @param index 题目编号
     * @return 生成的题目
     */
    private Exercise generateThreeOperandExercise(int range, int index) {
        Fraction a = FractionUtil.generateMixedNumber(range);
        Fraction b = FractionUtil.generateMixedNumber(range);
        Fraction c = FractionUtil.generateMixedNumber(range);

        String op1 = operators[random.nextInt(4)];
        String op2 = operators[random.nextInt(4)];

        // 先生成表达式
        String expression = formatExpression(a) + " " + op1 + " " +
                formatExpression(b) + " " + op2 + " " +
                formatExpression(c) + " =";

        // 验证第一个运算的合法性（传递完整表达式）
        if (!isOperationValid(a, b, op1, expression)) {
            return null;
        }

        // 验证第二个运算的合法性（传递完整表达式）
        // 先计算中间结果
        Fraction temp;
        try {
            temp = ExpressionEvaluator.evaluate(formatExpression(a) + " " + op1 + " " + formatExpression(b) + " =");
        } catch (Exception e) {
            return null;
        }

        if (!isOperationValid(temp, c, op2, expression)) {
            return null;
        }

        // 使用 ExpressionEvaluator 计算答案
        Fraction result;
        try {
            result = ExpressionEvaluator.evaluate(expression);
        } catch (Exception e) {
            log.debug("计算表达式失败: {}", expression, e);
            return null;
        }

        // 额外检查：确保最终结果非负
        if (!validationService.isNonNegative(result)) {
            return null;
        }

        if (!validationService.isInRange(result, range)) {
            return null;
        }

        return new Exercise(expression, result, index);
    }

    /**
     * 生成四个操作数的题目（三个运算符），可能包含括号
     * @param range 数值范围
     * @param index 题目编号
     * @return 生成的题目
     */
    private Exercise generateFourOperandExercise(int range, int index) {
        boolean useParentheses = random.nextBoolean();
        return useParentheses ?
                generateWithParentheses(range, index) :
                generateWithoutParentheses(range, index);
    }

    /**
     * 生成带括号的题目
     * @param range 数值范围
     * @param index 题目编号
     * @return 生成的题目
     */
    private Exercise generateWithParentheses(int range, int index) {
        Fraction a = FractionUtil.generateMixedNumber(range);
        Fraction b = FractionUtil.generateMixedNumber(range);
        Fraction c = FractionUtil.generateMixedNumber(range);

        String op1 = operators[random.nextInt(4)];
        String op2 = operators[random.nextInt(4)];

        int parenthesisType = random.nextInt(2);

        try {
            String expression;

            if (parenthesisType == 0) {
                // (a op b) op c
                // 先生成表达式
                expression = "(" + formatExpression(a) + " " + op1 + " " +
                        formatExpression(b) + ") " + op2 + " " +
                        formatExpression(c) + " =";

                // 验证运算合法性（传递完整表达式）
                if (!isOperationValid(a, b, op1, expression)) {
                    return null;
                }

            } else {
                // a op (b op c)
                // 先生成表达式
                expression = formatExpression(a) + " " + op1 + " (" +
                        formatExpression(b) + " " + op2 + " " +
                        formatExpression(c) + ") =";

                // 验证运算合法性（传递完整表达式）
                if (!isOperationValid(b, c, op2, expression)) {
                    return null;
                }
            }

            // 使用 ExpressionEvaluator 计算答案
            Fraction result = ExpressionEvaluator.evaluate(expression);

            // 额外检查：确保最终结果非负
            if (!validationService.isNonNegative(result)) {
                return null;
            }

            if (!validationService.isInRange(result, range)) {
                return null;
            }

            return new Exercise(expression, result, index);

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成不带括号的四个操作数题目
     * @param range 数值范围
     * @param index 题目编号
     * @return 生成的题目
     */
    private Exercise generateWithoutParentheses(int range, int index) {
        Fraction a = FractionUtil.generateMixedNumber(range);
        Fraction b = FractionUtil.generateMixedNumber(range);
        Fraction c = FractionUtil.generateMixedNumber(range);
        Fraction d = FractionUtil.generateMixedNumber(range);

        String op1 = operators[random.nextInt(4)];
        String op2 = operators[random.nextInt(4)];
        String op3 = operators[random.nextInt(4)];

        try {
            // 先生成表达式
            String expression = formatExpression(a) + " " + op1 + " " +
                    formatExpression(b) + " " + op2 + " " +
                    formatExpression(c) + " " + op3 + " " +
                    formatExpression(d) + " =";

            // 验证第一个运算的合法性（传递完整表达式）
            if (!isOperationValid(a, b, op1, expression)) {
                return null;
            }

            // 验证第二个运算的合法性（传递完整表达式）
            Fraction temp1;
            try {
                temp1 = ExpressionEvaluator.evaluate(formatExpression(a) + " " + op1 + " " + formatExpression(b) + " =");
            } catch (Exception e) {
                return null;
            }

            if (!isOperationValid(temp1, c, op2, expression)) {
                return null;
            }

            // 验证第三个运算的合法性（传递完整表达式）
            Fraction temp2;
            try {
                temp2 = ExpressionEvaluator.evaluate(formatExpression(a) + " " + op1 + " " +
                        formatExpression(b) + " " + op2 + " " +
                        formatExpression(c) + " =");
            } catch (Exception e) {
                return null;
            }

            if (!isOperationValid(temp2, d, op3, expression)) {
                return null;
            }

            // 使用表达式求值器计算答案
            Fraction result = ExpressionEvaluator.evaluate(expression);

            // 额外检查：确保最终结果非负
            if (!validationService.isNonNegative(result)) {
                return null;
            }

            if (!validationService.isInRange(result, range)) {
                return null;
            }

            return new Exercise(expression, result, index);

        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 验证运算的合法性
     * @param left 左操作数
     * @param right 右操作数
     * @param operator 运算符
     * @return 如果运算合法返回true，否则返回false
     */
    private boolean isOperationValid(Fraction left, Fraction right, String operator, String fullExpression) {
        // 先进行基础验证
        boolean basicValid;
        switch (operator) {
            case "-":
                basicValid = validationService.isValidSubtraction(left, right);
                break;
            case "÷":
                basicValid = validationService.isValidDivision(left, right);
                break;
            default:
                basicValid = true;
        }

        if (!basicValid) {
            return false;
        }

        // 如果基础验证通过，再进行完整表达式验证
        return validationService.isExpressionNonNegative(fullExpression);
    }

    /**
     * 格式化分数表达式
     * @param fraction 要格式化的分数
     * @return 格式化后的字符串
     */
    private String formatExpression(Fraction fraction) {
        return fraction.toString();
    }

    @Override
    public Map<Integer, Boolean> checkAnswers(List<String> exercises, List<String> answers) {
        Map<Integer, Boolean> results = new HashMap<>();

        for (int i = 0; i < exercises.size(); i++) {
            try {
                String exercise = exercises.get(i);
                Fraction result = ExpressionEvaluator.evaluate(exercise);
                Fraction expected = FractionUtil.parseFraction(answers.get(i));

                results.put(i + 1, result.equals(expected));
            } catch (Exception e) {
                log.debug("检查答案失败: {}", e.getMessage());
                results.put(i + 1, false);
            }
        }

        return results;
    }

    /**
     * 生成并验证表达式，确保所有中间步骤都不产生负数
     */
    private Exercise generateAndValidateExercise(int range, int index) {
        int attempts = 0;
        // 最多尝试50次
        while (attempts < 50) {
            Exercise exercise = generateSingleExercise(range, index);
            if (exercise != null &&
                    validationService.isExpressionNonNegative(exercise.getExpression())) {
                return exercise;
            }
            attempts++;
        }
        // 多次尝试后仍未生成符合条件的题目
        return null;
    }
}