package com.cg.service.esport.processTour;

import com.cg.domain.esport.dto.AvartarDTO;
import com.cg.domain.esport.dto.OrganizerResponseDTO;
import com.cg.domain.esport.dto.ProcessResponseDTO;
import com.cg.domain.esport.dto.ProcessTourDTO;
import com.cg.domain.esport.entities.*;
import com.cg.exception.DataInputException;
import com.cg.repository.esport.OrganizerRepository;
import com.cg.repository.esport.ProcessTourRepository;
import com.cg.repository.esport.TournamentRespository;
import com.cg.service.esport.avartar.IAvartarService;
import com.cg.service.esport.organizer.IOrganizerService;
import com.cg.service.esport.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ProcessTourServiceImp implements IProcessTourService {
    @Autowired
    private ProcessTourRepository processTourRepository;
    @Autowired
    private IUserService userService;
    @Autowired
    private OrganizerRepository organizerRepository;
    @Autowired
    private TournamentRespository tournamentRespository;
    @Autowired
    private IAvartarService avartarService;

    @Override
    public ProcessResponseDTO create(ProcessTourDTO processTourDTO) {
        Optional<Tournament> tournamentOpt = tournamentRespository.findById(processTourDTO.getTourId());
        if (!tournamentOpt.isPresent()) throw new DataInputException("notFoundTournament");

        Tournament tournament = tournamentOpt.get();
        if (tournament.getDeleted()) throw new DataInputException("tourEndOrBaned");

        Date dateTour = tournamentOpt.get().getCreatedAt();

        if (countDate(processTourDTO.getRegister(),dateTour) <= 0)
            throw new DataInputException("RegisterGreaterCreate");

        if (countDate(processTourDTO.getConfirm(),processTourDTO.getRegister()) <= 0)
            throw new DataInputException("ConfirmGreaterRegister");

        if (countDate(processTourDTO.getStart(),processTourDTO.getConfirm()) <= 0)
            throw new DataInputException("StartGreaterConfirm");

        if (countDate(processTourDTO.getEnd(),processTourDTO.getStart()) <= 0)
            throw new DataInputException("EndGreaterStart");

        User user = userService.findByCodeSecurity(processTourDTO.getCode());
        if (user == null) throw new DataInputException("notFoundOrganizer");

        if (!Objects.equals(user.getId(), processTourDTO.getUserId()))
            throw new DataInputException("notFoundOrganizer");

        Organizer organizer = organizerRepository.getByUser(user);
        if (organizer == null) throw new DataInputException("notFoundOrganizer");

        AvartarDTO avartarOrganizer = avartarService.findByOrganizer(organizer).toAvartarDTO();
        AvartarDTO avartarCate = avartarService.findByCategory(tournament.getCategory()).toAvartarDTO();
        return processTourRepository.save(processTourDTO.toProcessTour(tournament))
                .toProcessResponseDTO(avartarOrganizer, avartarCate);
    }
    @Override
    public long countDate(Date nextDay, Date prevDay){
        long diff = nextDay.getTime() - prevDay.getTime();
        return TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);
    }

}
