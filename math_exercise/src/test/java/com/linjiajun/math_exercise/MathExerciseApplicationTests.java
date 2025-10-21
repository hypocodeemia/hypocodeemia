package com.linjiajun.math_exercise;

import com.linjiajun.math_exercise.bean.Exercise;
import com.linjiajun.math_exercise.controller.CommandLineController;
import com.linjiajun.math_exercise.serivce.ExerciseService;
import com.linjiajun.math_exercise.serivce.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@Slf4j
@SpringBootTest
class MathExerciseApplicationTests {
    @Autowired
    private CommandLineController commandLineController;
    @Autowired
    private  ExerciseService exerciseService;
    @Autowired
    private ValidationService validationService;
    @Test
    void contextLoads() {
    }

    /**
     * 等效于执行: java -jar math_exercise-0.0.1-SNAPSHOT.jar -n 10000 -r 10
     * 直接调用Controller处理生成题目的逻辑
     */
    @Test
    public void testGenerateExercisesDirectly() {
        log.info("开始直接调用生成题目方法...");
        String[] args = {"-n", "10000", "-r", "10"};
        commandLineController.processCommand(args);
        log.info("生成题目方法调用完成");
    }

    /**
     * 等效于执行: java -jar math_exercise-0.0.1-SNAPSHOT.jar -e Exercises.txt -a Answers.txt
     * 直接调用Controller处理检查答案的逻辑
     */
    @Test
    public void testCheckAnswersDirectly() {
        log.info("开始直接调用检查答案方法...");
        String[] args = {"-e", "Exercises.txt", "-a", "Answers.txt"};
        commandLineController.processCommand(args);
        log.info("检查答案方法调用完成");
    }

    @Test
    public void testParamLost() {
        log.info("开始测试缺失参数-r的情况...");
        String[] args = {"-n", "10000"};
        commandLineController.processCommand(args);
    }

    @Test
    public void testIllegalParam() {
        log.info("开始测试不合规参数的情况...");
        String[] args = {"-n", "-1", "-r", "10"};
        commandLineController.processCommand(args);
    }

    @Test
    public void testWrongDirectly() {
        log.info("开始测试文件地址异常...");
        String[] args = {"-e", "121.txt", "-a", "1424.txt"};
        commandLineController.processCommand(args);
    }

    @Test
    public void testNoNegativeResults() {
        log.info("测试负数约束验证...");
        List<Exercise> exercises = exerciseService.generateExercises(100, 10);
        int negativeCount = 0;
        int expressionViolationCount = 0;

        for (Exercise exercise : exercises) {
            // 验证所有题目答案都不为负数
            if (exercise.getAnswer().getNumerator() < 0) {
                negativeCount++;
                log.error("发现负数答案: {} = {}", exercise.getExpression(), exercise.getAnswer());
            }

            // 验证表达式本身不包含会导致负数的运算
            if (!validationService.isExpressionNonNegative(exercise.getExpression())) {
                expressionViolationCount++;
                log.error("表达式违反非负要求: {}", exercise.getExpression());
            }
        }

        assertEquals("不应有负数答案", 0, negativeCount);
        assertEquals("不应有违反非负要求的表达式", 0, expressionViolationCount);
        log.info("✓ 成功验证100道题目均无负数结果");
    }

    @Test
    public void testDivisionResultsAreProperFractions() {
        log.info("测试除法结果真分数验证...");
        List<Exercise> exercises = exerciseService.generateExercises(100, 10);
        int divisionCount = 0;
        int improperFractionCount = 0;

        for (Exercise exercise : exercises) {
            if (exercise.getExpression().contains("÷")) {
                divisionCount++;
                // 验证除法结果必须是真分数
                if (!exercise.getAnswer().isProperFraction()) {
                    improperFractionCount++;
                    log.error("除法结果不是真分数: {} = {}",
                            exercise.getExpression(), exercise.getAnswer());
                }
            }
        }

        assertEquals("所有除法结果都应是真分数", 0, improperFractionCount);
        log.info("✓ 验证{}道除法题目，结果均为真分数", divisionCount);
    }

    @Test
    public void testOperatorCountLimit() {
        log.info("测试运算符个数限制...");
        List<Exercise> exercises = exerciseService.generateExercises(50, 10);
        int violationCount = 0;

        for (Exercise exercise : exercises) {
            String expression = exercise.getExpression();
            // 统计运算符数量
            long operatorCount = expression.chars()
                    .filter(ch -> ch == '+' || ch == '-' || ch == '×' || ch == '÷')
                    .count();
            // 验证运算符数量不超过3个
            if (operatorCount > 3) {
                violationCount++;
                log.error("运算符个数超过3个: {}", expression);
            }
        }

        assertEquals("所有题目运算符个数应不超过3个", 0, violationCount);
        log.info("✓ 验证50道题目运算符个数均不超过3个");
    }

    @Test
    public void testExerciseUniqueness() {
        log.info("测试题目去重功能...");
        List<Exercise> exercises = exerciseService.generateExercises(100, 10);
        Set<String> normalizedExpressions = new HashSet<>();
        int duplicateCount = 0;

        for (Exercise exercise : exercises) {
            String normalized = exercise.getNormalizedExpression();
            if (normalizedExpressions.contains(normalized)) {
                duplicateCount++;
                log.error("发现重复题目: {}", exercise.getExpression());
            }
            normalizedExpressions.add(normalized);
        }

        assertEquals("不应有重复题目", 0, duplicateCount);
        log.info("✓ 成功生成100道不重复题目");
    }

    @Test
    public void testLargeScalePerformance() {
        log.info("测试大规模性能...");
        long startTime = System.currentTimeMillis();
        List<Exercise> exercises = exerciseService.generateExercises(10000, 10);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 验证生成10000道题目在合理时间内完成
        assertTrue("生成10000道题目应在5秒内完成，实际耗时: " + duration + "ms", duration < 5000);
        assertEquals("应生成10000道题目", 10000, exercises.size());
        log.info("✓ 成功生成10000道题目，耗时: {}ms", duration);
    }
}
