package com.cg.service.esport.organizer;

import com.cg.domain.esport.dto.*;
import com.cg.domain.esport.entities.Organizer;
import com.cg.service.IGeneralService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface IOrganizerService extends IGeneralService<Organizer> {

    OrganizerResponseDTO createOrganizer(OrganizerRequestDTO organizerDTO);
    OrganizerResSecurity updateAvartar(OrganizerAvartarDTO organizerAvartarDTO);
    OrganizerResSecurity updateOrganizerNoAvarTar(OrganizerUpdateDTO organizerDTO);
    OrganizerResponseDTO findByUserId(Long id);
    Page<OrganizerResponseDTO> filter(OrganizerFilter organizerFilter, Pageable pageable);
    Page<OrganizerResSecurity> filterByAdmin(OrganizerFilter organizerFilter, Pageable pageable);
    OrganizerResSecurity findByCodeSecurity(String code);
    void deleteOrganizer(Long id);
    List<OrganizerResSecurity> findByDeleted(Boolean deleted);
    OrganizerResSecurity findByAdmin(Long id);
    void setDeleted(Boolean deleted, Long id);
}
