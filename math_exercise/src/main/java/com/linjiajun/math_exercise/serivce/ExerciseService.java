package com.linjiajun.math_exercise.serivce;

import com.linjiajun.math_exercise.bean.Exercise;

import java.util.List;
import java.util.Map;

/**
 * @author hypocodeemia
 * 负责生成数学题目和检查答案的正确性
 */
public interface ExerciseService {

    /**
     * 生成指定数量和数值范围的数学题目
     * @param count 需要生成的题目数量
     * @param range 数值范围（不包括该值），控制自然数、真分数分母的范围
     * @return 生成的题目列表，每个题目包含表达式和答案
     */
    List<Exercise> generateExercises(int count, int range);

    /**
     * 检查给定题目和答案的正确性
     * @param exercises 题目列表，每行一个题目表达式
     * @param answers 答案列表，每行一个答案
     * @return 检查结果映射，key为题目编号，value为是否正确
     */
    Map<Integer, Boolean> checkAnswers(List<String> exercises, List<String> answers);
}
