package com.cn.lp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Created by on 2019/8/1.
 */
@Repository
public interface TriggerDateDAO extends JpaRepository<TriggerData, Long> {

    Optional<TriggerData> findByName(String name);

}
