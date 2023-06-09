package com.cg.domain.esport.entities;

import com.cg.domain.esport.dto.AvartarDTO;
import com.cg.domain.esport.dto.TeamResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "team_tournament")
public class TeamTournament extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(targetEntity = Category.class)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(targetEntity = Student.class)
    @JoinColumn(name = "leader_id")
    private Student leader;

    public TeamResponseDTO toTeamResponseDTO(AvartarDTO avartarDTO){
        return new TeamResponseDTO()
                .setId(id)
                .setName(name)
                .setLeaderId(leader.getId())
                .setCategoryId(category.getId())
                .setCreateAt(getCreatedAt())
                .setAvartarDTO(avartarDTO);
    }
}
