package com.cg.domain.esport.dto.coupleInfor;

import com.cg.domain.esport.entities.TeamTournament;
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
public class Round {
    private String name;
    private List<CoupleInfor> listCouple = new ArrayList<>();
    public RoundDTO toRoundDTO(List<CoupleInforRes> listCouleInforRes){
       return new RoundDTO()
               .setName(name)
               .setListCouple(listCouleInforRes);
    }
}
