package com.linjiajun.text_plagiarism_check.service.impl;

import com.linjiajun.text_plagiarism_check.algorithm.SimHashAlgorithm;
import com.linjiajun.text_plagiarism_check.service.TextPlagiarismCheckService;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author hypocodeemia
 */
public class TextPlagiarismCheckServiceImpl implements TextPlagiarismCheckService {
    private final SimHashAlgorithm simHashAlgorithm = new SimHashAlgorithm();

    @Override
    public double checkSimilarity(String originalFilePath, String plagiarizedFilePath) throws IOException {
        // 读取文件内容
        String originalText = readFileContent(originalFilePath);
        String plagiarizedText = readFileContent(plagiarizedFilePath);

        // 验证文件内容
        if (originalText.isEmpty() || plagiarizedText.isEmpty()) {
            throw new IOException("文件内容为空，请检查文件路径和内容");
        }

        // 计算相似度
        double similarity = simHashAlgorithm.calculateSimilarity(originalText, plagiarizedText);

        // 四舍五入保留两位小数
        return Math.round(similarity * 100.0) / 100.0;
    }

    @Override
    public String readFileContent(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("文件不存在: " + filePath);
        }
        if (!file.canRead()) {
            throw new IOException("无法读取文件: " + filePath);
        }

        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    @Override
    public void writeResultToFile(String filePath, double similarity) throws IOException {
        String result = String.format("%.2f", similarity);
        FileUtils.writeStringToFile(new File(filePath), result, StandardCharsets.UTF_8);
    }

    @Override
    public void analyzeInDetail(String originalFilePath, String plagiarizedFilePath) throws IOException {
        String originalText = readFileContent(originalFilePath);
        String plagiarizedText = readFileContent(plagiarizedFilePath);

        System.out.println("=== 论文查重详细分析 ===");
        System.out.println("原文长度: " + originalText.length() + " 字符");
        System.out.println("抄袭文本长度: " + plagiarizedText.length() + " 字符");

        // 使用调试模式显示详细信息
        simHashAlgorithm.debugSimHash(originalText, plagiarizedText);
    }
}
