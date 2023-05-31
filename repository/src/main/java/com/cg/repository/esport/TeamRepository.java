package com.cg.repository.esport;

import com.cg.domain.esport.entities.Student;
import com.cg.domain.esport.entities.TeamTournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<TeamTournament, Long> {
    List<TeamTournament> findByName(String name);
    List<TeamTournament> findByLeader(Student student);
    TeamTournament findByIdAndLeader(Long id, Student student);
}
