package com.cg.repository.esport;

import com.cg.domain.esport.entities.TeamJoinTour;
import com.cg.domain.esport.entities.TeamTournament;
import com.cg.domain.esport.entities.Tournament;
import com.cg.domain.esport.enums.EnumStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamJoinTourRepository extends JpaRepository<TeamJoinTour,Long> {
    List<TeamJoinTour> findByTournamentAndTeam(Tournament tournament, TeamTournament teamTournament);
    List<TeamJoinTour> findByTournament(Tournament tournament);
    List<TeamJoinTour> findByTournamentAndState(Tournament tournament, EnumStatus state);
}
