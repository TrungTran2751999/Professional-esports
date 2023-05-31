package com.cg.service.esport.teamTournament;

import com.cg.domain.esport.dto.*;
import com.cg.domain.esport.entities.TeamTournament;
import com.cg.service.IGeneralService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITeamService extends IGeneralService<TeamTournament> {
    Page<TeamResponseDTO> filter(TeamFilter teamFilter, Pageable pageable);
    TeamResponseDTO createTeam(TeamDTO teamDTO);
    TeamResponseDTO findTeamById(Long id);
    List<StudentTeamResDTO> getAllStudentJoined(Long teamId, Boolean deleted);
    TeamResponseDTO updateNoAvartar(TeamUpdateDTO teamUpdateDTO);
    TeamResponseDTO updateAvartar(TeamAvartarDTO teamAvartarDTO);
    void setDeleted(Long id, Boolean deleted);
    void acceptJoin(TeamRequestJoin teamRequestJoin);
    void rejectJoin(TeamRequestJoin teamRequestJoin);
    void joinTournament(TeamJoinTourDTO teamJoinTourDTO);
    void leaveTournament(TeamJoinTourDTO teamJoinTourDTO);
}
