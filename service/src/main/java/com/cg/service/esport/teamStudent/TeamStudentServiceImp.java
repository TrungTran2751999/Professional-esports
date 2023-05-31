package com.cg.service.esport.teamStudent;

import com.cg.domain.esport.dto.StudentResponseDTO;
import com.cg.domain.esport.dto.StudentTeamResDTO;
import com.cg.domain.esport.dto.TeamResponseDTO;
import com.cg.domain.esport.dto.TeamStudentResDTO;
import com.cg.domain.esport.entities.*;
import com.cg.exception.DataInputException;
import com.cg.repository.esport.TeamStudentRepository;
import com.cg.service.esport.avartar.IAvartarService;
import com.cg.service.esport.student.IStudentService;
import com.cg.service.esport.teamTournament.ITeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamStudentServiceImp implements ITeamStudentService {
    @Autowired
    private IStudentService studentService;
    @Autowired
    private ITeamService teamService;
    @Autowired
    private IAvartarService avartarService;
    @Autowired
    private TeamStudentRepository teamStudentRepository;

    @Override
    public List<TeamStudent> findAll() {
        return teamStudentRepository.findAll();
    }

    @Override
    public TeamStudent getById(Long id) {
        return teamStudentRepository.getById(id);
    }

    @Override
    public Optional<TeamStudent> findById(Long id) {
        return teamStudentRepository.findById(id);
    }

    @Override
    public TeamStudent save(TeamStudent teamStudent) {
        return teamStudentRepository.save(teamStudent);
    }

    @Override
    public void remove(Long id) {
        teamStudentRepository.deleteById(id);
    }

    @Override
    public List<TeamStudentResDTO> findByStudent(Long studentId, Boolean deleted) {
        Optional<Student> studentOptional = studentService.findById(studentId);
        if(studentOptional.isPresent()){
            return teamStudentRepository.findByStudent(studentOptional.get())
                    .stream()
                    .map(teamStudent -> {
                        if(teamStudent.getDeleted() == deleted){
                            Date date = teamStudent.getUpdatedAt();
                            Avartar avartar = avartarService.findByTeam(teamStudent.getTeamTournament());
                            TeamResponseDTO teamResponseDTO = teamStudent.getTeamTournament().toTeamResponseDTO(avartar.toAvartarDTO());
                            return new TeamStudentResDTO()
                                    .setTeamResponseDTO(teamResponseDTO)
                                    .setDateJoin(date);
                        }
                        return null;
                    }).collect(Collectors.toList())
                    .stream()
                    .filter(Objects::nonNull).collect(Collectors.toList());
        }
        throw new DataInputException("notFoundStudent");
    }

    @Override
    public List<StudentTeamResDTO> findByTeam(Long teamId, Boolean deleted) {
        Optional<TeamTournament> teamOptional = teamService.findById(teamId);
        if(teamOptional.isPresent()){
            return teamStudentRepository.findByTeamTournament(teamOptional.get())
                    .stream()
                    .map(teamStudent -> {
                        if(teamStudent.getDeleted() == deleted) {
                            Date date = teamStudent.getUpdatedAt();
                            Avartar avartar = avartarService.findByStudent(teamStudent.getStudent());
                            StudentResponseDTO studentTeamResDTO = teamStudent.getStudent().toStudentResponseDTO(avartar.toAvartarDTO());
                            return new StudentTeamResDTO()
                                    .setDateJoin(date)
                                    .setStudentResponseDTO(studentTeamResDTO);
                        }
                        return null;
                    }).collect(Collectors.toList())
                    .stream()
                    .filter(Objects::nonNull).collect(Collectors.toList());
        }
        throw new DataInputException("notFoundTeam");
    }

    @Override
    public List<TeamStudentResDTO> getListTeamJoinedByCate(Long studentId, Long categoryId) {
        return teamStudentRepository.getListTeamJoinedByCate(studentId, categoryId)
                .stream()
                .map(teamStudent -> {
                    Date date = teamStudent.getUpdatedAt();
                    Avartar avartar = avartarService.findByTeam(teamStudent.getTeamTournament());
                    TeamResponseDTO teamResponseDTO = teamStudent.getTeamTournament().toTeamResponseDTO(avartar.toAvartarDTO());
                    return new TeamStudentResDTO()
                            .setTeamResponseDTO(teamResponseDTO)
                            .setDateJoin(date);
                }).collect(Collectors.toList());
    }

    @Override
    public void joinTeam(Long studentId, Long teamId) {
        Optional<TeamTournament> teamOptional = teamService.findById(teamId);
        if(!teamOptional.isPresent()) throw new DataInputException("notFoundTeam");
        Optional<Student> studentOptional = studentService.findById(studentId);
        if(!studentOptional.isPresent()) throw new DataInputException("notFoundStudent");

        List<TeamStudent> checkStudentJoin = teamStudentRepository.findByStudent(studentOptional.get());
        Category category = teamOptional.get().getCategory();
        for(int i=0; i<checkStudentJoin.size(); i++){
            if(Objects.equals(checkStudentJoin.get(i).getTeamTournament().getCategory().getId(), category.getId())){
                throw new DataInputException("studentJoinedSameCategoryTeam");
            }
        }
        TeamStudent teamStudent = new TeamStudent().setStudent(studentOptional.get())
                                                   .setTeamTournament(teamOptional.get());
        teamStudentRepository.save(teamStudent);
    }

    @Override
    public void acceptJoin(Long studentId, Long teamId) {
        Optional<TeamTournament> teamOptional = teamService.findById(teamId);
        if(!teamOptional.isPresent()) throw new DataInputException("notFoundTeam");
        Optional<Student> studentOptional = studentService.findById(studentId);
        if(!studentOptional.isPresent()) throw new DataInputException("notFoundStudent");

        List<TeamStudent> teamStudent = teamStudentRepository.findByStudentAndTeamTournament(
                studentOptional.get(),
                teamOptional.get()
        );
        if(teamStudent.size() > 0){
            teamStudentRepository.save((TeamStudent) teamStudent.get(0).setDeleted(false));
        }
    }

    @Override
    public void rejectJoin(Long studentId, Long teamId) {
        Optional<TeamTournament> teamOptional = teamService.findById(teamId);
        if(!teamOptional.isPresent()) throw new DataInputException("notFoundTeam");
        Optional<Student> studentOptional = studentService.findById(studentId);
        if(!studentOptional.isPresent()) throw new DataInputException("notFoundStudent");

        Student student = studentOptional.get();
        TeamTournament teamTournament = teamOptional.get();
        List<TeamStudent> teamStudent = teamStudentRepository.findByStudentAndTeamTournament(student, teamTournament);
        if(teamStudent.size() > 0){
            if(!teamStudent.get(0).getDeleted()){
                teamStudentRepository.delete(teamStudent.get(0));
            }else{
                throw new DataInputException("notDeleteMemberJoined");
            }
        }
    }

    @Override
    public void leaveTeam(Long studentId, Long teamId) {
        Optional<TeamTournament> teamOptional = teamService.findById(teamId);
        if(!teamOptional.isPresent()) throw new DataInputException("notFoundTeam");
        Optional<Student> studentOptional = studentService.findById(studentId);
        if(!studentOptional.isPresent()) throw new DataInputException("notFoundStudent");
        List<TeamStudent> teamStudent = teamStudentRepository.findByStudentAndTeamTournament(
                studentOptional.get(), teamOptional.get()
        );
        if(teamStudent.size() > 0){
            teamStudentRepository.delete(teamStudent.get(0));
        }
    }
}
