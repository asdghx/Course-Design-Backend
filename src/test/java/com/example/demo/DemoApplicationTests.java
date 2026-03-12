package com.example.demo;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }

    /**
     * 测试 HanLP 中文分词功能
     */
    @Test
    void testHanLPSegmentation() {
        System.out.println("========== HanLP 分词测试 ==========");
        
        // 测试 1：岗位名称分词
        String jobTitle1 = "Java 后端开发工程师";
        List<Term> terms1 = HanLP.segment(jobTitle1);
        System.out.println("\n【岗位名称】: " + jobTitle1);
        System.out.print("【分词结果】: ");
        for (Term term : terms1) {
            System.out.print(term.word + "/" + term.nature + "  ");
        }
        System.out.println();
        
        // 测试 2：岗位描述分词
        String jobDesc = "熟悉 Java、Python、Spring Boot 框架，有 MySQL 数据库使用经验";
        List<Term> terms2 = HanLP.segment(jobDesc);
        System.out.println("\n【岗位描述】: " + jobDesc);
        System.out.print("【分词结果】: ");
        for (Term term : terms2) {
            System.out.print(term.word + "/" + term.nature + "  ");
        }
        System.out.println();
        
        // 测试 3：关键词提取
        String text = "招聘 Java 工程师，要求熟悉 Spring Cloud、Redis、MySQL，有分布式系统开发经验";
        List<String> keywords = HanLP.extractKeyword(text, 5);
        System.out.println("\n【原文】: " + text);
        System.out.println("【提取关键词】: " + keywords);
        
        // 测试 4：简历内容分词
        String resume = "熟练掌握 Java 编程语言，熟悉 Spring Boot、MyBatis 等框架，了解微服务架构";
        List<Term> terms3 = HanLP.segment(resume);
        System.out.println("\n【简历内容】: " + resume);
        System.out.print("【分词结果】: ");
        for (Term term : terms3) {
            System.out.print(term.word + "/" + term.nature + "  ");
        }
        System.out.println();
        
        // 测试 5：技能关键词提取
        List<String> skills = HanLP.extractKeyword(resume, 8);
        System.out.println("\n【简历技能关键词】: " + skills);
        
        System.out.println("\n========== 测试完成 ==========");
    }

}
