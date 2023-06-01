package com.cg.domain.esport.dto.coupleInfor;

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
@Component
public class CoupleInfor {
    private Long teamAId;
    private Integer scoreA;
    private Long teamBId;
    private Integer scoreB;
    private Date createAt;
}
