package com.cg.repository.esport;

import com.cg.domain.esport.entities.Student;
import com.cg.domain.esport.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByEmail(String email);
    Student findByUser(User user);
    List<Student> findByDeleted(Boolean deleted);
}
