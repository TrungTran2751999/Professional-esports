package com.cg.domain.esport.dto;

import com.cg.domain.esport.entities.ProcessTournament;
import com.cg.domain.esport.entities.Tournament;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessTourDTO {
    private Long userId;
    private String code;
    private Long tourId;
    private Date register;
    private Date confirm;
    private Date start;
    private Date end;
    public ProcessTournament toProcessTour(Tournament tournament){
        return new ProcessTournament()
                .setRegisterFinish(register)
                .setConfirmFinish(confirm)
                .setStartFinish(start)
                .setEndFinish(end)
                .setTournament(tournament);
    }
}
