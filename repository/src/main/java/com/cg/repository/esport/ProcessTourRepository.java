package com.cg.repository.esport;

import com.cg.domain.esport.entities.ProcessTournament;
import com.cg.domain.esport.entities.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessTourRepository extends JpaRepository<ProcessTournament,Long> {
    ProcessTournament findByTournament(Tournament tournament);
}
