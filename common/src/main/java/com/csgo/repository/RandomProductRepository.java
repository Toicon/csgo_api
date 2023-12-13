package com.csgo.repository;

import com.csgo.domain.RandomProductDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RandomProductRepository extends JpaRepository<RandomProductDO, Integer> {

}
