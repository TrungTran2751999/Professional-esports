package com.cg.service.esport.teamTournament;

import com.cg.domain.esport.dto.*;
import com.cg.domain.esport.entities.*;
import com.cg.exception.DataInputException;
import com.cg.repository.esport.TeamFilterRepository;
import com.cg.repository.esport.TeamRepository;
import com.cg.service.esport.avartar.IAvartarService;
import com.cg.service.esport.category.ICategoryService;
import com.cg.service.esport.student.IStudentService;
import com.cg.service.esport.teamJoinTour.ITeamJoinTourService;
import com.cg.service.esport.teamStudent.ITeamStudentService;
import com.cg.service.esport.user.IUserService;
import com.cg.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Service
public class TeamServiceImp implements ITeamService {
    @Autowired
    private TeamFilterRepository teamFilterRepository;
    @Autowired
    private IAvartarService avartarService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IStudentService studentService;

    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private AppUtils appUtils;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private ITeamStudentService teamStudentService;
    @Autowired
    private ITeamJoinTourService teamJoinTourService;
    @Override
    public List<TeamTournament> findAll() {
        return teamRepository.findAll();
    }

    @Override
    public TeamTournament getById(Long id) {
        return teamRepository.getById(id);
    }

    @Override
    public Optional<TeamTournament> findById(Long id) {
        return teamRepository.findById(id);
    }

    @Override
    public TeamTournament save(TeamTournament teamTournament) {
        return teamRepository.save(teamTournament);
    }

    @Override
    public void remove(Long id) {
        teamRepository.deleteById(id);
    }

    @Override
    public Page<TeamResponseDTO> filter(TeamFilter teamFilter, Pageable pageable) {
        return teamFilterRepository.findAllByFilters(teamFilter, pageable).map(teamTournament -> {
            Avartar avartar = avartarService.findByTeam(teamTournament);
            return teamTournament.toTeamResponseDTO(avartar.toAvartarDTO());
        });
    }

    @Override
    @Transactional
    public TeamResponseDTO createTeam(TeamDTO teamDTO) {
        User user = userService.findByCodeSecurity(teamDTO.getCode());
        if(!Objects.equals(user.getId(), teamDTO.getLeaderUserId())) throw new DataInputException("notFoundStudent");

        StudentResSecurity leaderOpt = studentService.findByAdmin(user);
        if(leaderOpt == null) throw new DataInputException("notFoundStudent");

        Optional<Category> categoryOpt = categoryService.findById(teamDTO.getCategoryId());
        if(!categoryOpt.isPresent()) throw new DataInputException("notFoundCategory");

        List<TeamTournament> checkByName = teamRepository.findByName(teamDTO.getName());
        if(checkByName.size() > 0) throw new DataInputException("nameIsExist");

        List<TeamStudentResDTO> listTeamJoined = teamStudentService.findByStudent(leaderOpt.getId(), false);
        for(int i=0; i < listTeamJoined.size(); i++){
            if(Objects.equals(categoryOpt.get().getId(), listTeamJoined.get(i).getTeamResponseDTO().getCategoryId())){
                throw new DataInputException("studentJoinedSameCategoryTeam");
            }
        }
        TeamTournament team = new TeamTournament()
                                    .setName(teamDTO.getName())
                                    .setLeader(leaderOpt.toStudent().setUser(user))
                                    .setCategory(categoryOpt.get());
        team = teamRepository.save((TeamTournament) team.setDeleted(false));
        Avartar avartar = new Avartar();
        avartar = avartarService.save(avartar.setTeamTournament(team));
        avartar.setFileUrl(appUtils.uploadAvartar(teamDTO.getAvartar(), avartar.getId()));
        TeamStudent teamStudent = new TeamStudent()
                                    .setStudent(leaderOpt.toStudent().setUser(user))
                                    .setTeamTournament(team);
        teamStudentService.save((TeamStudent) teamStudent.setDeleted(false));
        return team.toTeamResponseDTO(avartar.toAvartarDTO());
    }

    @Override
    public TeamResponseDTO findTeamById(Long id) {
        TeamTournament teamTournament = teamRepository.getById(id);
        Avartar avartar = avartarService.findByTeam(teamTournament);
        return teamTournament.toTeamResponseDTO(avartar.toAvartarDTO());
    }

    @Override
    public List<StudentTeamResDTO> getAllStudentJoined(Long teamId, Boolean deleted) {

        return teamStudentService.findByTeam(teamId, deleted);
    }

    @Override
    @Transactional
    public TeamResponseDTO updateNoAvartar(TeamUpdateDTO teamUpdateDTO) {
        User user = userService.findByCodeSecurity(teamUpdateDTO.getCode());
        if(!Objects.equals(user.getId(), teamUpdateDTO.getUserId())) throw new DataInputException("notFoundStudent");

        Optional<TeamTournament> teamTournamentOpt = teamRepository.findById(teamUpdateDTO.getTeamId());
        if(!teamTournamentOpt.isPresent()) throw new DataInputException("notFoundTeam");

        List<TeamTournament> checkByName = teamRepository.findByName(teamUpdateDTO.getName());
        if(checkByName.size() > 0 && !Objects.equals(teamTournamentOpt.get().getName(), checkByName.get(0).getName())) throw new DataInputException("nameIsExist");

        StudentResSecurity student = studentService.findByAdmin(user);
        TeamTournament teamTournament = teamTournamentOpt.get();

        if(!Objects.equals(teamTournament.getLeader().getId(), student.getId())) throw new DataInputException("notFoundLeader");

        teamTournament = teamRepository.save((TeamTournament) teamUpdateDTO.toTeamTournament()
                                                .setLeader(student.toStudent())
                                                .setCategory(teamTournament.getCategory())
                                                .setDeleted(false));
        Avartar avartar = avartarService.findByTeam(teamTournament);
        return teamTournament.toTeamResponseDTO(avartar.toAvartarDTO());

    }

