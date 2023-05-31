package com.cg.api.esport;

import com.cg.domain.esport.dto.*;
import com.cg.domain.esport.entities.TeamTournament;
import com.cg.exception.DataInputException;
import com.cg.service.esport.teamTournament.ITeamService;
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
import java.util.Objects;

@RestController
@RequestMapping("/api/teamtournament")
public class TeamTournamentAPI {
    @Autowired
    private ITeamService teamService;
    @Autowired
    private AppUtils appUtils;
    @PostMapping("/filter")
    public ResponseEntity<?> filter(@Validated @RequestBody TeamFilter teamFilter, BindingResult bindingResult,
                                    @RequestParam(name = "sort", required = false, defaultValue = "ASC") String sort){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        int start = teamFilter.getStart();
        int limit = teamFilter.getLength();
        int page = start/limit+1;
        Sort sortable = null;
        if(sort.equals("ASC")){
            sortable = Sort.by("id").ascending();
        }
        if(sort.equals("DESC")){
            sortable = Sort.by("id").descending();
        }
        Pageable pageable = PageRequest.of(page-1, limit ,sortable);
        Page<TeamResponseDTO> pageStudent = teamService.filter(teamFilter, pageable);
        List<TeamResponseDTO> listStudent = pageStudent.getContent();
        return new ResponseEntity<>(listStudent, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return new ResponseEntity<>(teamService.findTeamById(id), HttpStatus.OK);
    }
    @GetMapping("/student/{id}")
    public ResponseEntity<?> getAllStudent(@PathVariable Long id, @RequestParam("deleted") Boolean deleted){
        return new ResponseEntity<>(teamService.getAllStudentJoined(id, deleted), HttpStatus.OK);
    }
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> create(@Validated TeamDTO teamDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        String[] imageExtension = new String[]{"image/png", "image/jpg", "image/jpeg"};
        for(int i=0; i<imageExtension.length; i++){
            if(Objects.equals(teamDTO.getAvartar().getContentType(), imageExtension[i])){
                return new ResponseEntity<>(teamService.createTeam(teamDTO), HttpStatus.OK);
            }else if(i == imageExtension.length-1){
                throw new DataInputException("Ảnh đại diện không đúng định dạng");
            }
        }
        return null;
    }
    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> update(@Validated @RequestBody TeamUpdateDTO teamUpdateDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        return new ResponseEntity<>(teamService.updateNoAvartar(teamUpdateDTO), HttpStatus.OK);
    }
    @PutMapping("/avartar")
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> updateAvartar(@Validated TeamAvartarDTO teamAvartarDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        String[] imageExtension = new String[]{"image/png", "image/jpg", "image/jpeg"};
        for(int i=0; i<imageExtension.length; i++){
            if(Objects.equals(teamAvartarDTO.getAvartar().getContentType(), imageExtension[i])){
                return new ResponseEntity<>(teamService.updateAvartar(teamAvartarDTO), HttpStatus.OK);
            }else if(i == imageExtension.length-1){
                throw new DataInputException("Ảnh đại diện không đúng định dạng");
            }
        }
        return null;
    }

    @PostMapping("/deleted/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> setDelete(@RequestParam("deleted") Boolean deleted, @PathVariable("id") Long id){
        teamService.setDeleted(id, deleted);
        return new ResponseEntity<>("UPDATE SUCCESS", HttpStatus.OK);
    }
    @PostMapping("/accept")
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> acceptJoin(@Validated @RequestBody TeamRequestJoin teamRequestJoin, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        teamService.acceptJoin(teamRequestJoin);
        return new ResponseEntity<>("ACCEPT JOIN SUCCESS", HttpStatus.OK);
    }
    @PostMapping("/reject")
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> rejectJoin(@Validated @RequestBody TeamRequestJoin teamRequestJoin, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        teamService.rejectJoin(teamRequestJoin);
        return new ResponseEntity<>("REJECT JOIN SUCCESS", HttpStatus.OK);
    }
    @PostMapping("/jointour")
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> joinTour(@RequestBody TeamJoinTourDTO teamJoinTourDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return appUtils.mapErrorToResponse(bindingResult);
        teamService.joinTournament(teamJoinTourDTO);
        return new ResponseEntity<>("JOIN TOUR SUCCESS", HttpStatus.OK);
    }
    @PostMapping("/leavetour")
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> leaveTour(@RequestBody TeamJoinTourDTO teamJoinTourDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return appUtils.mapErrorToResponse(bindingResult);
        teamService.leaveTournament(teamJoinTourDTO);
        return new ResponseEntity<>("LEAVE TOUR SUCCESS", HttpStatus.OK);
    }
}
