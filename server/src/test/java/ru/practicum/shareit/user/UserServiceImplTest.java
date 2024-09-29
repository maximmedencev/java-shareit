package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
        userService.update(user.getId(), userUpdate);

        // then
        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User userFromDB = query.setParameter("id", user.getId())
                .getSingleResult();

        assertThat(userFromDB.getId(), notNullValue());
        assertThat(userFromDB.getName(), equalTo(userUpdate.getName()));
        assertThat(userFromDB.getEmail(), equalTo(userUpdate.getEmail()));
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}