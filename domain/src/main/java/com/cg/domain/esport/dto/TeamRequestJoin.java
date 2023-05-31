package com.cg.domain.esport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequestJoin {
    @NotNull
    private Long userId;
    @NotBlank
    private String code;
    @NotNull
    private Long joinId;
    @NotNull
    private Long teamId;
}
