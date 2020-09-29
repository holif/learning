package com.len.boot.mysql.controller;

import com.len.boot.mysql.model.Read;
import com.len.boot.mysql.service.ReadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: jone
 * @date: Created in 2020/9/11 4:23 下午
 * @description:
 */
@Slf4j
@RestController
@RequestMapping("/read")
public class ReadController {

    @Autowired
    private ReadService readService;

    @PostMapping("/update")
    public String update(@RequestParam String rid){
        return readService.update(rid);
    }

    @GetMapping("/get")
    public Object get(@RequestParam String rid){
        Read read = readService.get(rid);
        return read;
    }

}
