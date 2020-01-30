package com.shanhh.asr.assist.web.controller;

import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author shanhonghao
 * @since
 */
@Controller
@RequestMapping(value = "api/exceptions")
public class BaseController {

        @PostMapping(value = "bindException")
        BaseResponse bindException(@Valid @ModelAttribute("form") Form form) {
            return new BaseResponse();
        }
        @Data

        static class Form {
            private int id;
        }

}
