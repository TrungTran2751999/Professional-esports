package com.cg.service.esport.teamJoinTour;

import com.cg.domain.esport.dto.TeamJoinTourDTO;
import com.cg.domain.esport.dto.TeamJoinTourResDTO;
import com.cg.domain.esport.enums.EnumStatus;

import java.util.List;

public interface ITeamJoinTourService {
    List<TeamJoinTourResDTO> findTeamJoin(Long tourId, EnumStatus state);
    void joinTour(TeamJoinTourDTO TeamJoinTourDTO);
    void leaveTour(TeamJoinTourDTO TeamJoinTourDTO);
    void acceptJoinTour(TeamJoinTourDTO teamJoinTourDTO);
    void rejectJoinTour(TeamJoinTourDTO teamJoinTourDTO);
}
