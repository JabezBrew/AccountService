package services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import account.errors.GenericException;
import account.services.EventLogService;
import account.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import account.entity.Role;
import account.entity.User;
import account.repo.RoleRepository;
import account.repo.UserRepository;
import account.errors.*;

public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private RoleRepository roleRepo;

    @Mock
    private Authentication auth;

    @Mock
    private EventLogService eventLogService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        Optional<User> optionalUser = Optional.of(user);
        Mockito.when(userRepo.findByEmail(email)).thenReturn(optionalUser);
        assertEquals(user, userDetailsService.loadUserByUsername(email));
    }

    @Test
    void testGetByEmail() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        Optional<User> optionalUser = Optional.of(user);
        Mockito.when(userRepo.findByEmail(email)).thenReturn(optionalUser);
        assertEquals(user, userDetailsService.getByEmail(email));
    }

    @Test
    void testGetCurrentUser() {
        User user = new User();
        Mockito.when(auth.getPrincipal()).thenReturn(user);
        assertEquals(user, userDetailsService.getCurrentUser());
    }

    @Test
    void testPasswordBreached() {
        String hashedPassword = "$2a$10$Jq6J8dJ5d5z6z9zZz6z6zOJ5d5z6z9zZz6z9zZz6z9zZz6z9zZz6z";
        userDetailsService.passwordBreached(hashedPassword);
        // No exception should be thrown
    }

    @Test
    void testDefineRole() {
        String roleName = "ROLE_USER";
        Role role = new Role();
        role.setRole(roleName);
        Optional<Role> optionalRole = Optional.of(role);
        Mockito.when(roleRepo.findByRole(roleName)).thenReturn(optionalRole);
        assertEquals(role, userDetailsService.defineRole(roleName));
    }

    @Test
    void testSaveUser() {
        String email = "test@example.com";
        String password = "password123";
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        Optional<User> optionalUser = Optional.empty();
        Mockito.when(userRepo.findByEmail(email)).thenReturn(optionalUser);
        Mockito.when(userRepo.count()).thenReturn(0L);
        Mockito.when(roleRepo.findByRole("ROLE_ADMINISTRATOR")).thenReturn(Optional.empty());
        Mockito.when(roleRepo.findByRole("ROLE_USER")).thenReturn(Optional.empty());
        userDetailsService.saveUser(user);
        Mockito.verify(userRepo).save(user);
    }

    @Test
    void testChangePass() {
        String newPassword = "newpassword123";
        User user = new User();
        user.setPassword("$2a$10$Jq6J8dJ5d5z6z9zZz6z6zOJ5d5z6z9zZz6z9zZz6z9zZz6z9zZz6z");
        Mockito.when(auth.getPrincipal()).thenReturn(user);
        userDetailsService.changepass(newPassword);
        Mockito.verify(userRepo).save(user);
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setEmail("test1@example.com");
        User user2 = new User();
        user2.setEmail("test2@example.com");
        Mockito.when(userRepo.findAll()).thenReturn(java.util.List.of(user1, user2));
        assertEquals(2, userDetailsService.getAllUsers().size());
    }

    @Test
    void testDeleteUser() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setRoles(java.util.Set.of(new Role("ROLE_USER")));
        Optional<User> optionalUser = Optional.of(user);
        Mockito.when(userRepo.findByEmail(email)).thenReturn(optionalUser);
        userDetailsService.deleteUser(email);
        Mockito.verify(userRepo).delete(user);
    }

    @Test
    void testDeleteUserWithAdminRole() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setRoles(java.util.Set.of(new Role("ROLE_ADMINISTRATOR")));
        Optional<User> optionalUser = Optional.of(user);
        Mockito.when(userRepo.findByEmail(email)).thenReturn(optionalUser);
        assertThrows(GenericException.class, () -> userDetailsService.deleteUser(email));
    }

    @Test
    void testDeleteNonexistentUser() {
        String email = "test@example.com";
        Optional<User> optionalUser = Optional.empty();
        Mockito.when(userRepo.findByEmail(email)).thenReturn(optionalUser);
        assertThrows(NotFoundException.class, () -> userDetailsService.deleteUser(email));
    }

    @Test
    void testChangeRoleGrant() {
        String userEmail = "test@example.com";
        String roleName = "user";
        String operation = "grant";
        Map<String, String> roleChange = new HashMap<>();
        roleChange.put("user", userEmail);
        roleChange.put("role", roleName);
        roleChange.put("operation", operation);
        User user = new User();
        user.setEmail(userEmail);
        Mockito.when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(user));
        Mockito.when(roleRepo.findByRole("ROLE_"+roleName.toUpperCase())).thenReturn(Optional.of(new Role()));
        userDetailsService.changeRole(roleChange);
        Mockito.verify(userRepo).save(user);
    }

    @Test
    void testChangeRoleRemove() {
        String userEmail = "test@example.com";
        String roleName = "user";
        String operation = "remove";
        Map<String, String> roleChange = new HashMap<>();
        roleChange.put("user", userEmail);
        roleChange.put("role", roleName);
        roleChange.put("operation", operation);
        User user = new User();
        user.setEmail(userEmail);
        user.setRoles(java.util.Set.of(new Role(roleName)));
        Mockito.when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(user));
        Mockito.when(roleRepo.findByRole("ROLE_"+roleName.toUpperCase())).thenReturn(Optional.of(new Role()));
        userDetailsService.changeRole(roleChange);
        Mockito.verify(userRepo).save(user);
    }

    @Test
    void testChangeRoleUserWithAdminRole() {
        String userEmail = "test@example.com";
        String roleName = "user";
        String operation = "grant";
        Map<String, String> roleChange = new HashMap<>();
        roleChange.put("user", userEmail);
        roleChange.put("role", roleName);
        roleChange.put("operation", operation);
        User user = new User();
        user.setEmail(userEmail);
        user.setRoles(java.util.Set.of(new Role("ROLE_ADMINISTRATOR")));
        Mockito.when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(user));
        assertThrows(GenericException.class, () -> userDetailsService.changeRole(roleChange));
    }

    @Test
    void testChangeRoleUserWithBusinessRole() {
        String userEmail = "test@example.com";
        String roleName = "user";
        String operation = "grant";
        Map<String, String> roleChange = new HashMap<>();
        roleChange.put("user", userEmail);
        roleChange.put("role", roleName);
        roleChange.put("operation", operation);
        User user = new User();
        user.setEmail(userEmail);
        user.setRoles(java.util.Set.of(new Role("ROLE_USER")));
        Mockito.when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(user));
        Mockito.when(roleRepo.findByRole("ROLE_"+roleName.toUpperCase())).thenReturn(Optional.of(new Role("ROLE_ADMINISTRATOR")));
        assertThrows(GenericException.class, () -> userDetailsService.changeRole(roleChange));
    }

    @Test
    void testChangeRoleNonexistentUser() {
        String userEmail = "test@example.com";
        String roleName = "user";
        String operation = "grant";
        Map<String, String> roleChange = new HashMap<>();
        roleChange.put("user", userEmail);
        roleChange.put("role", roleName);
        roleChange.put("operation", operation);
        Optional<User> optionalUser = Optional.empty();
        Mockito.when(userRepo.findByEmail(userEmail)).thenReturn(optionalUser);
        assertThrows(NotFoundException.class, () -> userDetailsService.changeRole(roleChange));
    }

    @Test
    void testChangeRoleNonexistentRole() {
        String userEmail = "test@example.com";
        String roleName = "user";
        String operation = "grant";
        Map<String, String> roleChange = new HashMap<>();
        roleChange.put("user", userEmail);
        roleChange.put("role", roleName);
        roleChange.put("operation", operation);
        User user = new User();
        user.setEmail(userEmail);
        Mockito.when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(user));
        Optional<Role> optionalRole = Optional.empty();
        Mockito.when(roleRepo.findByRole("ROLE_"+roleName.toUpperCase())).thenReturn(optionalRole);
        assertThrows(NotFoundException.class, () -> userDetailsService.changeRole(roleChange));
    }

}