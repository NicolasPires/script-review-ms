package com.cooperfilme.scriptreview.config;

import com.cooperfilme.scriptreview.entity.User;
import com.cooperfilme.scriptreview.enums.Role;
import com.cooperfilme.scriptreview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User analyst = User.builder()
                        .name("Analyst 1")
                        .email("analyst@cooperfilme.com")
                        .password(passwordEncoder.encode("123456"))
                        .role(Role.ANALYST)
                        .build();

                User reviewer = User.builder()
                        .name("Reviewer 1")
                        .email("reviewer@cooperfilme.com")
                        .password(passwordEncoder.encode("123456"))
                        .role(Role.REVIEWER)
                        .build();

                List<User> approvers = List.of(
                        User.builder().name("Approver 1").email("approver1@cooperfilme.com").password(passwordEncoder.encode("123456")).role(Role.APPROVER).build(),
                        User.builder().name("Approver 2").email("approver2@cooperfilme.com").password(passwordEncoder.encode("123456")).role(Role.APPROVER).build(),
                        User.builder().name("Approver 3").email("approver3@cooperfilme.com").password(passwordEncoder.encode("123456")).role(Role.APPROVER).build()
                );

                userRepository.save(analyst);
                userRepository.save(reviewer);
                userRepository.saveAll(approvers);

                System.out.println("✔ Usuários padrão criados com sucesso.");
            }
        };
    }
}
