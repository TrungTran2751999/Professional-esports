package com.cg.service.esport.tourTable;

import com.cg.domain.esport.dto.TourTableDTO;
import com.cg.domain.esport.dto.TourTableResDTO;
import com.cg.domain.esport.dto.coupleInfor.CoupleInforRes;
import com.cg.domain.esport.dto.coupleInfor.Round;
import com.cg.domain.esport.entities.Tournament;
import com.cg.domain.esport.entities.TournamentTable;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import java.util.List;

public interface ITourTableService {
    TourTableResDTO createOrUpdate(TourTableDTO tourTableDTO);
    List<CoupleInforRes> checkInforRound(Round round, Tournament tournament);
    TournamentTable initTourTable(Integer limit);
}
