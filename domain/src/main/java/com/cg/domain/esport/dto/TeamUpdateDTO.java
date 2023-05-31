package com.cg.domain.esport.dto;

import com.cg.domain.esport.entities.TeamTournament;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamUpdateDTO {
    @NotNull
    @Pattern(regexp = "^[A-Za-z\s]*$")
    private String name;

    @NotNull
    private Long teamId;

    @NotNull
    private Long userId;

    @NotNull
    private String code;
    public TeamTournament toTeamTournament(){
        return new TeamTournament()
                .setName(name)
                .setId(teamId);
    }
}
