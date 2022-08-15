package com.mockito.playground.user.service;

import com.mockito.playground.user.exception.UserProtectedException;
import com.mockito.playground.user.model.User;
import com.mockito.playground.user.repository.UserRepository;
import com.mockito.playground.user.validator.UniqueUsernameValidator;
import com.mockito.playground.user.validator.VipUsernameValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class UserServiceTest {

    private UserRepository userRepository;
    private UniqueUsernameValidator uniqueUsernameValidator;
    private VipUsernameValidator vipUsernameValidator;
    private UserService userService;

    private final User alice = new User(0, "Alice", false);
    private final User bob = new User(1, "Bob", true);
    private final User carl = new User(2, "Carl", false);
    private final User david = new User(3, "David", true);

    @Before
    public void init() {
        userRepository = mock(UserRepository.class);
        uniqueUsernameValidator = mock(UniqueUsernameValidator.class);
        vipUsernameValidator = mock(VipUsernameValidator.class);
        userService = new UserService(
                userRepository,
                uniqueUsernameValidator,
                vipUsernameValidator
        );
    }

    @Test
    public void testGetAll_EmptyListOfUsers() {
        // Given
        List<User> expectedUsers = List.of();
        doReturn(expectedUsers).when(userRepository).findAll();

        // When
        List<User> actualUsers = userService.getAll();

        // Then
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void testGetAll_MixOfUsers() {
        // Given
        List<User> usersInRepo = List.of(alice, bob, carl, david);
        List<User> expectedUsers = List.of(alice, carl);
        doReturn(usersInRepo).when(userRepository).findAll();

        // When
        List<User> actualUsers = userService.getAll();

        // Then
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void testGetAll_DBErrors() {
        // Given
        Exception expectedException = new RuntimeException("Database connection failed");
        doThrow(expectedException).when(userRepository).findAll();

        try {
            // When
            userService.getAll();
        } catch (Exception actualException) {
            assertEquals(expectedException.getClass(), actualException.getClass());
            assertEquals(expectedException.getMessage(), actualException.getMessage());
        }
    }

    @Test
    public void testGetAll_AllNormalUsers() {
        // Given
        List<User> expectedUsers = List.of(alice, carl);
        doReturn(expectedUsers).when(userRepository).findAll();

        // When
        List<User> actualUsers = userService.getAll();

        // Then
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void testGetUser_NormalUserExists() {
        // Given
        User expectedUser = alice;
        long userId = alice.getId();

        doReturn(Optional.of(expectedUser)).when(userRepository).findById(anyLong());

        // When
        User actualUser = userService.getUser(userId);

        // Then
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void testGetUser_VipUser() {
        // Given
        long userId = bob.getId();
        Exception expectedException = new UserProtectedException(userId);

        doReturn(Optional.of(bob)).when(userRepository).findById(userId);

        // When
        try {
            userService.getUser(userId);
        } catch (Exception actualException) {
            assertEquals(expectedException.getClass(), actualException.getClass());
            assertEquals(expectedException.getMessage(), actualException.getMessage());
        }
    }

    @Test
    public void testGetUser_UserNotFound() {
        // TODO
    }

    @Test
    public void testRegister_NormalValidUser() {
        // TODO
    }

    @Test
    public void testRegister_ValidVipUser() {
        String username = "Eric";
        boolean isVip = true;
        long currentRegisteredUsersCount = 5;
        User expectedUser = new User(currentRegisteredUsersCount, username, isVip);

        doReturn(true).when(uniqueUsernameValidator).isValid(username);
        doReturn(true).when(vipUsernameValidator).isValid(username);
        doReturn(currentRegisteredUsersCount).when(userRepository).count();
        doReturn(expectedUser).when(userRepository).save(any(User.class));

        // When
        User actualUser = userService.register(username, isVip);

        // Then
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void testRegister_UsernameAlreadyExists() {
        // TODO
    }

    @Test
    public void testRegister_InvalidUsername() {
        // TODO
    }

    @Test
    public void testRegister_InvalidVipUser() {
        // TODO
    }
}
