package com.cg.domain.esport.dto;

import com.cg.domain.esport.entities.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentResSecurity {
    private Long id;
    private String nickName;
    private String email;
    private String phoneNumber;
    private BigDecimal balance;
    private AvartarDTO avartarDTO;
    public Student toStudent(){
        return new Student()
                .setId(id)
                .setNickName(nickName)
                .setEmail(email)
                .setPhoneNumber(phoneNumber)
                .setBalance(balance);
    }

    @Override
    public String toString() {
        return "{id:" + id +
                ", nickName='" + nickName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", balance=" + balance +
                ", avartarDTO=" + avartarDTO +
                "}";
    }
}
