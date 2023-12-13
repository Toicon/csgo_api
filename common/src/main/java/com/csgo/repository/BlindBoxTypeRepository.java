package com.csgo.repository;

import com.csgo.domain.BlindBoxType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlindBoxTypeRepository extends JpaRepository<BlindBoxType, Integer> {

}
