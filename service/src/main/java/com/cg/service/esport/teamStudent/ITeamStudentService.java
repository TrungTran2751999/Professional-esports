package com.cg.service.esport.teamStudent;

import com.cg.domain.esport.dto.*;
import com.cg.domain.esport.entities.TeamStudent;
import com.cg.service.IGeneralService;

import java.util.List;

public interface ITeamStudentService extends IGeneralService<TeamStudent> {
    List<TeamStudentResDTO> findByStudent(Long studentId, Boolean deleted);
    List<StudentTeamResDTO> findByTeam(Long teamId, Boolean deleted);

    List<TeamStudentResDTO> getListTeamJoinedByCate(Long studentId, Long categoryId);
    void joinTeam(Long studentId, Long teamId);
    void acceptJoin(Long studentId, Long teamId);
    void rejectJoin(Long studentId, Long teamId);
    void leaveTeam(Long studentId, Long teamId);

}
