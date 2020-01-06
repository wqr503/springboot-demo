package com.cn.lp.controller;

import com.cn.lp.entity.User;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qirong
 * @date 2019/4/26
 */
@Validated
@RestController
public class TestController {

    @GetMapping("test")
    @ApiOperation(value = "测试", notes = "测试")
    public String test() {
        return "Hello World";
    }

    @PostMapping("testPostEntity")
    @ApiOperation(value = "测试", notes = "测试")
    @ResponseBody
    public Map<String, String> testPostEntity(@Valid @RequestBody User user) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", user.getFirstname() + " " + user.getLastname());
        return map;
    }

    @PostMapping("testPostForm")
    @ApiOperation(value = "测试", notes = "测试")
    @ResponseBody
    public String testPostForm(@Email(message = "邮箱格式错误")@RequestParam(value = "firstName") String firstName, @RequestParam(value = "lastName") String lastName) {
        return firstName + ":" + lastName;
    }

    @PostMapping("testPostFileForm")
    @ApiOperation(value = "测试", notes = "测试")
    public String testPostFileForm(
        @RequestParam(value = "firstName") String firstName,
        @RequestParam(value = "lastName") String lastName,
        @RequestParam(value = "uploadFile") MultipartFile uploadFile
    ) {
        return firstName + ":" + lastName;
    }

    @PostMapping("/multiUpload")
    @ResponseBody
    public String multiUpload(HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (file.isEmpty()) {
                return "上传第" + (i++) + "个文件失败";
            }
            String fileName = file.getOriginalFilename();
            System.out.println(fileName);
        }
        return "上传成功";
    }

    @PostMapping("testPostEntityForm")
    @ApiOperation(value = "测试", notes = "测试")
    public String testPostEntityForm(User user) {
        return user.getFirstname() + ":" + user.getLastname();
    }
}
