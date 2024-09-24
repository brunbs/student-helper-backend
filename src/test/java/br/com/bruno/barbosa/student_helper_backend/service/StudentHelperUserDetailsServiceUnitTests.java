package br.com.bruno.barbosa.student_helper_backend.service;

import br.com.bruno.barbosa.student_helper_backend.domain.entity.Role;
import br.com.bruno.barbosa.student_helper_backend.domain.entity.User;
import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.RoleEnum;
import br.com.bruno.barbosa.student_helper_backend.repository.RoleRepository;
import br.com.bruno.barbosa.student_helper_backend.repository.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentHelperUserDetailsServiceUnitTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private StudentHelperUserDetailsService userDetailsService;

    private final ObjectId roleId = new ObjectId();

    @Test
    void testLoadUserByUsername_Success() {
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setPassword("password");
        mockUser.setEnabled(true);
        mockUser.setRoleId(roleId);

        Role mockRole = new Role();
        mockRole.setId(roleId);
        mockRole.setRoleName(RoleEnum.STUDENT.name());

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(mockRole));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("unknownUser");
        });

        verify(userRepository, times(1)).findByUsername("unknownUser");
        verify(roleRepository, never()).findById(any(ObjectId.class));
    }

    @Test
    void testLoadUserByUsername_RoleNotFound() {
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setPassword("password");
        mockUser.setEnabled(true);
        mockUser.setRoleId(roleId);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userDetailsService.loadUserByUsername("testuser");
        });

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(roleRepository, times(1)).findById(roleId);
    }

}
