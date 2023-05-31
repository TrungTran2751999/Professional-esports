package com.cg.service.esport.student;

import com.cg.domain.esport.dto.*;
import com.cg.domain.esport.entities.*;
import com.cg.exception.DataInputException;
import com.cg.repository.esport.*;
import com.cg.service.esport.avartar.IAvartarService;
import com.cg.service.esport.category.ICategoryService;
import com.cg.service.esport.securitycode.ISecurityCodeService;
import com.cg.service.esport.teamStudent.ITeamStudentService;
import com.cg.service.esport.teamTournament.ITeamService;
import com.cg.service.esport.user.IUserService;
import com.cg.utils.AppUtils;
import com.cg.utils.GooglePojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Service
public class StudentServiceImp implements IStudentService{
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private StudentFilterRepository studentFilterRepository;

    @Autowired
    private IUserService userService;
    @Autowired
    private ITeamStudentService teamStudentService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private TeamStudentRepository teamStudentRepository;

    @Autowired
    private ITeamService teamService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IAvartarService avartarService;
    @Autowired
    private ISecurityCodeService securityCodeService;
    @Autowired
    private AppUtils appUtils;

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    public Student getById(Long id) {
        return studentRepository.getById(id);
    }

    @Override
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public void remove(Long id) {
        studentRepository.deleteById(id);
    }
    @Override
    public Page<StudentResponseDTO> filter(StudentFilter studentFilter, Pageable pageable) {
        return studentFilterRepository.findAllByFilters(studentFilter,pageable).map(student -> {
            Avartar avartar = avartarService.findByStudent(student);
            return student.toStudentResponseDTO(avartar.toAvartarDTO());
        });
    }

    @Override
    public Page<StudentResSecurity> filterByAdmin(StudentFilter studentFilter, Pageable pageable) {
        return studentFilterRepository.findAllByFilters(studentFilter,pageable).map(student -> {
            Avartar avartar = avartarService.findByStudent(student);
            return student.toStudentResSecurity(avartar.toAvartarDTO());
        });
    }


    @Override
    @Transactional
    public StudentResponseDTO createStudent(StudentDTO studentDTO) {
        List<Student> studentCheckEmail = studentRepository.findByEmail(studentDTO.getEmail());
        if(studentCheckEmail.size()==0){
            Role role = roleRepository.getById(3L);
            User user = (User) new User()
                    .setUsername(studentDTO.getName())
                    .setPassword(studentDTO.getPassword())
                    .setRole(role)
                    .setDeleted(false);
            user = userService.save(user);
            SecurityCode securityCode = securityCodeService.save(new SecurityCode().setUser(user));
            user.setCodeSecurity(securityCode.getId());
            Student studentResult = studentRepository.save((Student) studentDTO.toStudent().setUser(user).setDeleted(false));
            Avartar avartar = new Avartar();
            avartar = avartarService.save(avartar);
            avartar.setStudent(studentResult);
            return studentResult.toStudentResponseDTO(avartar.toAvartarDTO());
        }else{
            throw new DataInputException("Email đã tồn tại");
        }
    }

    @Override
    @Transactional
    public StudentResSecurity createStudentByGoogle(GooglePojo googlePojo) {
        Role role = roleRepository.getById(3L);
        User user = (User) new User()
                .setUsername(googlePojo.getEmail())
                .setPassword(passwordEncoder.encode(googlePojo.getId()))
                .setRole(role)
                .setDeleted(false);
        user = userService.save(user);
        SecurityCode securityCode = securityCodeService.save(new SecurityCode().setUser(user));
        user.setCodeSecurity(securityCode.getId());
        Student student = (Student) new Student()
                                .setEmail(googlePojo.getEmail())
                                .setNickName(googlePojo.getEmail().split("@")[0])
                                .setBalance(new BigDecimal(0))
                                .setUser(user)
                                .setDeleted(false);
        student = studentRepository.save(student);
        Avartar avartar = new Avartar();
        avartar = avartarService.save(avartar);
        avartar = avartar.setStudent(student);
        return student.toStudentResSecurity(avartar.toAvartarDTO());
    }

    @Override
    public StudentResponseDTO getStudentNoCode(Long id) {
        Optional<Student> student = studentRepository.findById(id);
        if(student.isPresent()){
            Avartar avartar = avartarService.findByStudent(student.get());
            return student.get().toStudentResponseDTO(avartar.toAvartarDTO());
        }else{
            throw new DataInputException("Học viên không tồn tại");
        }
    }

    @Override
    public List<TeamStudentResDTO> getListTeamJoined(Long id, Boolean deleted) {
        return teamStudentService.findByStudent(id, deleted);
    }

    @Override
    public List<TeamStudentResDTO> getListTeamJoinedByCategory(Long userId, String code, Long categoryId) {
        User user = userService.findByCodeSecurity(code);
        if(user == null) throw new DataInputException("notFoundStudent");

        if(!Objects.equals(user.getId(), userId)) throw new DataInputException("notFoundStudent");

        Student student = studentRepository.findByUser(user);
        if(student == null) throw new DataInputException("notFoundStudent");

        return teamStudentService.getListTeamJoinedByCate(student.getId(), categoryId);
    }


    @Override
    public StudentResSecurity getStudentSecurity(Long userId, String code) {
        try{
            User user = userService.findByCodeSecurity(code);
            if(user != null){
                if(Objects.equals(user.getId(), userId)){
                    Student student = studentRepository.findByUser(user);
                    Avartar avartar = avartarService.findByStudent(student);
                    return student.toStudentResSecurity(avartar.toAvartarDTO());
                }
            }
            throw new DataInputException("Học viên không tồn tại");
        }catch (Exception e){
            throw new DataInputException("Học viên không tồn tại");
        }
    }

