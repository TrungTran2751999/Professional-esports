package com.cg.domain.esport.dto;

import com.cg.domain.esport.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    @NotBlank(message = "Tên thể loại không được để trống")
    @Pattern(regexp = "^[A-Za-z\s]*$", message = "Tên không đúng định dạng")
    @Size(min = 5, max = 100, message = "Tên thể loại phải lớn hơn 5 và nhỏ hơn 100 kí tự")
    private String name;

    private String signal;

    @NotNull(message = "avartar không được bỏ trống")
    private MultipartFile avartar;
    public Category toCategory(){
        return new Category()
                .setName(name)
                .setBrief(signal);
    }
}
