package com.csgo.repository;

import com.csgo.domain.user.UserCommissionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCommissionLogRepository extends JpaRepository<UserCommissionLog, Integer> {

}
