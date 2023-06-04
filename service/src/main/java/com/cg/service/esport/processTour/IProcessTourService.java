package com.cg.service.esport.processTour;

import com.cg.domain.esport.dto.ProcessResponseDTO;
import com.cg.domain.esport.dto.ProcessTourDTO;

import java.util.Date;

public interface IProcessTourService {
    ProcessResponseDTO create(ProcessTourDTO processTourDTO);
    long countDate(Date nextDay, Date prevDay);
}
