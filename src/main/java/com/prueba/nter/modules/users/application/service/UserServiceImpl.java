package com.prueba.nter.modules.users.application.service;

import com.prueba.nter.commons.Constants;
import com.prueba.nter.commons.application.JsonFileReader;
import com.prueba.nter.error.exception.AlreadyExistsException;
import com.prueba.nter.modules.users.application.service.port.UserService;
import com.prueba.nter.modules.users.domain.UserEntity;
import com.prueba.nter.modules.users.infrastructure.dto.input.UserInputDto;
import com.prueba.nter.modules.users.infrastructure.dto.ouput.UserOutputDto;
import com.prueba.nter.modules.users.infrastructure.mapper.UserMapper;
import com.prueba.nter.modules.users.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of {@link UserService}.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JsonFileReader jsonFileReader;

    @Override
    @Transactional(readOnly = true)
    public List<UserOutputDto> getAll() {
        return userMapper.toOutputList(userRepository.findAll());
    }

    @Override
    @Transactional
    public List<UserOutputDto> importFromFile(MultipartFile file) {
        List<UserInputDto> inputs = jsonFileReader.read(file, UserInputDto.class);
        Set<String> processed = new HashSet<>();
        List<UserEntity> users = new ArrayList<>();

        for (UserInputDto input : inputs) {
            UserInputDto normalized = input.normalized();
            checkUniqueness(input.email(), normalized.email(), processed);
            users.add(userMapper.toEntity(normalized));
        }
        return userMapper.toOutputList(userRepository.saveAll(users));
    }

    private void checkUniqueness(String originalEmail, String normalizedEmail, Set<String> processed) {
        if (!processed.add(normalizedEmail) || userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new AlreadyExistsException(
                    MessageFormat.format(Constants.ERROR_USER_EXISTS, originalEmail));
        }
    }
}
