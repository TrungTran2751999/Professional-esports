package com.cg.domain.esport.dto;

import com.cg.domain.esport.entities.TeamTournament;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {
    @NotNull
    private Long leaderUserId;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z\s]*$", message = "Tên nhóm không đúng định dạng")
    private String name;

    @NotNull
    private Long categoryId;

    @NotNull
    private MultipartFile avartar;

    @NotBlank
    private String code;

}
