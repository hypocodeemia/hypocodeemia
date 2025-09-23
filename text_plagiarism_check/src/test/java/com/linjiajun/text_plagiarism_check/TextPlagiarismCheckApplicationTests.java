package com.linjiajun.text_plagiarism_check;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TextPlagiarismCheckApplication.class)
class TextPlagiarismCheckApplicationTests {
    // 文件地址前缀
    String file_prefix = "C:\\test\\long\\";
    // 文件名
    String read_file1_name = "orig.txt";
    String read_file2_name = "orig_0.8_add.txt";
    String export_name = "result.txt";

    @Test
    void contextLoads() {

    }

/*    @Test
    void testMainMethodWithArgs() {
        // 带参数调用main方法
        TextPlagiarismCheckApplication.main(new String[]
                {file_prefix+read_file1_name,file_prefix+read_file2_name,file_prefix+export_name});
    }*/

/*    @Test
    void testMainMethodWithOutArgs() {
        // 不带参数调用main方法
        TextPlagiarismCheckApplication.main(new String[]{});
    }*/

/*    @Test
    void testMainMethodWithWrongAddress() {
        // 带错误地址参数调用main方法
        TextPlagiarismCheckApplication.main(new String[]{file_prefix,file_prefix,file_prefix+export_name});
    }*/


}
