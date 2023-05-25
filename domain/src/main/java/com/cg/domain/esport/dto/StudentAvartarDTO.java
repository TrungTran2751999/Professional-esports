package com.cg.domain.esport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentAvartarDTO {
    @NotNull(message = "id không được để trống")
    private Long userId;

    @NotBlank(message = "code không được để trống")

    private String code;

    @NotNull(message = "Ảnh đại diện không được để trống")
    private MultipartFile avartar;
}
