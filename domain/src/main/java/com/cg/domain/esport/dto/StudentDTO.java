package com.cg.domain.esport.dto;

import com.cg.domain.esport.entities.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "Tên đăng nhập không đúng định dạng")
    @Length(min = 5, max = 50, message = "Độ dài tên phải nhỏ hơn 50 và lớn hơn 5 kí tự")
    private String name;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Length(min = 10, max = 50, message = "Độ dài mật khẩu phải nhỏ hơn 50 kí tự và lớn hơn 10 kí tự")
    private String password;

    @NotBlank(message = "Email không đuợc để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Tên hiển thị không được để trống")
    @Pattern(regexp = "^[A-Za-z]*$", message = "Tên đăng nhập không đúng định dạng")
    @Length(min = 5, max = 50, message = "Độ dài tên phải nhỏ hơn 50 và lớn hơn 5 kí tự")
    private String nickName;

    private BigDecimal balance;
    @NotBlank(message = "SDT không được để trống")
    @Pattern(regexp = "^0[0-9]{9}$", message = "SDT không đúng định dạng")
    private String phoneNumber;

    public Student toStudent(){
        return new Student()
                .setId(id)
                .setNickName(nickName)
                .setEmail(email)
                .setBalance(balance)
                .setPhoneNumber(phoneNumber);
    }
}
