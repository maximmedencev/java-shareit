package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    private final EntityManager entityManager;
    private final ItemService itemService;

    @DisplayName("Должен считывать данные всех вещей пользователя")
    @Test
    void shouldReadAllUserItems() {
        // given
        User owner = new User();
        owner.setName("Owner User");
        owner.setEmail("ivan@mail.ru");
        entityManager.persist(owner);

        User booker = new User();
        booker.setName("Booker User");
        booker.setEmail("petr@mail.ru");
        entityManager.persist(booker);

        Item item1 = new Item();
        item1.setName("Item1 name");
        item1.setDescription("Item1 description");
        item1.setOwner(owner);
        item1.setAvailable(true);
        entityManager.persist(item1);

        Booking booking1 = new Booking();
        booking1.setItem(item1);
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setBooker(booker);
        booking1.setStart(LocalDateTime.of(2023, 12, 12, 12, 12, 12));
        booking1.setEnd(LocalDateTime.of(2023, 12, 13, 13, 13, 13));
        entityManager.persist(booking1);


        Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setBooker(booker);
        booking2.setStart(LocalDateTime.of(2025, 12, 12, 12, 12, 12));
        booking2.setEnd(LocalDateTime.of(2025, 12, 13, 13, 13, 13));
        entityManager.persist(booking2);

        entityManager.flush();

        // when
        List<ItemDto> sourceUserItems = itemService.readAll(owner.getId()).stream().toList();

        // then
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.owner.id = :id", Item.class);
        List<Item> targetUserItems = query.setParameter("id", owner.getId()).getResultList();

        TypedQuery<LocalDateTime> queryNextBooking = entityManager
                .createQuery("Select max(b.start) from Booking b where b.item.id =" +
                        ":itemId and b.start > CURRENT_TIMESTAMP", LocalDateTime.class);
        LocalDateTime targetNextBooking = queryNextBooking.setParameter("itemId", item1.getId())
                .getSingleResult();

        TypedQuery<LocalDateTime> queryLastBooking = entityManager
                .createQuery("Select max(b.end) from Booking b where b.item.id =" +
                        ":itemId and b.end < CURRENT_TIMESTAMP", LocalDateTime.class);
        LocalDateTime targetLastBooking = queryLastBooking.setParameter("itemId", item1.getId())
                .getSingleResult();

        assertThat(targetUserItems, hasSize(sourceUserItems.size()));

        assertThat(targetUserItems.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(sourceUserItems.get(0).getName())),
                hasProperty("description", equalTo(sourceUserItems.get(0).getDescription())),
                hasProperty("available", equalTo(sourceUserItems.get(0).getAvailable()))
        ));
        assertThat(targetLastBooking, equalTo(sourceUserItems.get(0).getLastBooking()));
        assertThat(targetNextBooking, equalTo(sourceUserItems.get(0).getNextBooking()));

    }

}