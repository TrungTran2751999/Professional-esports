package com.cg.service.esport.tournament;

import com.cg.domain.esport.dto.TournamentDTO;
import com.cg.domain.esport.dto.TournamentFilter;
import com.cg.domain.esport.dto.TournamentReponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITournamentService {
    Page<TournamentReponseDTO> filter(TournamentFilter tournamentFilter, Pageable pageable);
    TournamentReponseDTO create(TournamentDTO tournamentDTO);

    void setDelete(Long tourId, Boolean deleted);
    void deleteTour(Long tourId);
}
