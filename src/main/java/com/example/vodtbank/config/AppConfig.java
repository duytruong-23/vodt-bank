package com.example.vodtbank.config;

import com.example.vodtbank.authentication.dto.SignUpRequest;
import com.example.vodtbank.authentication.entity.User;
import com.example.vodtbank.common.dto.BaseDto;
import com.example.vodtbank.common.entity.BaseEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class AppConfig {

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
}
