package com.cg.service.esport.teamJoinTour;

import com.cg.domain.esport.dto.TeamJoinTourDTO;
import com.cg.domain.esport.dto.TeamJoinTourResDTO;
import com.cg.domain.esport.entities.*;
import com.cg.domain.esport.enums.EnumStatus;
import com.cg.exception.DataInputException;
import com.cg.repository.esport.*;
import com.cg.service.esport.avartar.IAvartarService;
import com.cg.service.esport.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class TeamJoinTourServiceImp implements ITeamJoinTourService{
    @Autowired
    private TeamJoinTourRepository teamJoinTourRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private IUserService userService;
    @Autowired
    private IAvartarService avartarService;

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ProcessTourRepository processTourRepo;
    @Autowired
    private TournamentRespository tournamentRespo;
    @Autowired
    private OrganizerRepository organizerRepository;


    @Override
    public List<TeamJoinTourResDTO> findTeamJoin(Long tourId, EnumStatus state) {
        Optional<Tournament> tournamentOpt = tournamentRespo.findById(tourId);
        if (!tournamentOpt.isPresent()) throw new DataInputException("notFoundTour");
        return teamJoinTourRepository.findByTournamentAndState(tournamentOpt.get(), state)
                .stream()
                .map(teamJoinTour -> {
                    Avartar avartar = avartarService.findByTeam(teamJoinTour.getTeam());
                    return teamJoinTour.toTeamJoinTourResDTO(avartar.toAvartarDTO());
                }).collect(Collectors.toList());
    }
    @Override
    public void joinTour(TeamJoinTourDTO teamJoinTourDTO) {
        User user = userService.findByCodeSecurity(teamJoinTourDTO.getCode());
        if(user==null) throw new DataInputException("notFoundStudent");

        if(!Objects.equals(user.getId(), teamJoinTourDTO.getUserId()))
            throw new DataInputException("notFoundStudent");

        Student student = studentRepository.findByUser(user);
        if(student==null) throw new DataInputException("notFoundStudent");

        TeamTournament team = teamRepository.findByIdAndLeader(teamJoinTourDTO.getTeamId(), student);
        if(team == null) throw new DataInputException("notFoundLeader");
        if(team.getDeleted()) throw new DataInputException("teamIsBanned");

        Optional<Tournament> tournamentOpt = tournamentRespo.findById(teamJoinTourDTO.getTourId());
        if(!tournamentOpt.isPresent()) throw new DataInputException("notFoundTournament");

        Tournament tournament = tournamentOpt.get();
        if(!Objects.equals(team.getCategory().getId(), tournament.getCategory().getId()))
            throw new DataInputException("notRightCategory");

        ProcessTournament processTournament = processTourRepo.findByTournament(tournament);
        if(processTournament==null) throw new DataInputException("notFoundProcess");

        if(new Date().compareTo(processTournament.getRegisterFinish()) > 0)
            throw new DataInputException("RegisterOver");

        List<TeamJoinTour> checkTeamJoinTourRegis = teamJoinTourRepository.findByTeamAndState(team,EnumStatus.REGISTER);
        if(!checkTeamJoinTourRegis.isEmpty()) throw new DataInputException("teamRegistered");

        List<TeamJoinTour> checkTeamJoinTourConfirm = teamJoinTourRepository.findByTeamAndState(team,EnumStatus.CONFIRM);
        if(!checkTeamJoinTourConfirm.isEmpty()) throw new DataInputException("teamJoined");

        List<TeamJoinTour> checkLimit = teamJoinTourRepository.findByTournament(tournament);
        if(checkLimit.size()+1 > tournament.getJoinLimit()) throw new DataInputException("TeamInTourOver");

        TeamJoinTour teamJoinTour = new TeamJoinTour()
                                        .setTeam(team)
                                        .setTournament(tournament)
                                        .setCreateBy(new Date())
                                        .setState(EnumStatus.REGISTER);
        teamJoinTourRepository.save(teamJoinTour);
    }

    @Override
    public void leaveTour(TeamJoinTourDTO teamJoinTourDTO) {
        User user = userService.findByCodeSecurity(teamJoinTourDTO.getCode());
        if(user==null) throw new DataInputException("notFoundStudent");

        if(!Objects.equals(user.getId(), teamJoinTourDTO.getUserId()))
            throw new DataInputException("notFoundStudent");

        Student student = studentRepository.findByUser(user);
        if(student==null) throw new DataInputException("notFoundStudent");

        TeamTournament team = teamRepository.findByIdAndLeader(teamJoinTourDTO.getTeamId(), student);
        if(team == null) throw new DataInputException("notFoundLeader");

        Optional<Tournament> tournamentOpt = tournamentRespo.findById(teamJoinTourDTO.getTourId());
        if(!tournamentOpt.isPresent()) throw new DataInputException("notFoundTournament");

        Tournament tournament = tournamentOpt.get();
        if(!Objects.equals(team.getCategory().getId(), tournament.getCategory().getId()))
            throw new DataInputException("notRightCategory");

        ProcessTournament processTournament = processTourRepo.findByTournament(tournament);
        if(processTournament==null) throw new DataInputException("notFoundProcess");

        List<TeamJoinTour> checkTeamJoinTourConfirm = teamJoinTourRepository.findByTeamAndState(team,EnumStatus.CONFIRM);
        if(!checkTeamJoinTourConfirm.isEmpty()) throw new DataInputException("teamJoined");

        List<TeamJoinTour> checkTeamJoinTourRegis = teamJoinTourRepository.findByTeamAndState(team,EnumStatus.REGISTER);
        if(checkTeamJoinTourRegis.isEmpty()) throw new DataInputException("teamRegistered");

        if(new Date().compareTo(processTournament.getRegisterFinish()) > 0)
            throw new DataInputException("notLeaveWhenRegister");

        teamJoinTourRepository.delete(checkTeamJoinTourRegis.get(0));
    }

    @Override
    public void acceptJoinTour(TeamJoinTourDTO teamJoinTourDTO) {
        User user = userService.findByCodeSecurity(teamJoinTourDTO.getCode());
        if(user==null) throw new DataInputException("notFoundOrganizer");

        if(!Objects.equals(user.getId(), teamJoinTourDTO.getUserId()))
            throw new DataInputException("notFoundOrganizer");

        Organizer organizer = organizerRepository.getByUser(user);
        if(organizer==null) throw new DataInputException("notFoundOrganizer");


        Optional<TeamTournament> teamOpt = teamRepository.findById(teamJoinTourDTO.getTeamId());
        if(!teamOpt.isPresent()) throw new DataInputException("notFoundTeam");

        TeamTournament team = teamOpt.get();

        if(!Objects.equals(team.getId(), teamJoinTourDTO.getTeamId()))
            throw new DataInputException("notLeaderInTeam");

        Tournament tournament = tournamentRespo.findByIdAndOrganizer(teamJoinTourDTO.getTourId(),organizer);
        if(tournament==null) throw new DataInputException("notFoundOrganizer");

        List<TeamJoinTour> checkTeamJoinr = teamJoinTourRepository.findByTournamentAndTeam(tournament,team);
        if(checkTeamJoinr.isEmpty()) throw new DataInputException("teamNotRegistered");

        TeamJoinTour teamJoinTour = checkTeamJoinr.get(0).setState(EnumStatus.CONFIRM);
        teamJoinTourRepository.save(teamJoinTour);
    }

    @Override
    public void rejectJoinTour(TeamJoinTourDTO teamJoinTourDTO) {
        User user = userService.findByCodeSecurity(teamJoinTourDTO.getCode());
        if(user==null) throw new DataInputException("notFoundOrganizer");

        if(!Objects.equals(user.getId(), teamJoinTourDTO.getUserId()))
            throw new DataInputException("notFoundOrganizer");

        Organizer organizer = organizerRepository.getByUser(user);
        if(organizer==null) throw new DataInputException("notFoundOrganizer");

        Optional<TeamTournament> teamOpt = teamRepository.findById(teamJoinTourDTO.getTeamId());
        if(!teamOpt.isPresent()) throw new DataInputException("notFoundTeam");

        TeamTournament team = teamOpt.get();

        if(!Objects.equals(team.getId(), teamJoinTourDTO.getTeamId()))
            throw new DataInputException("notLeaderInTeam");

        Tournament tournament = tournamentRespo.findByIdAndOrganizer(teamJoinTourDTO.getTourId(),organizer);
        if(tournament==null) throw new DataInputException("notFoundOrganizer");

        List<TeamJoinTour> checkTeamJoinr = teamJoinTourRepository.findByTournamentAndTeam(tournament,team);
        if(checkTeamJoinr.isEmpty()) throw new DataInputException("teamNotRegistered");

        TeamJoinTour teamJoinTour = checkTeamJoinr.get(0).setState(EnumStatus.CONFIRM);
        teamJoinTourRepository.delete(teamJoinTour);
    }

}
