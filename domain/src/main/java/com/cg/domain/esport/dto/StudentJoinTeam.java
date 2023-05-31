package com.cg.domain.esport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentJoinTeam {
    @NotNull
    private Long userId;
    @NotBlank
    private String code;
    @NotNull
    private Long teamId;
}
