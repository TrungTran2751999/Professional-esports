package com.cg.service.esport.processTour;

import com.cg.domain.esport.dto.ProcessResponseDTO;
import com.cg.domain.esport.dto.ProcessTourDTO;

public interface IProcessTourService {
    ProcessResponseDTO create(ProcessTourDTO processTourDTO);
}
