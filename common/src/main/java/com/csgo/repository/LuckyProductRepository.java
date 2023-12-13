package com.csgo.repository;

import com.csgo.domain.LuckyProductDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LuckyProductRepository extends JpaRepository<LuckyProductDO, Integer> {

}
