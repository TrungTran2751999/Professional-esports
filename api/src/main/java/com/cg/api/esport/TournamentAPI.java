package com.cg.api.esport;

import com.cg.domain.esport.dto.*;
import com.cg.domain.esport.enums.EnumStatus;
import com.cg.repository.esport.TourTableRepository;
import com.cg.service.esport.processTour.IProcessTourService;
import com.cg.service.esport.teamJoinTour.ITeamJoinTourService;
import com.cg.service.esport.tourTable.ITourTableService;
import com.cg.service.esport.tournament.ITournamentService;
import com.cg.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournament")
public class TournamentAPI {
    @Autowired
    private ITournamentService tournamentService;
    @Autowired
    private AppUtils appUtils;
    @Autowired
    private ITourTableService tourTableService;
    @Autowired
    private IProcessTourService processTourService;
    @Autowired
    private ITeamJoinTourService teamJoinTourService;

    @PostMapping("/filter")
    public ResponseEntity<?> filter(@Validated @RequestBody TournamentFilter tournamentFilter, BindingResult bindingResult,
                                    @RequestParam(name = "sort", required = false, defaultValue = "ASC") String sort){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        int start = tournamentFilter.getStart();
        int limit = tournamentFilter.getLength();
        int page = start/limit+1;
        Sort sortable = null;
        if(sort.equals("ASC")){
            sortable = Sort.by("id").ascending();
        }
        if(sort.equals("DESC")){
            sortable = Sort.by("id").descending();
        }
        Pageable pageable = PageRequest.of(page-1, limit ,sortable);
        Page<TournamentReponseDTO> pageTour= tournamentService.filter(tournamentFilter, pageable);
        List<TournamentReponseDTO> listTour = pageTour.getContent();
        return new ResponseEntity<>(listTour, HttpStatus.OK);
    }
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<?> create(@Validated @RequestBody TournamentDTO tournamentDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        return new ResponseEntity<>(tournamentService.create(tournamentDTO), HttpStatus.OK);
    }
    @PostMapping("/deleted/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> setDelete(@RequestParam("deleted") Boolean deleted, @PathVariable("id") Long id){
        tournamentService.setDelete(id, deleted);
        return new ResponseEntity<>("ACTIVE DELETE", HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){
        tournamentService.deleteTour(id);
        return new ResponseEntity<>("DELETE SUCCESS", HttpStatus.OK);
    }
    @PostMapping("/table")
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<?> createOrUpdateTable(@Validated @RequestBody TourTableDTO tourTableDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        return new ResponseEntity<>(tourTableService.createOrUpdate(tourTableDTO), HttpStatus.OK);
    }
    @PostMapping("/process")
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<?> createProcess(@Validated @RequestBody ProcessTourDTO processTourDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        return new ResponseEntity<>(processTourService.create(processTourDTO),HttpStatus.OK);
    }
    @GetMapping("/register/{id}")
    public ResponseEntity<?> findRegister(@PathVariable("id") Long id){
        return new ResponseEntity<>(teamJoinTourService.findTeamJoin(id, EnumStatus.REGISTER), HttpStatus.OK);
    }
    @GetMapping("/confirm/{id}")
    public ResponseEntity<?> findConfirm(@PathVariable("id") Long id){
        return new ResponseEntity<>(teamJoinTourService.findTeamJoin(id, EnumStatus.CONFIRM), HttpStatus.OK);
    }
    @GetMapping("/table/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id){
        return new ResponseEntity<>(tourTableService.findById(id), HttpStatus.OK);
    }

}
