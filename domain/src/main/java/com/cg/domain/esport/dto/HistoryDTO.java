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
public class HistoryDTO {
    private Long teamAId;
    private Integer scoreA;
    private Long teamBId;
    private Integer scoreB;
    private Long tourId;
    private Date createAt;
}