    @Override
    @Transactional
    public TeamResponseDTO updateAvartar(TeamAvartarDTO teamAvartarDTO) {
        User user = userService.findByCodeSecurity(teamAvartarDTO.getCode());
        if(!Objects.equals(user.getId(), teamAvartarDTO.getUserId())) throw new DataInputException("notFoundStudent");

        Optional<TeamTournament> teamTournamentOpt = teamRepository.findById(teamAvartarDTO.getTeamId());
        if(!teamTournamentOpt.isPresent()) throw new DataInputException("notFoundTeam");

        StudentResSecurity student = studentService.findByAdmin(user);
        TeamTournament teamTournament = teamTournamentOpt.get();
        if(!Objects.equals(teamTournament.getLeader().getId(), student.getId())) throw new DataInputException("notFoundLeader");

        Avartar avartar = avartarService.findByTeam(teamTournament);
        String fileIdDelete = avartar.getFileUrl();
        avartar.setFileUrl(appUtils.uploadAvartar(teamAvartarDTO.getAvartar(), avartar.getId()));
        if(fileIdDelete != null){
            String fileId = fileIdDelete.split("&")[1].split("=")[1];
            try {
                appUtils.deleteFile(fileId);
            } catch (Exception e) {
                throw new DataInputException("FAIL");
            }
        }
       return teamTournament.toTeamResponseDTO(avartar.toAvartarDTO());
    }

    @Override
    @Transactional
    public void setDeleted(Long id, Boolean deleted) {
        Optional<TeamTournament> teamTournamentOpt = teamRepository.findById(id);
        if(!teamTournamentOpt.isPresent()) throw new DataInputException("notFoundTeam");
        TeamTournament teamTournament = (TeamTournament) teamTournamentOpt.get().setDeleted(deleted);
        teamRepository.save(teamTournament);
    }

    @Override
    public void acceptJoin(TeamRequestJoin teamRequestJoin) {
        User user = userService.findByCodeSecurity(teamRequestJoin.getCode());
        if(user == null) throw new DataInputException("notFoundStudent");

        Optional<TeamTournament> teamTournamentOpt = teamRepository.findById(teamRequestJoin.getTeamId());
        if(!teamTournamentOpt.isPresent()) throw new DataInputException("notFoundStudent");

        if(!Objects.equals(user.getId(), teamRequestJoin.getUserId())) throw new DataInputException("notFoundStudent");

        Optional<Student> joinerOpt = studentService.findById(teamRequestJoin.getJoinId());
        if(!joinerOpt.isPresent()) throw new DataInputException("notFoundJoiner");

        List<TeamStudentResDTO> checkJoined = teamStudentService.findByStudent(joinerOpt.get().getId(), false);
        for(int i=0; i< checkJoined.size(); i++){
            if(Objects.equals(teamTournamentOpt.get().getCategory().getId(), checkJoined.get(i).getTeamResponseDTO().getCategoryId())){
                throw new DataInputException("studentJoinedSameCategoryTeam");
            }
        }

        StudentResSecurity student = studentService.findByAdmin(user);
        if(!Objects.equals(student.getId(), teamTournamentOpt.get().getLeader().getId())) throw new DataInputException("notLeader");

        teamStudentService.acceptJoin(teamRequestJoin.getJoinId(), teamRequestJoin.getTeamId());
    }

    @Override
    public void rejectJoin(TeamRequestJoin teamRequestJoin) {
        User user = userService.findByCodeSecurity(teamRequestJoin.getCode());
        if(user == null) throw new DataInputException("notFoundStudent");

        Optional<TeamTournament> teamTournamentOpt = teamRepository.findById(teamRequestJoin.getTeamId());
        if(!teamTournamentOpt.isPresent()) throw new DataInputException("notFoundStudent");

        if(!Objects.equals(user.getId(), teamRequestJoin.getUserId())) throw new DataInputException("notFoundStudent");

        Optional<Student> joinerOpt = studentService.findById(teamRequestJoin.getJoinId());
        if(!joinerOpt.isPresent()) throw new DataInputException("notFoundJoiner");

        StudentResSecurity student = studentService.findByAdmin(user);
        if(!Objects.equals(student.getId(), teamTournamentOpt.get().getLeader().getId())) throw new DataInputException("notLeader");

        if(Objects.equals(student.getId(), joinerOpt.get().getId())) throw new DataInputException("notRejectLeader");

        List<StudentTeamResDTO> listStudent = teamStudentService.findByTeam(teamRequestJoin.getTeamId(), false);
        for(int i=0; i<listStudent.size(); i++){
            if(Objects.equals(listStudent.get(i).getStudentResponseDTO().getId(), teamRequestJoin.getJoinId())){
                teamStudentService.rejectJoin(teamRequestJoin.getJoinId(), teamRequestJoin.getTeamId());
                return;
            }
        }
        throw new DataInputException("notFoundStudentInTeam");

    }

    @Override
    public void joinTournament(TeamJoinTourDTO teamJoinTourDTO) {
        teamJoinTourService.joinTour(teamJoinTourDTO);
    }

    @Override
    public void leaveTournament(TeamJoinTourDTO teamJoinTourDTO) {
        teamJoinTourService.leaveTour(teamJoinTourDTO);
    }


}
