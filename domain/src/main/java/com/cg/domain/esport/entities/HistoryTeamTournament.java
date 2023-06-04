package com.cg.domain.esport.entities;

import com.cg.domain.esport.dto.coupleInfor.CoupleInfor;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "history_team_tournament")
public class HistoryTeamTournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Tournament.class)
    @JoinColumn(name="tournament")
    private Tournament tournament;

    @ManyToOne(targetEntity = TeamTournament.class)
    @JoinColumn(name = "teamA")
    private TeamTournament teamA;

    @ManyToOne(targetEntity = TeamTournament.class)
    @JoinColumn(name = "teamB")
    private TeamTournament teamB;

    @Column(name = "scoreA")
    private Integer scoreA;

    @Column(name = "scoreB")
    private Integer scoreB;

    @Column(name = "created_at")
    private Date createdAt;
}
