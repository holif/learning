package com.len.boot.mysql.dao;

import com.len.boot.mysql.model.Read;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: jone
 * @date: Created in 2020/9/11 4:40 下午
 * @description:
 */
@Repository
public interface ReadRepository extends JpaRepository<Read,String> {

    @Modifying
    @Transactional
    @Query(value = "update read_artile set read_num=read_num+1 where rid=?1",nativeQuery = true)
    int updateReadNumByRid(String rid);
}
