package com.cg.repository.esport;

import com.cg.domain.esport.entities.TournamentTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourTableRepository extends JpaRepository<TournamentTable,Long> {
}
