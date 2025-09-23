package com.linjiajun.text_plagiarism_check.util;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author hypocodeemia
 * 处理文本的工具类
 */
public class TextUtil {
    // 用于中文分词
    private final JiebaSegmenter segmenter;
    // 停用词
    private final Set<String> stopWords;
    // 同义词
    private final Map<String, String> synonyms;

    // （纯数字）正则
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+$");
    // （英文单词）正则
    //private static final Pattern ENGLISH_WORD_PATTERN = Pattern.compile("^[a-zA-Z]{2,}$");

    public TextUtil() {
        this.segmenter = new JiebaSegmenter();
        this.stopWords = getDefaultStopWords();
        this.synonyms = getDefaultSynonyms();
    }

    /**
     * 文本预处理（主要就是分词）
     * 1. 文本清理
     * 2. 中文分词（使用Jieba）
     * 3. 同义词替换
     * 4. 停用词过滤 & 标准化（英文）
     * @param text 从txt文挡中读取的String
     * @return 预处理后的单词集合
     */
    public List<String> preprocessText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 文本清理
        String cleanedText = cleanText(text);

        // 2. 中文分词
        List<String> words = segmentText(cleanedText);

        // 3. 同义词替换
        List<String> normalizedWords = words.stream()
                .map(this::replaceSynonym)
                .collect(Collectors.toList());

        // 4. 过滤处理
        return normalizedWords.stream()
                .map(String::toLowerCase)
                .filter(this::isValidWord)
                .filter(word -> !stopWords.contains(word))
                .map(this::normalizeWord)
                .filter(word -> !word.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 文本清理
     * @param text 从txt文挡中读取的String
     * @return 清理无用内容后的文本
     */
    private String cleanText(String text) {
        // 移除HTML标签
        text = text.replaceAll("<[^>]+>", "");
        // 移除URL
        text = text.replaceAll("https?://\\S+\\s?", "");
        // 移除电子邮件
        text = text.replaceAll("\\S+@\\S+\\.\\S+", "");
        // 移除特殊字符，保留中文、英文、数字和基本标点
        text = text.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9\\s.,!?;:，。！？；：]", "");
        // 合并多个空白字符
        text = text.replaceAll("\\s+", " ");
        return text.trim();
    }

    /**
     * 分词
     * @param text 从txt文挡中读取的String
     * @return 分词后得到的集合
     */
    private List<String> segmentText(String text) {
        // 判断文本语言类型（简单判断）
        boolean hasChinese = text.chars().anyMatch(c -> c >= 0x4E00 && c <= 0x9FFF);

        if (hasChinese) {
            // 中文文本使用Jieba分词
            List<SegToken> tokens = segmenter.process(text, JiebaSegmenter.SegMode.SEARCH);
            return tokens.stream()
                    .map(token -> token.word.trim())
                    .filter(word -> !word.isEmpty())
                    .collect(Collectors.toList());
        } else {
            // 英文文本按空格分词
            return Arrays.stream(text.split("\\s+"))
                    .filter(word -> !word.isEmpty())
                    .collect(Collectors.toList());
        }
    }

    /**
     * 验证单词是否有效
     * @param word 分词后得到的String
     * @return 布尔值：表示是否为有效单词
     */
    private boolean isValidWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            return false;
        }

        // 过滤纯数字
        if (NUMBER_PATTERN.matcher(word).matches()) {
            return false;
        }

        // 过滤单个字符（中文单字除外）
        return word.length() != 1 || isChineseCharacter(word.charAt(0));
    }

    /**
     * 判断是否为中文字符
     * @param c 字符，分词结果的第一位
     * @return 布尔值：表示是否为中文字符
     */
    private boolean isChineseCharacter(char c) {
        return c >= 0x4E00 && c <= 0x9FFF;
    }

    /**
     * 英文单词标准化（待补充，目前无效果）
     * @param word 分词后得到的String
     * @return 标准化后的单词（非英文的话不会改变）
     */
    private String normalizeWord(String word) {
        // 如果是英文单词，已在stream上游转换为小写
        // todo:后续可以在这边补充类似“单复数”，“时态”等的统一转换
        /* if (ENGLISH_WORD_PATTERN.matcher(word).matches()) {

        }*/
        return word;
    }

    /**
     * 计算词频（目前停用，补全同义词库与停用词库后启用）
     * @param words 文本预处理后的集合
     * @return 词频
     */
    public Map<String, Integer> calculateWordFrequency(List<String> words) {
        Map<String, Integer> frequency = new HashMap<>();
        for (String word : words) {
            frequency.put(word, frequency.getOrDefault(word, 0) + 1);
        }
        return frequency;
    }

    /**
     * 获取默认停用词表(不让读写其他文件，就先这样凑合着)
     * @return 默认停用词表
     */
    private Set<String> getDefaultStopWords() {
        // todo:后续换成读取txt文件,以及优化停用词库
        return new HashSet<>(Arrays.asList(
                // 中文停用词
                "的", "了", "在", "是", "我", "有", "和", "就", "不", "人", "都", "一", "一个", "上",
                "也", "很", "到", "说", "要", "去", "你", "会", "着", "没有", "看", "好", "自己",
                "这", "那", "他", "她", "它", "我们", "你们", "他们", "这个", "那个", "这些", "那些",
                "这样", "那样", "怎么", "什么", "为什么", "因为", "所以", "但是", "虽然", "如果", "然后",
                "可以", "应该", "能够", "需要", "必须", "已经", "正在", "将会", "不要", "不能", "不会",
                "之", "与", "及", "或", "等", "等等", "即", "如", "例如", "比如", "譬如", "尤其",
                "特别", "非常", "十分", "极其", "最", "更", "比较", "相对", "相当", "稍微", "有点",
                "关于", "对于", "根据", "按照", "通过", "由于", "为了", "关于", "至于", "在于",

                // 英文停用词
                "a", "an", "the", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with",
                "by", "from", "up", "about", "into", "through", "during", "before", "after",
                "above", "below", "between", "among", "is", "are", "was", "were", "be", "been",
                "being", "have", "has", "had", "do", "does", "did", "will", "would", "could",
                "should", "may", "might", "must", "can"
        ));
    }

    /**
     * 获取默认同义词表(不让读写其他文件，就先这样凑合着)
     * @return 默认同义词表
     */
    private Map<String, String> getDefaultSynonyms() {
        // todo:后续换成读取txt文件,以及优化同义词库
        Map<String, String> synonyms = new HashMap<>();
        synonyms.put("周一","星期一");synonyms.put("周二","星期二");synonyms.put("周三","星期三");
        synonyms.put("周四","星期四");synonyms.put("周五","星期五");synonyms.put("周六","星期六");
        synonyms.put("周日", "星期日");synonyms.put("星期天", "星期日");synonyms.put("周天", "星期日");
        synonyms.put("晴", "晴朗");
        return  synonyms;
    }

    /**
     * 同义词替换
     * @param word 分词后得到的String
     * @return 同义词（同义词表中没有的话，就输出原本的String）
     */
    private String replaceSynonym(String word) {
        return synonyms.getOrDefault(word, word);
    }


}
