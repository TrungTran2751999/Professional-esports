package com.cg.domain.esport.entities;

import com.cg.domain.esport.dto.AvartarDTO;
import com.cg.domain.esport.dto.StudentDTO;
import com.cg.domain.esport.dto.StudentResSecurity;
import com.cg.domain.esport.dto.StudentResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "students")
public class Student extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(columnDefinition = "decimal(12,0) default 0")
    private BigDecimal balance;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "name")
    private String nickName;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User user;

    public StudentDTO toStudentDTO(){
        return new StudentDTO()
                .setId(id)
                .setEmail(email)
                .setBalance(balance)
                .setPhoneNumber(phoneNumber)
                .setNickName(nickName);
    }
    public StudentResponseDTO toStudentResponseDTO(AvartarDTO avartarDTO){
        return new StudentResponseDTO()
                .setId(id)
                .setName(nickName)
                .setAvartarDTO(avartarDTO);
    }
    public StudentResSecurity toStudentResSecurity(AvartarDTO avartarDTO){
        return new StudentResSecurity()
                .setId(id)
                .setNickName(nickName)
                .setPhoneNumber(phoneNumber)
                .setBalance(balance)
                .setEmail(email)
                .setAvartarDTO(avartarDTO);
    }
}
