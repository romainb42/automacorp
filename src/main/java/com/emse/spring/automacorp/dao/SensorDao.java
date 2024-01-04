package com.emse.spring.automacorp.dao;

import com.emse.spring.automacorp.model.SensorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SensorDao extends JpaRepository<SensorEntity, Long> {

    @Query("select c from SensorEntity c where c.id=:id")
    SensorEntity findById(@Param("id") String id);

    @Query("select c from SensorEntity c where c.name=:name")
    SensorEntity findByName(@Param("name") String name);

    @Modifying
    @Query("delete from SensorEntity c where c.name = ?1")
    default void deleteByName(String name) {
    }
}
