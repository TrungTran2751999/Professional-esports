package com.cg.service.esport.teamLimit;

import com.cg.domain.esport.entities.TeamLimit;
import com.cg.repository.esport.TeamLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamLimitServiceImp implements ITeamLimitService{
    @Autowired
    private TeamLimitRepository teamLimitRepository;

    @Override
    public List<TeamLimit> findAll() {
        return teamLimitRepository.findAll();
    }

    @Override
    public TeamLimit getById(Long id) {
        return teamLimitRepository.getById(id);
    }

    @Override
    public Optional<TeamLimit> findById(Long id) {
        return teamLimitRepository.findById(id);
    }

    @Override
    public TeamLimit save(TeamLimit teamLimit) {
        return teamLimitRepository.save(teamLimit);
    }

    @Override
    public void remove(Long id) {
        teamLimitRepository.deleteById(id);
    }
}
