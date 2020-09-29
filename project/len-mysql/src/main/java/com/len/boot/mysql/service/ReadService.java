package com.len.boot.mysql.service;

import com.len.boot.mysql.dao.ReadRepository;
import com.len.boot.mysql.model.Read;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: jone
 * @date: Created in 2020/9/11 4:25 下午
 * @description:
 */
@Service("ReadService")
public class ReadService {

    @Autowired
    private ReadRepository readRepository;


    public String update(String rid){
        int i = readRepository.updateReadNumByRid(rid);
        if(i>0){
            return "ok";
        } else {
            return "error";
        }
    }

    public Read get(String rid){
        return readRepository.getOne(rid);
    }


}
