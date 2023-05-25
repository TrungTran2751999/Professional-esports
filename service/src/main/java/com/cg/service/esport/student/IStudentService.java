package com.cg.service.esport.student;

import com.cg.domain.esport.dto.*;
import com.cg.domain.esport.entities.Student;
import com.cg.domain.esport.entities.User;
import com.cg.service.IGeneralService;
import com.cg.utils.GooglePojo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IStudentService extends IGeneralService<Student> {
    StudentResponseDTO createStudent(StudentDTO studentDTO);
    StudentResSecurity createStudentByGoogle(GooglePojo googlePojo);
    StudentResponseDTO getStudentNoCode(Long id);
    StudentResSecurity getStudentSecurity(Long id, String code);
    StudentResSecurity getStudentByAdmin(Long id);
    StudentResSecurity findByAdmin(User user);
    StudentResSecurity updateNoAvartar(StudentUpdateDTO studentUpdateDTO);
    StudentResSecurity updateAvartar(OrganizerAvartarDTO organizerAvartarDTO);
    Page<StudentResponseDTO> filter(StudentFilter studentFilter, Pageable pageable);
    Page<StudentResSecurity> filterByAdmin(StudentFilter studentFilter, Pageable pageable);
    void setDeleted(Boolean deleted, Long id);
}
