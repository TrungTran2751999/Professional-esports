package com.cg.api.esport;

import com.cg.domain.esport.dto.*;
import com.cg.domain.esport.entities.Avartar;
import com.cg.exception.DataInputException;
import com.cg.service.esport.avartar.IAvartarService;
import com.cg.service.esport.student.IStudentService;
import com.cg.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
public class StudentAPI {
    @Autowired
    private IStudentService studentService;
    @Autowired
    private IAvartarService avartarService;
    @Autowired
    private AppUtils appUtils;
    @PostMapping("/filter")
    public ResponseEntity<?> filter(@Validated @RequestBody StudentFilter studentFilter, BindingResult bindingResult,
                                    @RequestParam(name = "sort", required = false, defaultValue = "ASC") String sort){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        int start = studentFilter.getStart();
        int limit = studentFilter.getLength();
        int page = start/limit+1;
        Sort sortable = null;
        if(sort.equals("ASC")){
            sortable = Sort.by("id").ascending();
        }
        if(sort.equals("DESC")){
            sortable = Sort.by("id").descending();
        }
        Pageable pageable = PageRequest.of(page-1, limit ,sortable);
        Page<StudentResponseDTO> pageStudent = studentService.filter(studentFilter, pageable);
        List<StudentResponseDTO> listStudent = pageStudent.getContent();
        return new ResponseEntity<>(listStudent, HttpStatus.OK);
    }
    @PostMapping("/admin/filter")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> filterByAdmin(@Validated @RequestBody StudentFilter studentFilter, BindingResult bindingResult,
                                    @RequestParam(name = "sort", required = false, defaultValue = "ASC") String sort){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        int start = studentFilter.getStart();
        int limit = studentFilter.getLength();
        int page = start/limit+1;
        Sort sortable = null;
        if(sort.equals("ASC")){
            sortable = Sort.by("id").ascending();
        }
        if(sort.equals("DESC")){
            sortable = Sort.by("id").descending();
        }
        Pageable pageable = PageRequest.of(page-1, limit ,sortable);
        Page<StudentResSecurity> pageStudent = studentService.filterByAdmin(studentFilter, pageable);
        List<StudentResSecurity> listStudent = pageStudent.getContent();
        return new ResponseEntity<>(listStudent, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id){
        return new ResponseEntity<>(studentService.getStudentNoCode(id), HttpStatus.OK);
    }
    @GetMapping("/team/{id}")
    public ResponseEntity<?> getTeamJoined(@PathVariable("id") Long id, @RequestParam("deleted") Boolean deleted){
        return new ResponseEntity<>(studentService.getListTeamJoined(id, deleted), HttpStatus.OK);
    }
    @GetMapping("/security/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> getByIdSecure(@PathVariable("id") Long id, @RequestParam("code") String code){
        return new ResponseEntity<>(studentService.getStudentSecurity(id,code), HttpStatus.OK);
    }
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getByIdByAdmin(@PathVariable("id") Long id){
        return new ResponseEntity<>(studentService.getStudentByAdmin(id), HttpStatus.OK);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> updateNoAvartar(@Validated @RequestBody StudentUpdateDTO studentUpdateDTO, BindingResult bindingResult){
        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }
        return new ResponseEntity<>(studentService.updateNoAvartar(studentUpdateDTO), HttpStatus.OK);
    }
    @PostMapping("/avartar")
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> updateAvartar(@Validated OrganizerAvartarDTO organizerAvartarDTO, BindingResult bindingResult){
        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }
        String[] imageExtension = new String[]{"image/png", "image/jpg", "image/jpeg"};
        for(int i=0; i<imageExtension.length; i++){
            if(Objects.equals(organizerAvartarDTO.getAvartar().getContentType(), imageExtension[i])){
                 return new ResponseEntity<>(studentService.updateAvartar(organizerAvartarDTO), HttpStatus.OK);
            }else if(i == imageExtension.length-1){
                throw new DataInputException("Ảnh đại diện không đúng định dạng");
            }
        }
        return null;
    }
    @PostMapping("/deleted/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> setDeleted(@RequestParam("deleted") Boolean deleted, @PathVariable("id") Long id){
        studentService.setDeleted(deleted, id);
        return new ResponseEntity<>("UPDATE SUCCESS", HttpStatus.OK);
    }
    @PostMapping("/join")
    public ResponseEntity<?> joinTeam(@Validated @RequestBody StudentJoinTeam studentJoinTeam, BindingResult bindingResult){
        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }
        studentService.joinTeam(studentJoinTeam);
        return new ResponseEntity<>("JOIN TEAM SUCCESS", HttpStatus.OK);
    }
    @PostMapping("/leave")
    public ResponseEntity<?> leaveTeam(@Validated @RequestBody StudentJoinTeam studentJoinTeam, BindingResult bindingResult){
        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }
        studentService.leaveTeam(studentJoinTeam);
        return new ResponseEntity<>("LEAVE TEAM SUCCESS", HttpStatus.OK);
    }
    @GetMapping("/category")
    public ResponseEntity<?> getTeamByCategory(@Validated @RequestBody StudentByCategory studentByCategory, BindingResult bindingResult){
        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }
        List<TeamStudentResDTO> result = studentService.getListTeamJoinedByCategory(studentByCategory.getUserId(),
                                                   studentByCategory.getCode(),
                                                   studentByCategory.getCategoryId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
