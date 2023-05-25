package com.cg.repository.esport;

import com.cg.domain.esport.entities.Avartar;
import com.cg.domain.esport.entities.Organizer;
import com.cg.domain.esport.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvartarRespository extends JpaRepository<Avartar, String> {
    Avartar findByOrganizer(Organizer organizer);
    Avartar getByStudent(Student student);
}
