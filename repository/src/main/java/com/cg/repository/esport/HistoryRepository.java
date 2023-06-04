package com.cg.repository.esport;

import com.cg.domain.esport.entities.HistoryTeamTournament;
import com.cg.domain.esport.entities.TeamTournament;
import com.cg.domain.esport.entities.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface HistoryRepository extends JpaRepository<HistoryTeamTournament,Long> {
    List<HistoryTeamTournament> findByTeamAOrTeamB(TeamTournament teamA, TeamTournament teamB);
    @Query(value = "SELECT * FROM history_team_tournament WHERE" +
            "((teamA=:teamAId AND teamB=:teamBId) OR(teamA=:teamBId AND teamB=:teamAId))" +
            "AND created_at=:date AND tournament=:tourId",nativeQuery = true)
    HistoryTeamTournament findByTeamIdAndDate(@Param("teamAId") Long teamAId,
                                              @Param("teamBId") Long teamBId,
                                              @Param("date") Date date,
                                              @Param("tourId") Long tourId);
    @Query(value = "SELECT h.* FROM professional_esports.history_team_tournament AS h " +
            "JOIN team_join_tournament AS tJ ON (h.teama = tJ.team_id OR h.teamb = tJ.team_id)" +
            " WHERE tj.state = 'REMOVE' AND tJ.team_id=:teamId", nativeQuery = true)
    List<HistoryTeamTournament> checkTeamRemove(@Param("teamId") Long teamId);
}
