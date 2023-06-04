package com.cg.domain.esport.dto;

import com.cg.domain.esport.entities.ProcessTournament;
import com.cg.domain.esport.entities.Tournament;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date register;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date confirm;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date start;
    @JsonFormat(pattern = "dd-MM-yyyy")
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
