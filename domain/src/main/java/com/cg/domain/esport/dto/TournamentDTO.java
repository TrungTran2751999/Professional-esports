package com.cg.domain.esport.dto;

import com.cg.domain.esport.entities.BaseEntity;
import com.cg.domain.esport.entities.Tournament;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TournamentDTO{
    @NotNull
    private Long userId;
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    @NotNull
    private Long categoryId;
    @NotNull
    private BigDecimal prize;
    @NotNull
    private Integer joinLimit;

    public Tournament toTournament(){
        return new Tournament()
                .setPrize(prize)
                .setName(name)
                .setJoinLimit(joinLimit);
    }
}
