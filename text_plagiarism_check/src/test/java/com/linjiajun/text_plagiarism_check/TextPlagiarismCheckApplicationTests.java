package com.linjiajun.text_plagiarism_check;

import com.linjiajun.text_plagiarism_check.service.TextPlagiarismCheckService;
import com.linjiajun.text_plagiarism_check.service.impl.TextPlagiarismCheckServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TextPlagiarismCheckApplicationTests {

    TextPlagiarismCheckService service = new TextPlagiarismCheckServiceImpl();

    @Test
    void contextLoads() {

    }

}
