package com.cg.domain.esport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ProcessResponseDTO {
    private Date register;
    private Date confirm;
    private Date start;
    private Date end;
    private TournamentReponseDTO tournamentReponseDTO;
}
