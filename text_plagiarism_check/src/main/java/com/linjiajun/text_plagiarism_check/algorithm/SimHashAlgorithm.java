package com.linjiajun.text_plagiarism_check.algorithm;

import com.google.common.hash.Hashing;
import com.linjiajun.text_plagiarism_check.util.TextUtil;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @author hypocodeemia
 * SimHash相关函数
 */
public class SimHashAlgorithm {
    // 用于预处理文本
    private final TextUtil textUtil = new TextUtil();

    /**
     * 生成文本的SimHash指纹
     * 算法步骤：
     * 1. 分词和计算词频
     * 2. 为每个单词生成哈希
     * 3. 加权合并哈希向量
     * 4. 降维生成最终指纹
     * @param text 从txt文件中读取的String文本
     * @return 文本的SimHash指纹
     */
    public String generateSimHash(String text) {
        // 1. 文本预处理和分词
        List<String> words = textUtil.preprocessText(text);
        Map<String, Integer> wordFreq = textUtil.calculateWordFrequency(words);

        // 2. 初始化64位特征向量
        int[] featureVector = new int[64];

        // 3. 处理每个词
        for (Map.Entry<String, Integer> entry : wordFreq.entrySet()) {
            String word = entry.getKey();
            // 因同义词库和停用词库均不完善，目前使用1为初始权重更稳定
            int weight = 1;
            //todo:后面完善同义词库和停用词库后，可以启用词频
            //int weight = entry.getValue();

            // 生成词的64位哈希
            BigInteger wordHash = BigInteger.valueOf(generateWordHash(word));

            // 4. 加权更新特征向量
            for (int i = 0; i < 64; i++) {
                // 检查哈希的第i位是否为1
                if (wordHash.testBit(i)) {
                    // 位为1，增加权重
                    featureVector[i] += weight;
                } else {
                    // 位为0，减少权重
                    featureVector[i] -= weight;
                }
            }
        }

        // 5. 生成最终SimHash
        BigInteger simHash = BigInteger.ZERO;
        for (int i = 0; i < 64; i++) {
            if (featureVector[i] > 0) {
                // 正值设为1
                simHash = simHash.setBit(i);
            }
            // 负值保持为0
        }
        // 返回二进制字符串，返回前统一64位
        return String.format("%64s", simHash.toString(2)).replace(' ', '0');
    }

    /**
     * 为单词生成64位哈希值
     * 使用Guava的Murmur3进行生成，确保质量足够高，防止碰撞率偏高造成的伪差异
     * @param word 分词后所得集合中的String
     * @return 单词的64位哈希值
     */
    private long generateWordHash(String word) {
        return Hashing.murmur3_128().hashBytes(word.getBytes()).asLong();
    }

    /**
     * 计算两个SimHash的海明距离
     * 海明距离：两个等长字符串对应位置不同字符的数量
     * @param hash1 文章1的SimHash指纹
     * @param hash2 文章2的SimHash指纹
     * @return 两个SimHash的海明距离
     */
    public int calculateHammingDistance(String hash1, String hash2) {
        int dist = 0;
        for (int i = 0; i < 64; i++) {
            if (hash1.charAt(i) != hash2.charAt(i)) {
                dist++;
            }
        }
        return dist;
    }

    /**
     * 计算文本相似度（主方法）
     * 相似度 = 1 - (海明距离 / 指纹长度)
     * @param text1 读取txt文挡1所得的String
     * @param text2 读取txt文挡2所得的String
     * @return 文本相似度
     */
    public double calculateSimilarity(String text1, String text2) {
        // 生成SimHash指纹
        String hash1 = generateSimHash(text1);
        String hash2 = generateSimHash(text2);

        // 计算海明距离
        int hammingDistance = calculateHammingDistance(hash1, hash2);
        int fingerprintLength = Math.max(hash1.length(), hash2.length());

        // 计算相似度
        double similarity = 1.0 - (double) hammingDistance / fingerprintLength;

        // 确保结果在[0,1]范围内
        return Math.max(0.0, Math.min(1.0, similarity));
    }

    /**
     * 调试方法：显示SimHash计算详情（基本和calculateSimilarity相同，只是会而外sout出来各个变量值）
     * @param text1 读取txt文挡1所得的String
     * @param text2 读取txt文挡2所得的String
     */
    public void debugSimHash(String text1, String text2) {
        String hash1 = generateSimHash(text1);
        String hash2 = generateSimHash(text2);
        int distance = calculateHammingDistance(hash1, hash2);
        int length = Math.max(hash1.length(), hash2.length());

        System.out.println("文本1指纹: " + hash1);
        System.out.println("文本2指纹: " + hash2);
        System.out.println("指纹长度: " + length);
        System.out.println("海明距离: " + distance);
        System.out.println("相似度: " + String.format("%.4f", 1.0 - (double)distance/length));
    }

}
