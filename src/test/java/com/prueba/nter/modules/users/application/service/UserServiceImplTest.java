package com.prueba.nter.modules.users.application.service;

import com.prueba.nter.commons.application.JsonFileReader;
import com.prueba.nter.error.exception.AlreadyExistsException;
import com.prueba.nter.modules.users.domain.UserEntity;
import com.prueba.nter.modules.users.infrastructure.dto.input.UserInputDto;
import com.prueba.nter.modules.users.infrastructure.mapper.UserMapper;
import com.prueba.nter.modules.users.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    private static final MultipartFile FILE =
            new MockMultipartFile("file", "Users.json", "application/json", "[]".getBytes());

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private JsonFileReader jsonFileReader;

    @InjectMocks
    private UserServiceImpl userService;

    private UserInputDto input(String email) {
        return new UserInputDto("Laura Fernández", email, LocalDate.of(2019, 5, 10));
    }

    @Test
    void shouldImportUsers() {
        UserInputDto input = input("laura.fernandez@gmail.es");
        when(jsonFileReader.read(FILE, UserInputDto.class)).thenReturn(List.of(input));
        when(userRepository.existsByEmail(input.email())).thenReturn(false);
        when(userMapper.toEntity(input)).thenReturn(UserEntity.builder().email(input.email()).build());
        when(userRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        userService.importFromFile(FILE);

        verify(userRepository).saveAll(any());
    }

    @Test
    void shouldRejectEmailAlreadyStored() {
        UserInputDto input = input("laura.fernandez@gmail.es");
        when(jsonFileReader.read(FILE, UserInputDto.class)).thenReturn(List.of(input));
        when(userRepository.existsByEmail(input.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.importFromFile(FILE))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("laura.fernandez@gmail.es");
        verify(userRepository, never()).saveAll(any());
    }

    @Test
    void shouldRejectEmailDuplicatedWithinTheSameFile() {
        UserInputDto input = input("laura.fernandez@gmail.es");
        when(jsonFileReader.read(FILE, UserInputDto.class)).thenReturn(List.of(input, input));
        when(userRepository.existsByEmail(input.email())).thenReturn(false);
        when(userMapper.toEntity(input)).thenReturn(UserEntity.builder().email(input.email()).build());

        assertThatThrownBy(() -> userService.importFromFile(FILE))
                .isInstanceOf(AlreadyExistsException.class);
    }
}
