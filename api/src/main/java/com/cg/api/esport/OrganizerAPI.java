package com.cg.api.esport;

import com.cg.domain.esport.dto.*;
import com.cg.domain.esport.entities.Organizer;
import com.cg.domain.esport.entities.User;
import com.cg.exception.DataInputException;
import com.cg.exception.UnauthorizedException;
import com.cg.repository.esport.OrganizerFilterRepository;
import com.cg.repository.esport.OrganizerRepository;
import com.cg.service.esport.avartar.IAvartarService;
import com.cg.service.esport.jwt.JwtService;
import com.cg.service.esport.organizer.IOrganizerService;
import com.cg.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/organizer")
public class OrganizerAPI {
    @Autowired
    private IOrganizerService organizerService;
    @Autowired
    private IAvartarService avartarService;
    @Autowired
    private AppUtils appUtils;
    @Autowired
    private OrganizerFilterRepository organizerFilterRepository;

    @PostMapping("/filter")
    public ResponseEntity<?> filter(@Validated @RequestBody OrganizerFilter organizerFilter, BindingResult bindingResult,
                                    @RequestParam(name = "sort", required = false, defaultValue = "ASC") String sort){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        int start = organizerFilter.getStart();
        int length = organizerFilter.getLength();
        int page = start / length + 1;
        Sort sortable = null;
        if (sort.equals("ASC")) {
            sortable = Sort.by("id").ascending();
        }
        if (sort.equals("DESC")) {
            sortable = Sort.by("id").descending();
        }
        Pageable pageable = PageRequest.of(page - 1, length, sortable);
        Page<OrganizerResponseDTO> pageableCustomers = organizerService.filter(organizerFilter, pageable);
        List<OrganizerResponseDTO> listOrganizer = pageableCustomers.getContent();
        return new ResponseEntity<>(listOrganizer, HttpStatus.OK);
    }
    @PostMapping("/admin/filter")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> filterByAdmin(@Validated @RequestBody OrganizerFilter organizerFilter, BindingResult bindingResult,
                                    @RequestParam(name = "sort", required = false, defaultValue = "ASC") String sort){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        int start = organizerFilter.getStart();
        int length = organizerFilter.getLength();
        int page = start / length + 1;
        Sort sortable = null;
        if (sort.equals("ASC")) {
            sortable = Sort.by("id").ascending();
        }
        if (sort.equals("DESC")) {
            sortable = Sort.by("id").descending();
        }
        Pageable pageable = PageRequest.of(page - 1, length, sortable);
        Page<OrganizerResSecurity> pageableCustomers = organizerService.filterByAdmin(organizerFilter, pageable);
        List<OrganizerResSecurity> listOrganizer = pageableCustomers.getContent();
        return new ResponseEntity<>(listOrganizer, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id){
        try{
            Organizer organizer = organizerService.getById(id);
            AvartarDTO avartarDTO = avartarService.findByOrganizer(organizer).toAvartarDTO();
            return new ResponseEntity<>(organizerService.getById(id).toOrganizerResponseDTO(avartarDTO), HttpStatus.OK);
        }catch (Exception e){
            throw new DataInputException("Không tìm thấy nhà tổ chức");
        }
    }
    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<?> updateOrganizer(@Validated @RequestBody OrganizerUpdateDTO organizerDTO, BindingResult bindingResult){
        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }
            return new ResponseEntity<>(organizerService.updateOrganizerNoAvarTar(organizerDTO), HttpStatus.OK);
    }
    @GetMapping("/security/{code}")
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<?> findSecurity(@PathVariable("code") String code){
        try{
            OrganizerResSecurity organizerResponseDTO = organizerService.findByCodeSecurity(code);
            return new ResponseEntity<>(organizerResponseDTO, HttpStatus.OK);
        }catch (Exception e){
            throw new DataInputException("Không tìm tìm thấy nhà tổ chức");
        }
    }
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getByIdByAdmin(@PathVariable("id") Long id){
        return new ResponseEntity<>(organizerService.findByAdmin(id), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZER')")
    @PostMapping("/avartar")
    public ResponseEntity<?> updateImage(@Validated OrganizerAvartarDTO organizerAvartarDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        String[] imageExtension = new String[]{"image/png", "image/jpg", "image/jpeg"};
        for(int i=0; i<imageExtension.length; i++){
            if(Objects.equals(organizerAvartarDTO.getAvartar().getContentType(), imageExtension[i])){
                return new ResponseEntity<>(organizerService.updateAvartar(organizerAvartarDTO), HttpStatus.OK);
            }else if(i == imageExtension.length-1){
                throw new DataInputException("Ảnh đại diện không đúng định dạng");
            }
        }
        return null;
    }
    @PostMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> setDeleted(@RequestParam("deleted") Boolean deleted, @RequestParam("id") Long id){
        organizerService.setDeleted(deleted, id);
        return new ResponseEntity<>("UPDATE SUCCESS", HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteOrganizer(@PathVariable("id") Long id){
        organizerService.deleteOrganizer(id);
        return new ResponseEntity<>("DELETE SUCCESS", HttpStatus.OK);
    }
}
