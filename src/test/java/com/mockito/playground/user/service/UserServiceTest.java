package com.mockito.playground.user.service;

import com.mockito.playground.user.exception.InvalidVipUsernameException;
import com.mockito.playground.user.exception.UserNotFoundException;
import com.mockito.playground.user.exception.UserProtectedException;
import com.mockito.playground.user.exception.UsernameAlreadyExistsException;
import com.mockito.playground.user.model.User;
import com.mockito.playground.user.repository.UserRepository;
import com.mockito.playground.user.validator.UniqueUsernameValidator;
import com.mockito.playground.user.validator.VipUsernameValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
    public void initTests() {
        userRepository = mock(UserRepository.class);
        uniqueUsernameValidator = mock(UniqueUsernameValidator.class);
        vipUsernameValidator = mock(VipUsernameValidator.class);
        userService = new UserService(
                userRepository,
                uniqueUsernameValidator,
                vipUsernameValidator
        );
    }

    ///////////// getAll()

    /**
     * GIVEN
     *  - Users Alice, Bob (VIP), Carl (VIP), David are registered
     *
     * WHEN
     *  - All users are queries
     *
     * THEN
     *  - All non VIP users are returned
     *  - Expected users = [Alice, David]
     */
    @Test
    public void testGetAll_SomeUsersAreVIPs() {
        // Given
        List<User> users = Arrays.asList(alice, bob, carl, david);
        List<User> expectedUsers = Arrays.asList(alice, carl);
        doReturn(users).when(userRepository).findAll();

        // When
        List<User> actualUsers = userService.getAll();

        // Then
        assertEquals(expectedUsers, actualUsers);
    }

    /**
     * GIVEN
     *  - Users Alice (VIP), Bob (VIP), Carl (VIP), David (VIP) are registered
     *
     * WHEN
     *  - All users are queries
     *
     * THEN
     *  - All non VIP users are returned
     *  - Expected users = []
     */
    @Test
    public void testGetAll_AllUsersAreVIPs() {
        // Given
        List<User> users = Arrays.asList(bob, david);
        List<User> expectedUsers = List.of();
        doReturn(users).when(userRepository).findAll();

        // When
        List<User> actualUsers = userService.getAll();

        // Then
        assertEquals(expectedUsers, actualUsers);
    }

    ///////////// getUser(long)

    /**
     * GIVEN
     *  - User Alice (0) is registered
     *
     * WHEN
     *  - user with id 0 is queried
     *
     * THEN
     *  - Alice is returned
     */
    @Test
    public void testGetUser_NonVipUser() {
        // Given
        User expectedUser = alice;
        long userId = expectedUser.getId();
        doReturn(Optional.of(expectedUser)).when(userRepository).findById(userId);

        // When
        User actualUser = userService.getUser(userId);

        // Then
        assertEquals(expectedUser, actualUser);
    }

    /**
     * GIVEN
     *  - Users Bob (1, VIP) is registered
     *
     * WHEN
     *  - user with id 1 is queried
     *
     * THEN
     *  - A 'UserProtectedException' is thrown
     */
    @Test
    public void testGetUser_VipUser() {
        // Given
        User user = david;
        long userId = user.getId();
        Exception expectedException = new UserProtectedException(userId);
        doReturn(Optional.of(user)).when(userRepository).findById(userId);

        try {
            // When
            userService.getUser(userId);
        } catch (Exception actualException) {
            // Then
            assertEquals(expectedException.getClass(), actualException.getClass());
            assertEquals(expectedException.getMessage(), actualException.getMessage());
        }
    }

    /**
     * GIVEN
     *  - There's no registered user with id 5 yet
     *
     * WHEN
     *  - user with id 5 is queried
     *
     * THEN
     *  - A 'UserNotFoundException' is thrown
     */
    @Test
    public void testGetUser_UserNotFound() {
        // Given
        long userId = 5;
        Exception expectedException = new UserNotFoundException(userId);
        doReturn(Optional.empty()).when(userRepository).findById(userId);

        try {
            // When
            userService.getUser(userId);
        } catch (Exception actualException) {
            // Then
            assertEquals(expectedException.getClass(), actualException.getClass());
            assertEquals(expectedException.getMessage(), actualException.getMessage());
        }
    }

    ///////////// register(string, boolean)

    /**
     * GIVEN
     *  - A user wants to register with username 'Erik' and wants to be a normal user (No VIP)
     *  - There's no registered user with username 'Erik'
     *
     * WHEN
     *  - User registers with his information
     *
     * THEN
     *  - A user with generated with the registered user information
     *  - User creation time is after registration time
     */
    @Test
    public void testRegister_validNormalUser() {
        // Given
        String username = "Erik";
        boolean isVip = false;
        long registeredUsersCount = 4;
        User expectedUser = new User(registeredUsersCount, username, isVip);
        doReturn(true).when(uniqueUsernameValidator).isValid(username);
        doReturn(expectedUser).when(userRepository).save(any(User.class));

        // When
        User actualUser = userService.register(username, isVip);

        // Then
        assertEquals(expectedUser, actualUser);
    }

    /**
     * GIVEN
     *  - A user wants to register with username 'VIP-Erik' and wants to be a VIP user
     *  - There's no registered user with username 'VIP-Erik'
     *
     * WHEN
     *  - User registers with his information
     *
     * THEN
     *  - A user with generated with the registered user information
     *  - User creation time is after registration time
     */
    @Test
    public void testRegister_validVipUser() {
        // Given
        String username = "Erik";
        boolean isVip = true;
        long registeredUsersCount = 4;
        User expectedUser = new User(registeredUsersCount, username, isVip);
        doReturn(true).when(uniqueUsernameValidator).isValid(username);
        doReturn(true).when(vipUsernameValidator).isValid(username);
        doReturn(expectedUser).when(userRepository).save(any(User.class));

        // When
        User actualUser = userService.register(username, isVip);

        // Then
        assertEquals(expectedUser, actualUser);
    }

    /**
     * GIVEN
     *  - A user wants to register with username 'Alice' and wants to be a <Whatever> user
     *  - There's already a registered user with username 'Alice'
     *
     * WHEN
     *  - User registers with his information
     *
     * THEN
     *  - A 'UsernameAlreadyExistsException' is thrown
     */
    @Test
    public void testRegister_UsernameAlreadyExists() {
        // Given
        String username = "Erik";
        boolean isVip = new Random().nextBoolean();
        Exception expectedException = new UsernameAlreadyExistsException(username);
        doReturn(false).when(uniqueUsernameValidator).isValid(username);

        try {
            // When
            userService.register(username, isVip);
        } catch (Exception actualException) {
            // Then
            assertEquals(expectedException.getClass(), actualException.getClass());
            assertEquals(expectedException.getMessage(), actualException.getMessage());
        }
    }

    /**
     * GIVEN
     *  - A user wants to register with username 'Erik' and wants to be a VIP user
     *  - There's no registered user with username 'Erik'
     *  - Valid VIP usernames MUST contain 'VIP'
     *
     * WHEN
     *  - User registers with his information
     *
     * THEN
     *  - A 'InvalidVipUsernameException' is thrown
     */
    @Test
    public void testRegister_InvalidVipUsername() {
        // Given
        String username = "Erik";
        boolean isVip = true;
        Exception expectedException = new InvalidVipUsernameException(username);
        doReturn(true).when(uniqueUsernameValidator).isValid(username);
        doReturn(false).when(vipUsernameValidator).isValid(username);

        try {
            // When
            userService.register(username, isVip);
        } catch (Exception actualException) {
            // Then
            assertEquals(expectedException.getClass(), actualException.getClass());
            assertEquals(expectedException.getMessage(), actualException.getMessage());
        }
    }
}
