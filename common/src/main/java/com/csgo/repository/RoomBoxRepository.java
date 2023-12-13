package com.csgo.repository;

import com.csgo.domain.RoomBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomBoxRepository extends JpaRepository<RoomBox, Integer> {

}
