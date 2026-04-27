package com.example.vodtbank.config;

import java.util.List;

import com.example.vodtbank.authentication.dto.SignUpRequest;
import com.example.vodtbank.authentication.entity.User;
import com.example.vodtbank.authentication.repository.UserRepository;
import com.example.vodtbank.common.dto.BaseDto;
import com.example.vodtbank.common.entity.BaseEntity;
import com.example.vodtbank.role.dto.SystemRole;
import com.example.vodtbank.role.entity.Role;
import com.example.vodtbank.role.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
@EnableScheduling
public class AppConfig {
	@Value("${admin.email}")
	private String adminEmail;

	@Value("${admin.password}")
	private String adminPassword;

	/**
	 * This bean is used to configure the Thymeleaf template engine.
	 */
	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix("templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML");
		templateResolver.setCharacterEncoding("UTF-8");

		templateEngine.setTemplateResolver(templateResolver);
		return templateEngine;
	}

	/**
	 * This bean is used to configure the ModelMapper for object mapping.
	 */
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration()
				.setFieldMatchingEnabled(true)
				.setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
				.setMatchingStrategy(MatchingStrategies.STANDARD);

		modelMapper.addMappings(new PropertyMap<BaseDto, BaseEntity>() {
			@Override
			protected void configure() {
				skip(destination.getId());
				skip(destination.getCreatedAt());
				skip(destination.getUpdatedAt());
			}
		});

		modelMapper.addMappings(new PropertyMap<SignUpRequest, User>() {
			@Override
			protected void configure() {
				skip(destination.getRoles());
				skip(destination.getPassword());
			}
		});

		return modelMapper;
	}

	@Bean
	CommandLineRunner initAdmin(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
		return args -> {
			if (userRepository.findByEmail(adminEmail).isEmpty()) {
				User admin = new User();
				admin.setFirstName("Admin");
				admin.setEmail(adminEmail);
				admin.setPassword(encoder.encode(adminPassword));

				Role adminRole = roleRepository.findByName(SystemRole.ADMIN.name())
						.orElseThrow(() -> new RuntimeException("Admin role not found in database"));

				admin.setRoles(List.of(adminRole));

				userRepository.save(admin);
			}
		};
	}
}
