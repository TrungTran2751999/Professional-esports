package com.cg.domain.esport.entities;

import com.cg.domain.esport.dto.AvartarDTO;
import com.cg.domain.esport.dto.ProcessResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "process_tournament")
public class ProcessTournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "register_finish")
    private Date registerFinish;

    @Column(name = "confirm_finish")
    private Date confirmFinish;

    @Column(name = "start_finish")
    private Date startFinish;

    @Column(name = "end_finish")
    private Date endFinish;

    @OneToOne(targetEntity = Tournament.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    public ProcessResponseDTO toProcessResponseDTO(AvartarDTO avartarOrganizer, AvartarDTO avartarCate){
        return new ProcessResponseDTO()
                .setRegister(registerFinish)
                .setConfirm(confirmFinish)
                .setStart(startFinish)
                .setEnd(endFinish)
                .setTournamentReponseDTO(tournament.toTournamentResponseDTO(avartarOrganizer, avartarCate));
    }

}
