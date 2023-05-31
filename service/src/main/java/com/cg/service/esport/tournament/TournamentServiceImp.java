package com.cg.service.esport.tournament;

import com.cg.domain.esport.dto.*;
import com.cg.domain.esport.entities.*;
import com.cg.exception.DataInputException;
import com.cg.repository.esport.*;
import com.cg.service.esport.avartar.IAvartarService;
import com.cg.service.esport.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Service
public class TournamentServiceImp implements ITournamentService{
    @Autowired
    private TournamentRespository tournamentRespository;
    @Autowired
    private TournamentFilterRepository tournamentFilterRepo;
    @Autowired
    private IUserService userService;
    @Autowired
    private OrganizerRepository organizerRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TeamLimitRepository teamLimitRepository;
    @Autowired
    private IAvartarService avartarService;

    @Override
    public Page<TournamentReponseDTO> filter(TournamentFilter filter, Pageable pageable) {
        return tournamentFilterRepo.findAllByFilters(filter,pageable).map(tournament -> {
            Avartar avartarOrganizer = avartarService.findByOrganizer(tournament.getOrganizer());
            Avartar avartarCategory = avartarService.findByCategory(tournament.getCategory());
            return tournament.toTournamentResponseDTO(avartarOrganizer.toAvartarDTO(), avartarCategory.toAvartarDTO());
        });
    }

    @Override
    public TournamentReponseDTO create(TournamentDTO tournamentDTO) {
        User user = userService.findByCodeSecurity(tournamentDTO.getCode());
        if(user==null) throw new DataInputException("notFoundOrganizer");

        if(!Objects.equals(user.getId(), tournamentDTO.getUserId()))
            throw new DataInputException("notFoundOrganizer");

        Organizer organizer = organizerRepository.getByUser(user);
        if(organizer==null) throw new DataInputException("notFoundOrganizer");

        Optional<Category> category = categoryRepository.findById(tournamentDTO.getCategoryId());
        if(!category.isPresent()) throw new DataInputException("notFoundCategory");

        List<TeamLimit> teamLimitList = teamLimitRepository.findByMax(tournamentDTO.getJoinLimit());
        if(teamLimitList.isEmpty()) throw new DataInputException("notLimitExist");

        Tournament tournament = tournamentDTO.toTournament()
                                    .setCategory(category.get())
                                    .setOrganizer(organizer)
                                    .setCreatedAt(new Date());
        Avartar avartar = avartarService.findByOrganizer(organizer);
        Avartar avartarCategory = avartarService.findByCategory(tournament.getCategory());
        return tournamentRespository.save(tournament).toTournamentResponseDTO(avartar.toAvartarDTO(), avartarCategory.toAvartarDTO());
    }

    @Override
    public void setDelete(Long tourId, Boolean deleted) {
        Optional<Tournament> tournamentOpt = tournamentRespository.findById(tourId);
        if(!tournamentOpt.isPresent()) throw new DataInputException("notFoundTour");

        tournamentRespository.save(tournamentOpt.get()
                                    .setDeleted(deleted));
    }

    @Override
    public void deleteTour(Long tourId) {
        Optional<Tournament> tournamentOpt = tournamentRespository.findById(tourId);
        if(!tournamentOpt.isPresent()) throw new DataInputException("notFoundTour");

        if(!tournamentOpt.get().getDeleted()) throw new DataInputException("notDeleteTourActive");

        tournamentRespository.deleteById(tourId);
    }

}
