package com.cg.service;

import com.cg.domain.esport.entities.Role;
import com.cg.domain.esport.entities.SecurityCode;
import com.cg.domain.esport.entities.TeamLimit;
import com.cg.domain.esport.entities.User;
import com.cg.domain.esport.enums.EnumRole;
import com.cg.repository.esport.*;
import com.cg.service.esport.role.IRoleService;
import com.cg.service.esport.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInit implements ApplicationRunner {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private TeamLimitRepository teamLimitRepository;
    private CodeSecurityRepository codeSecurityRepository;

    @Autowired
    public DataInit(UserRepository userRepository,
                    RoleRepository roleRepository,
                    PasswordEncoder passwordEncoder,
                    TeamLimitRepository teamLimitRepository,
                    CodeSecurityRepository codeSecurityRepository){
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.teamLimitRepository = teamLimitRepository;
        this.codeSecurityRepository = codeSecurityRepository;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(!roleRepository.existsById(1L) && !userRepository.existsById(1L)){
            Role[] listRole = new Role[]{
                    new Role(null,"ROLE_ADMIN", EnumRole.ADMIN),
                    new Role(null,"ROLE_PARTNER", EnumRole.PARTNER),
                    new Role(null,"ROLE_STUDENT", EnumRole.STUDENT),
                    new Role(null,"ROLE_MENTOR", EnumRole.MENTOR),
                    new Role(null,"ROLE_ORGANIZER", EnumRole.ORGANIZER)
            };
            TeamLimit[] listTeamLimit = new TeamLimit[]{
                    new TeamLimit(null, 8),
                    new TeamLimit(null, 16),
                    new TeamLimit(null, 32),
                    new TeamLimit(null, 64)
            };
            User user = (User) new User()
                    .setUsername("admin")
                    .setPassword(passwordEncoder.encode("12345"))
                    .setRole(roleRepository.getById(1L))
                    .setDeleted(false);
            SecurityCode securityCode = codeSecurityRepository.save(new SecurityCode().setUser(user));
            teamLimitRepository.saveAll(Arrays.asList(listTeamLimit));
            roleRepository.saveAll(Arrays.asList(listRole));
            userRepository.save(user.setCodeSecurity(securityCode.getId()));
        }
    }
}
