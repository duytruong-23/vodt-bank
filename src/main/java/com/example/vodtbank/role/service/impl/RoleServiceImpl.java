package com.example.vodtbank.role.service.impl;

import java.util.List;

import com.example.vodtbank.exception.BadRequestException;
import com.example.vodtbank.exception.NotFoundException;
import com.example.vodtbank.role.dto.RoleDto;
import com.example.vodtbank.role.entity.Role;
import com.example.vodtbank.role.repository.RoleRepository;
import com.example.vodtbank.role.service.RoleService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor =  Exception.class)
public class RoleServiceImpl implements RoleService {
	private final RoleRepository roleRepository;
	private final ModelMapper modelMapper;

	public RoleServiceImpl(RoleRepository roleRepository, ModelMapper modelMapper) {
		this.roleRepository = roleRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public RoleDto createRole(RoleDto roleDto) {
		if(roleRepository.findByName(roleDto.getName()).isPresent()) {
			throw new BadRequestException("Role with name " + roleDto.getName() + " already exists");
		}

		Role role = modelMapper.map(roleDto, Role.class);
		return modelMapper.map(roleRepository.save(role), RoleDto.class);
	}

	@Override
	public RoleDto updateRole(RoleDto roleDto) {
		Role role = roleRepository.findById(roleDto.getId())
				.orElseThrow(() -> new NotFoundException("Role with id " + roleDto.getId() + " not found"));

		role.setName(roleDto.getName());

		return modelMapper.map(roleRepository.save(role), RoleDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RoleDto> getAllRoles() {
		return roleRepository.findAll().stream()
				.map(role -> modelMapper.map(role, RoleDto.class))
				.toList();
	}

	@Override
	public void deleteRole(Long roleId) {
		if(!roleRepository.existsById(roleId)) {
			throw new NotFoundException("Role with id " + roleId + " not found");
		}

		roleRepository.deleteById(roleId);
	}
}
