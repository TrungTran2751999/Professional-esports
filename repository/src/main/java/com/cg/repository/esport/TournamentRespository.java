package com.cg.repository.esport;

import com.cg.domain.esport.entities.Organizer;
import com.cg.domain.esport.entities.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TournamentRespository extends JpaRepository<Tournament,Long> {
    Tournament findByIdAndOrganizer(Organizer organizer, Long id);
}
