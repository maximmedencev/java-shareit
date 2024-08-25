package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class UserRepositoryTest {

    static final long TEST_USER1_ID = 1L;
    static final long TEST_USER2_ID = 2L;
    static final long TEST_USER3_ID = 3L;
    static final String TEST_USER1_EMAIL = "user1@mail.com";
    static final String UPDATED_USER_EMAIL = "updUser1@mail.com";


    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl();
    }

    @Test
    void contextLoads() {

    }

    static User getTestUser1() {
        return User.builder()
                .id(TEST_USER1_ID)
                .email("user1@mail.com")
                .name("User1Name")
                .build();
    }

    static User getTestUser2() {
        return User.builder()
                .id(TEST_USER2_ID)
                .email("user2@mail.com")
                .name("User2Name")
                .build();
    }

    static User getTestUser3() {
        return User.builder()
                .id(TEST_USER3_ID)
                .email("user3@mail.com")
                .name("User3Name")
                .build();
    }

    @Test
    @DisplayName("Должен создавать пользователя")
    public void shouldCreateUser() {
        assertThat(userRepository.create(getTestUser1()))
                .isEqualTo(
                        User.builder()
                                .id(TEST_USER1_ID)
                                .email("user1@mail.com")
                                .name("User1Name")
                                .build()
                );
    }

    @Test
    @DisplayName("Должен получать данные пользователя пользователя")
    public void shouldReadUser() {
        userRepository.create(getTestUser1());
        assertThat(userRepository.read(TEST_USER1_ID))
                .isEqualTo(
                        User.builder()
                                .id(1L)
                                .email("user1@mail.com")
                                .name("User1Name")
                                .build()
                );
    }

    @Test
    @DisplayName("Должен обновлять данные пользователя")
    public void shouldUpdateUser() {
        userRepository.create(getTestUser1());
        assertThat(userRepository.update(User.builder()
                .id(1L)
                .email(UPDATED_USER_EMAIL)
                .name("updUser1Name")
                .build()
        ).getName()).isEqualTo("updUser1Name");

        assertThat(userRepository.read(TEST_USER1_ID)
                .getEmail()).isEqualTo(UPDATED_USER_EMAIL);

    }

    @Test
    @DisplayName("Должен удалять пользователя")
    public void shouldDeleteUser() {
        userRepository.create(getTestUser1());
        userRepository.delete(TEST_USER1_ID);
        assertThat(userRepository.read(TEST_USER1_ID)).isNull();
    }

    @Test
    @DisplayName("Должен получать данные всех пользователей")
    public void shouldReadAllUsers() {
        userRepository.create(getTestUser1());
        userRepository.create(getTestUser2());
        userRepository.create(getTestUser3());

        List<User> allUsers = new ArrayList<>(userRepository.readAll());
        assertThat(allUsers.get(0)).isEqualTo(getTestUser1());
        assertThat(allUsers.get(1)).isEqualTo(getTestUser2());
        assertThat(allUsers.get(2)).isEqualTo(getTestUser3());

    }

    @Test
    @DisplayName("Должен возвращать истину, если имэйл есть в репозитории")
    public void shouldReturnTrueWhenEmailExistInRepository() {
        userRepository.create(getTestUser1());

        assertThat(userRepository.isEmailExistInRepository(TEST_USER1_EMAIL))
                .isEqualTo(true);

    }

    @Test
    @DisplayName("Должен возвращать истину, если пользователь с указанным id есть в репо")
    public void shouldReturnTrueWhenUserExistInRepository() {
        userRepository.create(getTestUser1());

        assertThat(userRepository.isUserExist(TEST_USER1_ID))
                .isEqualTo(true);
    }


}
