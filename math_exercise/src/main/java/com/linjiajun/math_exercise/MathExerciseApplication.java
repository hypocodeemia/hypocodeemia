package com.linjiajun.math_exercise;

import com.linjiajun.math_exercise.controller.CommandLineController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author hypocodeemia
 */
@SpringBootApplication
public class MathExerciseApplication {

    public static void main(String[] args) {
        SpringApplication.run(MathExerciseApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(CommandLineController controller) {
        return args -> {
            // 如果没有参数，显示帮助信息
            if (args.length == 0) {
                controller.printHelp();
                return;
            }
            // 处理命令行参数
            controller.processCommand(args);
        };
    }

}
