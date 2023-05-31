package com.cg.domain.esport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryAvartarDTO {
    private Long id;
    private MultipartFile avartar;
}
