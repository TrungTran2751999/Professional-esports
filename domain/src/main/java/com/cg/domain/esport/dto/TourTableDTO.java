package com.cg.domain.esport.dto;

import com.cg.domain.esport.dto.coupleInfor.Round;
import com.cg.domain.esport.entities.Tournament;
import com.cg.domain.esport.entities.TournamentTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TourTableDTO {
    @NotNull
    private Long userId;
    @NotBlank
    private String code;
    private Long id;
    @NotNull
    private List<Round> eliminateRound;
    @NotNull
    private Round quarterRound;
    @NotNull
    private Round semiRound;
    @NotNull
    private Round finalRound;
    @NotNull
    private Long tourId;
    public TournamentTable toTournamentTable(Tournament tournament){
        return new TournamentTable()
                .setId(id)
                .setTournament(tournament)
                .setEliminateRound(eliminateRound)
                .setQuarterRound(quarterRound)
                .setSemiRound(semiRound)
                .setFinalRound(finalRound);
    }
}
