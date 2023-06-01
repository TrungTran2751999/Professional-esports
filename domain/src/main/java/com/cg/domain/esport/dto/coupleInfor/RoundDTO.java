package com.cg.domain.esport.dto.coupleInfor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class RoundDTO {
    private String name;
    private List<CoupleInforRes> listCouple = new ArrayList<>();
}
