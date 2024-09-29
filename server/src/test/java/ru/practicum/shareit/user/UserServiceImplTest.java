package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final EntityManager entityManager;
    private final UserService userService;

    @Test
    @DisplayName("Должен обоновлять данные пользователя")
    void shouldUpdateUserData() {
        // given
        User user = makeUser("Ivan", "ivan@.mail.com");

        User userUpdate = makeUser("Petr", "petr@mail.ru");
        entityManager.persist(user);
        entityManager.flush();

        // when
        UserDto sourceUserDto = userService.update(user.getId(), userUpdate);

        // then
        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User userFromDB = query.setParameter("id", user.getId())
                .getSingleResult();

        assertThat(userFromDB.getId(), notNullValue());
        assertThat(userFromDB.getName(), equalTo(sourceUserDto.getName()));
        assertThat(userFromDB.getEmail(), equalTo(sourceUserDto.getEmail()));
    }

    @Test
    @DisplayName("Должен сохранять данные пользователя")
    void shouldSaveUserData() {
        // given
        User user = makeUser("Petr", "petr@.mail.com");
        // when
        UserDto sourceUserDto = userService.create(user);
        // then
        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User userFromDB = query.setParameter("id", user.getId())
                .getSingleResult();

        assertThat(userFromDB.getId(), notNullValue());
        assertThat(userFromDB.getName(), equalTo(sourceUserDto.getName()));
        assertThat(userFromDB.getEmail(), equalTo(sourceUserDto.getEmail()));
    }

    @Test
    @DisplayName("Должен получать данные пользователя")
    void shouldReadUserData() {
        // given
        User user = makeUser("Ivan", "ivan@.mail.com");

        entityManager.persist(user);
        entityManager.flush();

        // when
        UserDto sourceUserDto = userService.read(user.getId());

        // then
        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User userFromDB = query.setParameter("id", user.getId())
                .getSingleResult();

        assertThat(userFromDB.getId(), notNullValue());
        assertThat(userFromDB.getName(), equalTo(sourceUserDto.getName()));
        assertThat(userFromDB.getEmail(), equalTo(sourceUserDto.getEmail()));
    }

    @Test
    @DisplayName("Должен получать данные пользователя")
    void shouldReadAllUsersData() {
        // given

        User user1 = makeUser("Ivan", "ivan@.mail.com");
        User user2 = makeUser("Petr", "petr@.mail.com");
        User user3 = makeUser("Semen", "semen@.mail.com");

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        entityManager.flush();

        // when
        List<UserDto> userDtos = userService.readAll().stream().toList();

        // then
        TypedQuery<User> query = entityManager.createQuery("Select u from User u", User.class);
        List<User> usersFromDB = query.getResultList();

        assertThat(usersFromDB.get(0).getName(), equalTo(userDtos.get(0).getName()));
        assertThat(usersFromDB.get(1).getName(), equalTo(userDtos.get(1).getName()));
        assertThat(usersFromDB.get(2).getName(), equalTo(userDtos.get(2).getName()));
    }

    @Test
    @DisplayName("Должен обоновлять данные пользователя")
    void shouldDeleteUser() {
        // given
        User user = makeUser("Ivan", "ivan@.mail.com");

        entityManager.persist(user);
        entityManager.flush();

        // when
        userService.delete(user.getId());

        // then
        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        assertThat(query.setParameter("id", user.getId())
                .getResultList()
                .isEmpty(), equalTo(true));
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }


}