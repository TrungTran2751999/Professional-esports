package com.cg.domain.esport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TournamentReponseDTO {
    private Long id;
    private String name;
    private BigDecimal prize;
    private Integer joinLimit;
    private String address;
    private CategoryResponseDTO categoryResponseDTO;
    private OrganizerResponseDTO organizerResponseDTO;
}
