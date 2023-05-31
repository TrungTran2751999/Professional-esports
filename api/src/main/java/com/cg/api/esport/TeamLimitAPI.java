package com.cg.api.esport;

import com.cg.domain.esport.entities.TeamLimit;
import com.cg.exception.DataInputException;
import com.cg.repository.esport.TeamLimitRepository;
import com.cg.service.esport.teamLimit.ITeamLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/limit")
public class TeamLimitAPI {
    @Autowired
    private ITeamLimitService teamLimitService;
    @Autowired
    private TeamLimitRepository teamLimitRepository;
    @GetMapping
    public ResponseEntity<?> getAll(){
        return new ResponseEntity<>(teamLimitService.findAll(), HttpStatus.OK);
    }
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> create(@RequestParam("limit") Integer limit){
        List<TeamLimit> teamLimitList = teamLimitRepository.findByMax(limit);
        if(teamLimitList.size() > 0) throw new DataInputException("limitExist");

        TeamLimit teamLimit = new TeamLimit().setMax(limit);
        return new ResponseEntity<>(teamLimitService.save(teamLimit), HttpStatus.OK);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> update(@RequestParam("limit") Integer limit, @PathVariable("id") Long id){
        Optional<TeamLimit> teamLimitOpt = teamLimitService.findById(id);
        if(!teamLimitOpt.isPresent()) throw new DataInputException("notFoundTeamLimit");
        List<TeamLimit> teamLimitList = teamLimitRepository.findByMax(limit);

        if(teamLimitList.size() > 0 && !Objects.equals(teamLimitOpt.get().getMax(), teamLimitList.get(0).getMax()))
            throw new DataInputException("teamLimitExist");

        TeamLimit teamLimit = new TeamLimit().setMax(limit).setId(id);
        return new ResponseEntity<>(teamLimitService.save(teamLimit), HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){
        teamLimitService.remove(id);
        return new ResponseEntity<>("DELETE SUCCESS", HttpStatus.OK);
    }
}
