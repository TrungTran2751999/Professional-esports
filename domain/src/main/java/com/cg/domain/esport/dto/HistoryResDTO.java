package com.cg.domain.esport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResDTO {
    private Long id;
    private TeamResponseDTO teamA;
    private Integer scoreA;
    private TeamResponseDTO teamB;
    private Integer scoreB;
    private Long tourId;
    private Date createAt;
}
