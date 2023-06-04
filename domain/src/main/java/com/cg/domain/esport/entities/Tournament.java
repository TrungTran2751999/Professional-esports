package com.cg.domain.esport.entities;

import com.cg.domain.esport.dto.AvartarDTO;
import com.cg.domain.esport.dto.TournamentReponseDTO;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tournament")
//@TypeDef(name="joinPresent", typeClass = JsonType.class)
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @ManyToOne(targetEntity = Category.class)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "prize", nullable = false)
    private BigDecimal prize;

    @Column(name = "join_limit", nullable = false)
    private Integer joinLimit;

    @ManyToOne(targetEntity = Organizer.class)
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;

    @Column(name = "address")
    private String address;

    @Column(name = "create_at")
    private Date createdAt;

    @Column(name = "begining", columnDefinition = "boolean default false")
    private Boolean begining = false;

    @Column(name = "deleted", columnDefinition = "boolean default true")
    private Boolean deleted = true;

    public TournamentReponseDTO toTournamentResponseDTO(AvartarDTO avartarDTO, AvartarDTO avartarCate){
        return new TournamentReponseDTO()
                .setId(id)
                .setName(name)
                .setAddress(address)
                .setCategoryResponseDTO(category.toCategoryDTO().setAvartarDTO(avartarCate))
                .setPrize(prize)
                .setJoinLimit(joinLimit)
                .setOrganizerResponseDTO(organizer.toOrganizerResponseDTO(avartarDTO));
    }
}
