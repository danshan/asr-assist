package com.github.danshan.asrassist.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 默认的 index 入口, 通过 freemarker 做渲染
     */
    @Controller
    static class ViewController {

        @GetMapping("/")
        public String index() {
            return "index";
        }

        @GetMapping("favicon.ico")
        @ResponseBody
        public void returnNoFavicon() {
        }

    }
}
