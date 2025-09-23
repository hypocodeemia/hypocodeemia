package com.linjiajun.text_plagiarism_check.service;

import java.io.IOException;

/**
 * @author hypocodeemia
 */
public interface TextPlagiarismCheckService {
    /**
     * 论文查重主方法
     * @param originalFilePath 原文文件路径
     * @param plagiarizedFilePath 抄袭版文件路径
     * @return 相似度分数(保留两位小数)
     */
    double checkSimilarity(String originalFilePath, String plagiarizedFilePath) throws IOException;

    /**
     * 读取文件内容
     * @param filePath 文件绝对路径（从java -jar 命令中获取）
     * @return 文本文件内容的String
     */
    String readFileContent(String filePath) throws IOException;

    /**
     * 将结果写入文件
     * @param filePath 输出文件的绝对路径（从java -jar 命令中获取）
     * @param similarity 两个文件的相似度
     */
    void writeResultToFile(String filePath, double similarity) throws IOException;

    /**
     * 详细分析模式（用于调试）
     * @param originalFilePath “原文件”的绝对路径（从java -jar 命令中获取）
     * @param plagiarizedFilePath “抄袭文件”的绝对路径（从java -jar 命令中获取）
     */
    void analyzeInDetail(String originalFilePath, String plagiarizedFilePath) throws IOException;
}
