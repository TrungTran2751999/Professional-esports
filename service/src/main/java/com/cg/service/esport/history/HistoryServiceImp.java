package com.cg.service.esport.history;

import com.cg.domain.esport.dto.HistoryDTO;
import com.cg.domain.esport.dto.HistoryResDTO;
import com.cg.domain.esport.dto.coupleInfor.CoupleInfor;
import com.cg.domain.esport.dto.coupleInfor.CoupleInforRes;
import com.cg.domain.esport.entities.Avartar;
import com.cg.domain.esport.entities.HistoryTeamTournament;
import com.cg.domain.esport.entities.TeamTournament;
import com.cg.domain.esport.entities.Tournament;
import com.cg.exception.DataInputException;
import com.cg.repository.esport.HistoryRepository;
import com.cg.repository.esport.TeamRepository;
import com.cg.repository.esport.TournamentRespository;
import com.cg.service.esport.avartar.IAvartarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HistoryServiceImp implements IHistoryService{
    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TournamentRespository tournamentRespository;

    @Autowired
    private IAvartarService avartarService;

    @Override
    public List<HistoryResDTO> findHistoryByTeam(Long teamId) {
        Optional<TeamTournament> teamTournamentOpt = teamRepository.findById(teamId);
        if(!teamTournamentOpt.isPresent()) throw new DataInputException("notFoundTeam");

        TeamTournament team = teamTournamentOpt.get();
        List<HistoryResDTO> listHistoryResDTO = new ArrayList<>();
        List<HistoryTeamTournament> historyList = historyRepository.findByTeamAOrTeamB(team,team);
        for(int i=0; i<historyList.size(); i++){
            HistoryTeamTournament historyTeam = historyList.get(i);
            Avartar avartarA = avartarService.findByTeam(historyTeam.getTeamA());
            Avartar avartarB = avartarService.findByTeam(historyTeam.getTeamB());

            HistoryResDTO historyResDTO = new HistoryResDTO()
                    .setTeamA(historyTeam.getTeamA().toTeamResponseDTO(avartarA.toAvartarDTO()))
                    .setTeamB(historyTeam.getTeamB().toTeamResponseDTO(avartarB.toAvartarDTO()))
                    .setScoreA(historyTeam.getScoreA())
                    .setScoreB(historyTeam.getScoreB())
                    .setTourId(historyTeam.getTournament().getId());

            listHistoryResDTO.add(historyResDTO);
        }
        return listHistoryResDTO;

    }

    @Override
    public HistoryResDTO create(HistoryDTO historyDTO) {
        Optional<TeamTournament> teamAOpt = teamRepository.findById(historyDTO.getTeamAId());
        if(!teamAOpt.isPresent()) throw new DataInputException("notFoundTeamA");

        Optional<TeamTournament> teamBOpt = teamRepository.findById(historyDTO.getTeamBId());
        if(!teamBOpt.isPresent()) throw new DataInputException("notFoundTeamB");

        Optional<Tournament> tournamentOpt = tournamentRespository.findById(historyDTO.getTourId());
        if(!tournamentOpt.isPresent()) throw new DataInputException("notFoundTour");

        HistoryTeamTournament history = historyRepository.findByTeamIdAndDate(
                historyDTO.getTeamAId(),
                historyDTO.getTeamBId(),
                historyDTO.getCreateAt(),
                historyDTO.getTourId()
        );
        if(history!=null) return null;
        TeamTournament teamA = teamAOpt.get();
        TeamTournament teamB = teamBOpt.get();

        Avartar avartarA = avartarService.findByTeam(teamA);
        Avartar avartarB = avartarService.findByTeam(teamB);

        HistoryTeamTournament historyTeamTournament = new HistoryTeamTournament()
                .setTeamA(teamA)
                .setTeamB(teamB)
                .setScoreA(historyDTO.getScoreA())
                .setScoreB(historyDTO.getScoreB())
                .setCreatedAt(historyDTO.getCreateAt());

        historyTeamTournament = historyRepository.save(historyTeamTournament);
        return new HistoryResDTO()
                .setId(historyTeamTournament.getId())
                .setTeamA(teamA.toTeamResponseDTO(avartarA.toAvartarDTO()))
                .setTeamB(teamB.toTeamResponseDTO(avartarB.toAvartarDTO()))
                .setScoreA(historyTeamTournament.getScoreA())
                .setScoreB(historyTeamTournament.getScoreB())
                .setCreateAt(historyTeamTournament.getCreatedAt());
    }
}