    @Override
    public StudentResSecurity getStudentByAdmin(Long id) {
        try{
            Student student = getById(id);
            if(student != null){
                Avartar avartar = avartarService.findByStudent(student);
                return student.toStudentResSecurity(avartar.toAvartarDTO());
            }
            throw new DataInputException("Học viên không tồn tại");
        }catch (Exception e){
            throw new DataInputException("Học viên không tồn tại");
        }
    }

    @Override
    public StudentResSecurity findByAdmin(User user) {
        Student student = studentRepository.findByUser(user);
        Avartar avartar = avartarService.findByStudent(student);
        return student.toStudentResSecurity(avartar.toAvartarDTO());
    }

    @Override
    @Transactional
    public StudentResSecurity updateNoAvartar(StudentUpdateDTO studentUpdateDTO) {
            User user = userService.findByCodeSecurity(studentUpdateDTO.getCode());
            if(user == null) throw new DataInputException("Học viên không tồn tại");
            Long userId = studentUpdateDTO.getUserId();
            if (Objects.equals(user.getId(), userId) && user.getId() != null && userId != null) {
                Student student = studentRepository.findByUser(user);

                List<Student> studentCheckEmail = studentRepository.findByEmail(studentUpdateDTO.getEmail());
                if(studentCheckEmail.size() > 0 && !Objects.equals(student.getEmail(), studentCheckEmail.get(0).getEmail()))
                    throw new DataInputException("EmailExist");

                Avartar avartar = avartarService.findByStudent(student);
                return studentRepository.save(
                                studentUpdateDTO.toStudent()
                                        .setId(student.getId())
                                        .setUser(user)
                                        .setBalance(student.getBalance()))
                        .toStudentResSecurity(avartar.toAvartarDTO());
            } else {
                throw new DataInputException("Học viên không tồn tại");
            }
    }

    @Override
    @Transactional
    public StudentResSecurity updateAvartar(OrganizerAvartarDTO organizer) {
        User user = userService.findByCodeSecurity(organizer.getCode());
        if(user == null) throw new DataInputException("Học viên không tồn tại");
        try{
            if(Objects.equals(user.getId(), organizer.getUserId()) && organizer.getCode() != null && organizer.getUserId() != null){
                Student student = studentRepository.findByUser(user);
                Avartar avartar = avartarService.findByStudent(student);
                String fileIdDelete = avartar.getFileUrl();
                avartarService.save(avartar.setFileUrl(appUtils.uploadAvartar(organizer.getAvartar(), avartar.getId())));
                if(fileIdDelete!= null){
                    String fileId = fileIdDelete.split("&")[1].split("=")[1];
                    try {
                        appUtils.deleteFile(fileId);
                    } catch (Exception e) {
                        throw new DataInputException("FAIL");
                    }
                }
                return student.toStudentResSecurity(avartar.toAvartarDTO());
            }else{
                throw new DataInputException("Học viên không tồn tại");
            }
        }catch (Exception e){
            throw new DataInputException("Học viên không tồn tại");
        }
    }

    @Override
    @Transactional
    public void setDeleted(Boolean deleted, Long id) {
        Optional<Student> student = studentRepository.findById(id);
        if(student.isPresent()){
            User user = userService.getById(student.get().getUser().getId());
            userService.save((User) user.setDeleted(deleted));
            studentRepository.save((Student) studentRepository.findByUser(user).setDeleted(deleted));
        }else{
            throw new DataInputException("Học viên không tồn tại");
        }
    }

    @Override
    public void joinTeam(StudentJoinTeam studentJoinTeam) {
        User user = userService.findByCodeSecurity(studentJoinTeam.getCode());
        if(user == null) throw new DataInputException("notFoundStudent");

        Optional<TeamTournament> teamTournamentOpt = teamService.findById(studentJoinTeam.getTeamId());
        if(!teamTournamentOpt.isPresent()) throw new DataInputException("notFoundTeam");

        Student student = studentRepository.findByUser(user);
        if(student == null) throw new DataInputException("notFoundStudent");


        if(!Objects.equals(user.getId(), studentJoinTeam.getUserId())) throw new DataInputException("notFoundStudent");

        teamStudentService.joinTeam(student.getId(), teamTournamentOpt.get().getId());
    }

    @Override
    public void leaveTeam(StudentJoinTeam studentJoinTeam) {
        User user = userService.findByCodeSecurity(studentJoinTeam.getCode());
        if(user == null) throw new DataInputException("notFoundStudent");

        Student student = studentRepository.findByUser(user);
        if(student == null) throw new DataInputException("notFoundStudent");

        if(!Objects.equals(user.getId(), studentJoinTeam.getUserId())) throw new DataInputException("notFoundStudent");

        Optional<TeamTournament> teamTournamentOpt = teamService.findById(studentJoinTeam.getTeamId());
        if(!teamTournamentOpt.isPresent()) throw new DataInputException("notFoundTeam");

        if(Objects.equals(teamTournamentOpt.get().getLeader().getId(), student.getId())) throw new DataInputException("notLeaveLeader");

        List<StudentTeamResDTO> teamStudentList = teamStudentService.findByTeam(studentJoinTeam.getTeamId(), false);
        for(int i=0; i<teamStudentList.size(); i++){
            if(Objects.equals(teamStudentList.get(i).getStudentResponseDTO().getId(), student.getId())){
                teamStudentService.leaveTeam(student.getId(), student.getId());
                return;
            }
        }
        throw new DataInputException("notFoundStudentInTeam");

    }


}
