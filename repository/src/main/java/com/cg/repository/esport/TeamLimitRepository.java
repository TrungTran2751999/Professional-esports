package com.cg.repository.esport;

import com.cg.domain.esport.entities.TeamLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamLimitRepository extends JpaRepository<TeamLimit,Long> {
    List<TeamLimit> findByMax(Integer max);
}
