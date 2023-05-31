package com.cg.domain.esport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamTournamentDTO {
    private Long id;
    private String name;
    private CategoryResponseDTO category;
    private StudentDTO leader;
}
