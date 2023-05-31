package com.cg.domain.esport.entities;

import com.cg.domain.esport.dto.AvartarDTO;
import com.cg.domain.esport.dto.TeamJoinTourResDTO;
import com.cg.domain.esport.enums.EnumStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "team_join_tournament")
public class TeamJoinTour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Tournament.class)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToOne(targetEntity = TeamTournament.class)
    @JoinColumn(name = "team_id")
    private TeamTournament team;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "state", columnDefinition = "default REGISTER")
    private EnumStatus state;

    @CreatedBy
    @Column(name = "create_at")
    private Date createBy;

    public TeamJoinTourResDTO toTeamJoinTourResDTO(AvartarDTO avartarTeam){
        return new TeamJoinTourResDTO()
                .setJoinDate(createBy)
                .setTeamResponseDTO(team.toTeamResponseDTO(avartarTeam));
    }
}
