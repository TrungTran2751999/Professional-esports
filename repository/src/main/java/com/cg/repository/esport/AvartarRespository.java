package com.cg.repository.esport;

import com.cg.domain.esport.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvartarRespository extends JpaRepository<Avartar, String> {
    Avartar findByOrganizer(Organizer organizer);
    Avartar getByStudent(Student student);
    Avartar findByTeamTournament(TeamTournament teamTournament);
    Avartar findByCategory(Category category);
}
