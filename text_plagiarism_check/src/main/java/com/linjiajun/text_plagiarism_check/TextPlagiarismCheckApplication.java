package com.linjiajun.text_plagiarism_check;

import com.linjiajun.text_plagiarism_check.service.TextPlagiarismCheckService;
import com.linjiajun.text_plagiarism_check.service.impl.TextPlagiarismCheckServiceImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;


/**
 * @author hypocodeemia
 */
@SpringBootApplication
public class TextPlagiarismCheckApplication {

    public static void main( String[] args ) {
        // 检查命令行参数
        if (args.length != 3) {
            System.out.println("参数不全，请参考下列说明");
            printUsage();
            System.exit(1);
        }

        String originalPath = args[0];
        String plagiarizedPath = args[1];
        String resultPath = args[2];

        TextPlagiarismCheckService service = new TextPlagiarismCheckServiceImpl();

        try {
            // 显示启动信息
            System.out.println("=== 论文查重系统启动 ===");
            System.out.println("原文文件: " + originalPath);
            System.out.println("抄袭文件: " + plagiarizedPath);
            System.out.println("结果文件: " + resultPath);
            System.out.println("开始计算相似度...");

            // 计算相似度以及耗时
            long startTime = System.currentTimeMillis();
            double similarity = service.checkSimilarity(originalPath, plagiarizedPath);
            long endTime = System.currentTimeMillis();
            // 显示结果
            System.out.println("查重完成！");
            System.out.println("重复率: " + String.format("%.2f", similarity));
            System.out.println("计算耗时: " + (endTime - startTime) + "ms");

            // 写入结果文件
            service.writeResultToFile(resultPath, similarity);
            System.out.println("结果已保存至: " + resultPath);

            // 如果相似度较高，显示详细分析
            if (similarity > 0.3) {
                System.out.println("\n相似度较高(> 0.3),开始详细分析...");
                service.analyzeInDetail(originalPath, plagiarizedPath);
            }

        } catch (IOException e) {
            System.err.println("文件操作错误: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("系统错误: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * 打印使用说明
     */
    private static void printUsage() {
        System.err.println("用法: java -jar text_plagiarism_check-0.0.1-SNAPSHOT.jar <原文路径> <抄袭版路径> <结果文件路径>");
        System.err.println("示例(已cd到jar包目录下): java -jar text_plagiarism_check-0.0.1-SNAPSHOT.jar C:\\test\\orig.txt C:\\test\\orig_add.txt C:\\test\\result.txt");
        System.err.println("\n参数说明:");
        System.err.println("  原文路径: 原始论文文件的绝对路径");
        System.err.println("  抄袭版路径: 抄袭版论文文件的绝对路径");
        System.err.println("  结果文件路径: 保存相似度结果的文件的绝对路径");
    }

}
