package com.cg.service.esport.tourTable;

import com.cg.domain.esport.dto.TeamResponseDTO;
import com.cg.domain.esport.dto.TourTableDTO;
import com.cg.domain.esport.dto.TourTableResDTO;
import com.cg.domain.esport.dto.coupleInfor.CoupleInfor;
import com.cg.domain.esport.dto.coupleInfor.CoupleInforRes;
import com.cg.domain.esport.dto.coupleInfor.Round;
import com.cg.domain.esport.dto.coupleInfor.RoundDTO;
import com.cg.domain.esport.entities.*;
import com.cg.exception.DataInputException;
import com.cg.repository.esport.*;
import com.cg.service.esport.avartar.IAvartarService;
import com.cg.service.esport.organizer.IOrganizerService;
import com.cg.service.esport.teamJoinTour.ITeamJoinTourService;
import com.cg.service.esport.teamTournament.ITeamService;
import com.cg.service.esport.user.IUserService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    @Override
    public TourTableResDTO createOrUpdate(TourTableDTO tourTableDTO) {
        User user = userService.findByCodeSecurity(tourTableDTO.getCode());
        if(user==null) throw new DataInputException("notFoundOrganizer");

        if(!Objects.equals(user.getId(), tourTableDTO.getUserId()))
            throw new DataInputException("notFoundOrganizer");

        Organizer organizer = organizerRepository.getByUser(user);
        if(organizer==null) throw new DataInputException("notFoundOrganizer");

        Tournament tournament = tournamentRespository.findByIdAndOrganizer(organizer, tourTableDTO.getTourId());
        if(tournament==null) throw new DataInputException("notFoundTournament");

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
        if(quater!=null){
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
        TournamentTable tournamentTable = tourTableRepository.save(tourTableDTO.toTournamentTable(tournament));
        return new TourTableResDTO()
                .setId(tournamentTable.getId())
                .setEliminate(eliminateDTO)
                .setQuater(quaterDTO)
                .setSemi(semiDTO)
                .setConclusion(finalDTO);
    }
    @Override
    public List<CoupleInforRes> checkInforRound(Round round, Tournament tournament){
        List<CoupleInforRes> listCoupleRes = new ArrayList<>();
        List<CoupleInfor> listCouple = round.getListCouple();
        for(int i=0; i< listCouple.size(); i++){
            CoupleInfor coupleInfor = listCouple.get(i);
            Optional<TeamTournament> teamAOpt = teamRepository.findById(coupleInfor.getTeamAId());
            Optional<TeamTournament> teamBOpt = teamRepository.findById(coupleInfor.getTeamBId());

            if(!teamAOpt.isPresent()) throw new DataInputException("notFoundTeamA");
            if(!teamBOpt.isPresent()) throw new DataInputException("notFoundTeamB");

            List<TeamJoinTour> checkTeamA = teamJoinTourRepository.findByTournamentAndTeam(tournament, teamAOpt.get());
            if(checkTeamA.isEmpty()) throw new DataInputException("teamANotInTour");
            List<TeamJoinTour> checkTeamB = teamJoinTourRepository.findByTournamentAndTeam(tournament, teamBOpt.get());
            if(checkTeamB.isEmpty()) throw new DataInputException("teamBNotInTour");

            Avartar avartarA = avartarService.findByTeam(teamAOpt.get());
            TeamResponseDTO teamA = teamAOpt.get().toTeamResponseDTO(avartarA.toAvartarDTO());

            Avartar avartarB = avartarService.findByTeam(teamBOpt.get());
            TeamResponseDTO teamB = teamAOpt.get().toTeamResponseDTO(avartarB.toAvartarDTO());

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
    public TournamentTable initTourTable(Integer limit) {
        double roundCount = Math.sqrt(limit);
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
                .setListCouple(addCouple(coupleInfor,3));
        List<Round> eliminate = new ArrayList<>();
        if(roundCount==3){
            quaterRound = null;
        }
        eliminate = createEliminate(coupleInfor,limit);
        return new TournamentTable()
                .setEliminateRound(eliminate)
                .setQuarterRound(quaterRound)
                .setSemiRound(semiRound)
                .setFinalRound(finalRound);
    }
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
            listCouple.add(coupleInfor);
            Round round = new Round()
                    .setName("")
                    .setListCouple(listCouple);
            eliminate.add(round);
        }else{
            double countEliminate = Math.sqrt(limit) - 3;
            for(int i=1; i<=countEliminate; i++){
                List<CoupleInfor> listCouple = new ArrayList<>();
                for(double j = 1; j<= limit; j++){
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
}
