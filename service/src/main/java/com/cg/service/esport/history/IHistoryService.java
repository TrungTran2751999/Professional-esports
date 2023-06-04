package com.cg.service.esport.history;

import com.cg.domain.esport.dto.HistoryDTO;
import com.cg.domain.esport.dto.HistoryResDTO;
import com.cg.domain.esport.dto.coupleInfor.CoupleInfor;
import com.cg.domain.esport.dto.coupleInfor.CoupleInforRes;

import java.util.List;

public interface IHistoryService {
    List<HistoryResDTO> findHistoryByTeam(Long teamId);
    HistoryResDTO create(HistoryDTO historyDTO);
}
