package com.cg.repository.esport;

import com.cg.domain.esport.entities.Student;
import com.cg.domain.esport.entities.TeamStudent;
import com.cg.domain.esport.entities.TeamTournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamStudentRepository extends JpaRepository<TeamStudent, Long> {
    List<TeamStudent> findByStudent(Student student);
    List<TeamStudent> findByTeamTournament(TeamTournament teamTournament);
    List<TeamStudent> findByStudentAndTeamTournament(Student student, TeamTournament teamTournament);
    @Query(value = "SELECT team_student.* FROM professional_esports.team_student LEFT JOIN team_tournament " +
            "ON team_tournament.id = team_tournament_id " +
            "where student_id = :studentId and category_id= :categoryId", nativeQuery = true)
    List<TeamStudent> getListTeamJoinedByCate(@Param("studentId") Long studentId, @Param("categoryId") Long categoryId);
}
