package com.linjiajun.math_exercise.controller;

import com.linjiajun.math_exercise.bean.Exercise;
import com.linjiajun.math_exercise.serivce.ExerciseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hypocodeemia
 * 命令行控制器
 * 处理用户输入的命令行参数，协调题目生成和答案检查
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommandLineController {
    private final ExerciseService exerciseService;

    /**
     * 处理命令行命令
     * 解析命令行参数并执行相应的操作（生成题目或检查答案）
     * @param args 命令行参数数组
     */
    public void processCommand(String[] args) {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("n") && cmd.hasOption("r")) {
                int count = Integer.parseInt(cmd.getOptionValue("n"));
                int range = Integer.parseInt(cmd.getOptionValue("r"));

                if (count <= 0 || range <= 1) {
                    log.error("题目数量必须大于0，数值范围必须大于1");
                    return;
                }

                generateExercises(count, range);

            } else if (cmd.hasOption("e") && cmd.hasOption("a")) {
                String exerciseFile = cmd.getOptionValue("e");
                String answerFile = cmd.getOptionValue("a");

                checkAnswers(exerciseFile, answerFile);

            } else {
                printHelp();
            }

        } catch (ParseException e) {
            log.error("参数解析错误: {}", e.getMessage());
            printHelp();
        } catch (NumberFormatException e) {
            log.error("参数格式不正确");
            printHelp();
        } catch (Exception e) {
            log.error("程序执行错误: {}", e.getMessage());
        }
    }

    /**
     * 创建命令行选项定义
     * @return 配置好的Options对象
     */
    private Options createOptions() {
        Options options = new Options();

        options.addOption(Option.builder("n")
                .hasArg()
                .argName("count")
                .desc("生成题目的数量")
                .build());

        options.addOption(Option.builder("r")
                .hasArg()
                .argName("range")
                .desc("数值范围")
                .build());

        options.addOption(Option.builder("e")
                .hasArg()
                .argName("exercisefile")
                .desc("题目文件")
                .build());

        options.addOption(Option.builder("a")
                .hasArg()
                .argName("answerfile")
                .desc("答案文件")
                .build());

        return options;
    }

    /**
     * 打印程序使用帮助信息
     */
    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        String header = "\n小学四则运算题目生成程序\n\n";
        String footer = "\n示例:\n" +
                "生成题目: java -jar math_exercise-0.0.1-SNAPSHOT.jar -n 10 -r 10\n" +
                "检查答案: java -jar math_exercise-0.0.1-SNAPSHOT.jar -e Exercises.txt -a Answers.txt\n";

        formatter.printHelp("Myapp", header, createOptions(), footer, true);
    }

    /**
     * 生成指定数量和范围的数学题目
     * @param count 题目数量
     * @param range 数值范围
     */
    private void generateExercises(int count, int range) {
        try {
            log.info("正在生成 {} 道题目，数值范围: {}", count, range);

            List<Exercise> exercises = exerciseService.generateExercises(count, range);

            writeExercisesToFile(exercises);
            writeAnswersToFile(exercises);

            log.info("题目生成完成！");
            log.info("题目文件: Exercises.txt");
            log.info("答案文件: Answers.txt");

        } catch (Exception e) {
            log.error("生成题目时发生错误: {}", e.getMessage());
        }
    }

    /**
     * 将题目列表写入文件
     *
     * @param exercises 题目列表
     */
    private void writeExercisesToFile(List<Exercise> exercises) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                Files.newOutputStream(Paths.get("Exercises.txt")), StandardCharsets.UTF_8))) {
            for (Exercise exercise : exercises) {
                writer.println(exercise.getExpression());
            }
        } catch (IOException e) {
            throw new RuntimeException("写入题目文件失败: " + e.getMessage());
        }
    }

    /**
     * 将答案列表写入文件
     *
     * @param exercises 题目列表（包含答案）
     */
    private void writeAnswersToFile(List<Exercise> exercises) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                Files.newOutputStream(Paths.get("Answers.txt")), StandardCharsets.UTF_8))) {
            for (Exercise exercise : exercises) {
                writer.println(exercise.getAnswer().toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("写入答案文件失败: " + e.getMessage());
        }
    }

    /**
     * 检查答案的正确性
     * 读取题目文件和答案文件，比较计算结果与给定答案
     * @param exerciseFile 题目文件名
     * @param answerFile 答案文件名
     */
    private void checkAnswers(String exerciseFile, String answerFile) {
        try {
            List<String> exercises = readLinesFromFile(exerciseFile);
            List<String> answers = readLinesFromFile(answerFile);

            if (exercises.size() != answers.size()) {
                log.error("题目和答案数量不匹配");
                return;
            }

            Map<Integer, Boolean> results = exerciseService.checkAnswers(exercises, answers);

            List<Integer> correct = results.entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .sorted()
                    .collect(Collectors.toList());

            List<Integer> wrong = results.entrySet().stream()
                    .filter(entry -> !entry.getValue())
                    .map(Map.Entry::getKey)
                    .sorted()
                    .collect(Collectors.toList());

            writeGradeToFile(correct, wrong);

            log.info("答案检查完成！");
            log.info("统计文件: Grade.txt");

        } catch (IOException e) {
            log.error("文件读取错误: {}", e.getMessage());
        }
    }

    /**
     * 将检查结果写入统计文件
     *
     * @param correct 正确的题目编号列表
     * @param wrong   错误的题目编号列表
     */
    private void writeGradeToFile(List<Integer> correct, List<Integer> wrong) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                Files.newOutputStream(Paths.get("Grade.txt")), StandardCharsets.UTF_8))) {
            writer.println("Correct: " + correct.size() + " " + formatList(correct));
            writer.println("Wrong: " + wrong.size() + " " + formatList(wrong));
        } catch (IOException e) {
            throw new RuntimeException("写入统计文件失败: " + e.getMessage());
        }
    }

    /**
     * 从文件读取所有行
     * @param filename 文件名
     * @return 文件内容行列表
     * @throws IOException 如果文件读取失败
     */
    private List<String> readLinesFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Files.newInputStream(Paths.get(filename)), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    /**
     * 格式化题目编号列表为字符串
     * 格式：(1, 2, 3, 4, 5)
     * @param list 题目编号列表
     * @return 格式化后的字符串
     */
    private String formatList(List<Integer> list) {
        return list.isEmpty() ? "()" :
                "(" + list.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")";
    }
}