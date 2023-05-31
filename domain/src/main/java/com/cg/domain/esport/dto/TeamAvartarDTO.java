package com.cg.domain.esport.dto;

import com.cg.domain.esport.entities.TeamTournament;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamAvartarDTO {
    @NotNull(message = "id nhóm không được để trống")
    private Long teamId;

    @NotNull(message = "id nhóm trưởng không được để trống")
    private Long userId;

    @NotNull(message = "code không được để trống")
    private String code;

    @NotNull(message = "Ảnh đại diện không được để trống")
    private MultipartFile avartar;
}
