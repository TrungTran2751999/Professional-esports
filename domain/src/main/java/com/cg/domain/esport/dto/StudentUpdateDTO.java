package com.cg.domain.esport.dto;

import com.cg.domain.esport.entities.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpdateDTO {
    @NotNull(message = "id không được để trống")
    private Long userId;
    @NotBlank(message = "Tên không được để trống")
    @Pattern(regexp = "^[A-Za-z]*$", message = "Tên không đúng định dạng")
    @Size(min=5, max=30, message = "Độ dài tên phải nhỏ hơn 30 và lớn hơn 5 kí tự")
    private String nickName;
    @Email(message = "Email không đúng định dạng")
    private String email;
    @NotBlank(message = "SDT không được để trống")
    @Pattern(regexp = "^0[0-9]{9}$", message = "SDT không đúng định dạng")
    private String phoneNumber;
    @NotBlank(message = "Code không được bỏ trống")
    private String code;

    public Student toStudent(){
        return new Student()
                .setId(userId)
                .setPhoneNumber(phoneNumber)
                .setEmail(email)
                .setNickName(nickName);
    }
}
