package com.cg.domain.esport.dto.coupleInfor;

import com.cg.domain.esport.dto.TeamResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CoupleInforRes {
    private TeamResponseDTO teamA;
    private Integer scoreA;
    private TeamResponseDTO teamB;
    private Integer scoreB;
    private Date createAt;
}
