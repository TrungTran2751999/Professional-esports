package com.cg.domain.esport.dto;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponseDTO {
    private Long id;
    private String name;
    private Long leaderId;
    private Long categoryId;
    private AvartarDTO avartarDTO;
    private Date createAt;
}
