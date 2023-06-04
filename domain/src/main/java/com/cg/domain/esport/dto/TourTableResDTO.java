package com.cg.domain.esport.dto;

import com.cg.domain.esport.dto.coupleInfor.RoundDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TourTableResDTO {
    private Long id;
    private List<RoundDTO> eliminate;
    private RoundDTO quater;
    private RoundDTO semi;
    private RoundDTO conclusion;
}
