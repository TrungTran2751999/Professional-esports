package com.cg.service.esport.tourTable;

import com.cg.domain.esport.dto.HistoryDTO;
import com.cg.domain.esport.dto.TeamResponseDTO;
import com.cg.domain.esport.dto.TourTableDTO;
import com.cg.domain.esport.dto.TourTableResDTO;
import com.cg.domain.esport.dto.coupleInfor.CoupleInfor;
import com.cg.domain.esport.dto.coupleInfor.CoupleInforRes;
import com.cg.domain.esport.dto.coupleInfor.Round;
import com.cg.domain.esport.dto.coupleInfor.RoundDTO;
import com.cg.domain.esport.entities.*;
import com.cg.domain.esport.enums.EnumStatus;
import com.cg.exception.DataInputException;
import com.cg.repository.esport.*;
import com.cg.service.esport.avartar.IAvartarService;
import com.cg.service.esport.history.IHistoryService;
import com.cg.service.esport.organizer.IOrganizerService;
import com.cg.service.esport.processTour.IProcessTourService;
import com.cg.service.esport.teamJoinTour.ITeamJoinTourService;
import com.cg.service.esport.teamTournament.ITeamService;
import com.cg.service.esport.user.IUserService;
import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TourTableServiceImp implements ITourTableService{
    @Autowired
    private TourTableRepository tourTableRepository;
    @Autowired
    private IUserService userService;
    @Autowired
    private OrganizerRepository organizerRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamJoinTourRepository teamJoinTourRepository;
    @Autowired
    private TournamentRespository tournamentRespository;

    @Autowired
    private IAvartarService avartarService;
    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private IProcessTourService processTourService;
    @Autowired
    private ProcessTourRepository processTourRepository;


    @Override
    public TourTableResDTO findById(Long tourId) {
        Optional<Tournament> tourOpt = tournamentRespository.findById(tourId);
        if(!tourOpt.isPresent()) throw new DataInputException("notFoundTour");

        TournamentTable tourTable = tourTableRepository.findByTournament(tourOpt.get());
        if(tourTable == null) throw new DataInputException("notFoundTable");

        return convertTourResDTO(tourTable,tourOpt.get());
    }

    @Override
    @Transactional
    public TourTableResDTO createOrUpdate(TourTableDTO tourTableDTO) {
        User user = userService.findByCodeSecurity(tourTableDTO.getCode());
        if(user==null) throw new DataInputException("notFoundOrganizer");

        if(!Objects.equals(user.getId(), tourTableDTO.getUserId()))
            throw new DataInputException("notFoundOrganizer");

        Organizer organizer = organizerRepository.getByUser(user);
        if(organizer==null) throw new DataInputException("notFoundOrganizer");

        Tournament tournament = tournamentRespository.findByIdAndOrganizer(tourTableDTO.getTourId(), organizer);
        if(tournament==null) throw new DataInputException("notFoundTournament");

        List<TeamJoinTour> listConfirm = teamJoinTourRepository.findByTournamentAndState(tournament, EnumStatus.CONFIRM);
        if(listConfirm.size() < tournament.getJoinLimit() && !tournament.getBegining()){
            throw new DataInputException("joinNotEnough");
        }
        if(listConfirm.size() == tournament.getJoinLimit()){
            tournamentRespository.save(tournament.setBegining(true));
        }

        List<Round> eliminateRound = tourTableDTO.getEliminateRound();
        List<RoundDTO> eliminateDTO = new ArrayList<>();
        if(eliminateRound != null){
            for(int i=0; i< eliminateRound.size(); i++){
                List<CoupleInforRes> listCoupleInforRes = checkInforRound(eliminateRound.get(i),tournament);
                String name = eliminateRound.get(i).getName();
                RoundDTO roundDTO = new RoundDTO()
                        .setName(name)
                        .setListCouple(listCoupleInforRes);
                eliminateDTO.add(roundDTO);
            }
        }

        Round quater = tourTableDTO.getQuarterRound();
        RoundDTO quaterDTO = new RoundDTO();
        if(!quater.getListCouple().isEmpty()){
           quaterDTO = new RoundDTO()
                    .setName(quater.getName())
                    .setListCouple(checkInforRound(quater, tournament));
        }

        Round semi = tourTableDTO.getSemiRound();
        RoundDTO semiDTO = new RoundDTO();
        if(semi!=null){
            semiDTO = new RoundDTO()
                    .setName(semi.getName())
                    .setListCouple(checkInforRound(semi, tournament));
        }

        Round conclusion = tourTableDTO.getFinalRound();
        RoundDTO finalDTO = new RoundDTO();
        if(conclusion!=null){
            finalDTO = new RoundDTO()
                    .setName(conclusion.getName())
                    .setListCouple(checkInforRound(conclusion, tournament));
        }
        TournamentTable tournamentTable = tourTableRepository.save(
                tourTableDTO.toTournamentTable(tournament)
        );

        return new TourTableResDTO()
                .setId(tournamentTable.getId())
                .setEliminate(eliminateDTO)
                .setQuater(quaterDTO)
                .setSemi(semiDTO)
                .setConclusion(finalDTO);
    }
    @Override
    @Transactional
    public List<CoupleInforRes> checkInforRound(Round round, Tournament tournament){
        List<CoupleInforRes> listCoupleRes = new ArrayList<>();
        List<CoupleInfor> listCouple = round.getListCouple();
        for(int i=0; i< listCouple.size(); i++){
            CoupleInfor coupleInfor = listCouple.get(i);

            Long teamAId = coupleInfor.getTeamAId();
            Long teamBId = coupleInfor.getTeamBId();
            if (Objects.equals(teamAId, teamBId) && teamAId != 0 && teamBId!=0)
                throw new DataInputException("teamANotEqualteamB");

            Integer scoreA = coupleInfor.getScoreA();
            Integer scoreB = coupleInfor.getScoreB();
            if(Objects.equals(scoreA, scoreB) && scoreA!=0 && scoreB!=0)
                throw new DataInputException("scoreAAndscoreBnotEqual");

            ProcessTournament processTournament = processTourRepository.findByTournament(tournament);
            if(processTournament==null) throw new DataInputException("notFoundProcess");

            Date dateStart = processTournament.getStartFinish();
            Date dateEnd = processTournament.getEndFinish();

            long deltaStart = processTourService.countDate(coupleInfor.getCreateAt(),dateStart);
            long deltaEnd = processTourService.countDate(coupleInfor.getCreateAt(),dateEnd);

            if(coupleInfor.getTeamAId()==null) throw new DataInputException("teamAIdNotNull");
            if(coupleInfor.getTeamBId()==null) throw new DataInputException("teamBIdNotNull");

            TeamResponseDTO teamA = new TeamResponseDTO();
            TeamResponseDTO teamB = new TeamResponseDTO();

            if(coupleInfor.getTeamAId()!=0 && coupleInfor.getTeamBId()!=0){
                Optional<TeamTournament> teamAOpt = teamRepository.findById(coupleInfor.getTeamAId());
                Optional<TeamTournament> teamBOpt = teamRepository.findById(coupleInfor.getTeamBId());

                if(!teamAOpt.isPresent()) throw new DataInputException("notFoundTeamA");
                if(!teamBOpt.isPresent()) throw new DataInputException("notFoundTeamB");

                List<TeamJoinTour> checkTeamA = teamJoinTourRepository.findByTournamentAndTeam(tournament, teamAOpt.get());
                if(checkTeamA.isEmpty()) throw new DataInputException("teamANotInTour");
                List<TeamJoinTour> checkTeamB = teamJoinTourRepository.findByTournamentAndTeam(tournament, teamBOpt.get());
                if(checkTeamB.isEmpty()) throw new DataInputException("teamBNotInTour");

                String nameA = teamAOpt.get().getName();
                String nameB = teamBOpt.get().getName();

                HistoryTeamTournament checkHistoryExist = historyRepository.findByTeamIdAndDate(
                        coupleInfor.getTeamAId(),
                        coupleInfor.getTeamBId(),
                        coupleInfor.getCreateAt(),
                        tournament.getId()
                );
                if(checkHistoryExist==null){
                    List<HistoryTeamTournament> checkRemoveA = historyRepository.checkTeamRemove(coupleInfor.getTeamAId());
                    List<HistoryTeamTournament> checkRemoveB = historyRepository.checkTeamRemove(coupleInfor.getTeamBId());

                    if(!checkRemoveA.isEmpty()) throw new DataInputException(nameA + "IsRemoved");
                    if(!checkRemoveB.isEmpty()) throw new DataInputException(nameB + "IsRemoved");

                    HistoryTeamTournament history = new HistoryTeamTournament()
                            .setTeamA(teamAOpt.get())
                            .setTeamB(teamBOpt.get())
                            .setScoreA(coupleInfor.getScoreA())
                            .setScoreB(coupleInfor.getScoreB())
                            .setTournament(tournament)
                            .setCreatedAt(coupleInfor.getCreateAt());
                    historyRepository.save(history);
                }else{
                    if(Objects.equals(checkHistoryExist.getScoreB(), checkHistoryExist.getScoreA())){
                        historyRepository.save(checkHistoryExist
                                .setScoreB(coupleInfor.getScoreB())
                                .setScoreA(coupleInfor.getScoreA()));
                    }
                }

                if(deltaStart < 0 && deltaEnd > 0) throw new DataInputException(nameA + " vs " +nameB+" isValidInDate");

                if(listCouple.size()==1){
                    if(coupleInfor.getScoreA() > coupleInfor.getScoreB()){
                        teamJoinTourRepository.save(checkTeamB.get(0).setState(EnumStatus.REMOVE));
                        teamJoinTourRepository.save(checkTeamA.get(0).setState(EnumStatus.WIN));
                    }else if(coupleInfor.getScoreA() < coupleInfor.getScoreB()){
                        teamJoinTourRepository.save(checkTeamA.get(0).setState(EnumStatus.REMOVE));
                        teamJoinTourRepository.save(checkTeamB.get(0).setState(EnumStatus.WIN));
                    }
                }
                if(coupleInfor.getScoreA() > coupleInfor.getScoreB()){
                    teamJoinTourRepository.save(checkTeamB.get(0).setState(EnumStatus.REMOVE));
                }else if(coupleInfor.getScoreA() < coupleInfor.getScoreB()){
                    teamJoinTourRepository.save(checkTeamA.get(0).setState(EnumStatus.REMOVE));
                }

                Avartar avartarA = avartarService.findByTeam(teamAOpt.get());
                teamA = teamAOpt.get().toTeamResponseDTO(avartarA.toAvartarDTO());

                Avartar avartarB = avartarService.findByTeam(teamBOpt.get());
                teamB = teamBOpt.get().toTeamResponseDTO(avartarB.toAvartarDTO());

            }
            CoupleInforRes coupleInforRes = new CoupleInforRes()
                    .setTeamA(teamA)
                    .setScoreA(coupleInfor.getScoreA())
                    .setTeamB(teamB)
                    .setScoreB(coupleInfor.getScoreB())
                    .setCreateAt(coupleInfor.getCreateAt());
            listCoupleRes.add(coupleInforRes);
        }
        return listCoupleRes;
    }

    @Override
    @Transactional
    public TournamentTable initTourTable(Integer limit, Tournament tournament) {
        CoupleInfor coupleInfor = new CoupleInfor()
                .setTeamAId(0L)
                .setTeamBId(0L)
                .setScoreA(0)
                .setScoreB(0);
        Round finalRound = new Round()
                .setName("")
                .setListCouple(addCouple(coupleInfor,1));
        Round semiRound = new Round()
                .setName("")
                .setListCouple(addCouple(coupleInfor,2));
        Round quaterRound = new Round()
                .setName("")
                .setListCouple(addCouple(coupleInfor,4));
        if(limit==8){
            quaterRound = null;
        }
        List<Round> eliminate = createEliminate(coupleInfor,limit);
        return new TournamentTable()
                .setEliminateRound(eliminate)
                .setQuarterRound(quaterRound)
                .setSemiRound(semiRound)
                .setFinalRound(finalRound)
                .setTournament(tournament);
    }

    @Transactional
    public List<CoupleInfor> addCouple(CoupleInfor coupleInfor, double count){
        List<CoupleInfor> listCouple = new ArrayList<>();
        for(int i=1; i<=count; i++){
            listCouple.add(coupleInfor);
        }
        return listCouple;
    }
    public List<Round> createEliminate(CoupleInfor coupleInfor, double limit){
        List<Round> eliminate = new ArrayList<>();
        if(limit==8){
            List<CoupleInfor> listCouple = new ArrayList<>();
            for(int i=1; i<=4; i++){
                listCouple.add(coupleInfor);
            }
            Round round = new Round()
                    .setName("")
                    .setListCouple(listCouple);
            eliminate.add(round);
        }else{
            double countEliminate = Math.sqrt(limit) - 3;
            for(double i=1; i<=countEliminate; i++){
                List<CoupleInfor> listCouple = new ArrayList<>();
                for(int j = 1; j<= limit/2; j++){
                    listCouple.add(coupleInfor);
                }
                Round round = new Round()
                        .setName("")
                        .setListCouple(listCouple);
                eliminate.add(round);
                limit = limit/2;
            }
        }
        return eliminate;
    }
    @Transactional
    public List<CoupleInforRes> convertRoundDDTO(Round round){
        List<CoupleInforRes> listCoupleRes = new ArrayList<>();
        List<CoupleInfor> listCouple = round.getListCouple();
        for(int i=0; i< listCouple.size(); i++){
            CoupleInfor coupleInfor = listCouple.get(i);

            Optional<TeamTournament> teamAOpt = teamRepository.findById(coupleInfor.getTeamAId());
            Optional<TeamTournament> teamBOpt = teamRepository.findById(coupleInfor.getTeamBId());

            TeamResponseDTO teamA = null;
            TeamResponseDTO teamB = null;

            if(teamAOpt.isPresent()){
                Avartar avartarA = avartarService.findByTeam(teamAOpt.get());
                teamA = teamAOpt.get().toTeamResponseDTO(avartarA.toAvartarDTO());
            }
            if(teamBOpt.isPresent()){
                Avartar avartarB = avartarService.findByTeam(teamBOpt.get());
                teamB = teamAOpt.get().toTeamResponseDTO(avartarB.toAvartarDTO());
            }

            CoupleInforRes coupleInforRes = new CoupleInforRes()
                    .setTeamA(teamA)
                    .setScoreA(coupleInfor.getScoreA())
                    .setTeamB(teamB)
                    .setScoreB(coupleInfor.getScoreB())
                    .setCreateAt(coupleInfor.getCreateAt());
            listCoupleRes.add(coupleInforRes);
        }
        return listCoupleRes;
    }
    @Transactional
    public TourTableResDTO convertTourResDTO(TournamentTable tourTableDTO, Tournament tournament){
        List<Round> eliminateRound = tourTableDTO.getEliminateRound();
        List<RoundDTO> eliminateDTO = new ArrayList<>();
        if(eliminateRound != null){
            for(int i=0; i< eliminateRound.size(); i++){
                List<CoupleInforRes> listCoupleInforRes = convertRoundDDTO(eliminateRound.get(i));
                String name = eliminateRound.get(i).getName();
                RoundDTO roundDTO = new RoundDTO()
                        .setName(name)
                        .setListCouple(listCoupleInforRes);
                eliminateDTO.add(roundDTO);
            }
        }

        Round quater = tourTableDTO.getQuarterRound();
        RoundDTO quaterDTO = new RoundDTO();
        if(quater!=null){
            quaterDTO = new RoundDTO()
                    .setName(quater.getName())
                    .setListCouple(convertRoundDDTO(quater));
        }

        Round semi = tourTableDTO.getSemiRound();
        RoundDTO semiDTO = new RoundDTO();
        if(semi!=null){
            semiDTO = new RoundDTO()
                    .setName(semi.getName())
                    .setListCouple(convertRoundDDTO(semi));
        }

        Round conclusion = tourTableDTO.getFinalRound();
        RoundDTO finalDTO = new RoundDTO();
        if(conclusion!=null){
            finalDTO = new RoundDTO()
                    .setName(conclusion.getName())
                    .setListCouple(convertRoundDDTO(conclusion));
        }

        return new TourTableResDTO()
                .setId(tourTableDTO.getId())
                .setEliminate(eliminateDTO)
                .setQuater(quaterDTO)
                .setSemi(semiDTO)
                .setConclusion(finalDTO);
    }
}
