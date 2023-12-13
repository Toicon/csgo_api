package com.csgo.repository;

import com.csgo.domain.BlindBoxProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlindBoxProductRepository extends JpaRepository<BlindBoxProduct, Integer> {

}
