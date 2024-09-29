package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingParamDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    private final BookingService bookingService;
    private final EntityManager entityManager;

    @DisplayName("Должен возвращать все бронирования пользователя")
    @Test
    void shouldReturnAllByOwnerId() {
        // given
        User booker = new User();
        booker.setName("Ivan");
        booker.setEmail("ivan@mail.ru");
        entityManager.persist(booker);

        User owner = new User();
        owner.setName("Petr");
        owner.setEmail("petr@mail.ru");
        entityManager.persist(owner);

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
        booking2.setStart(LocalDateTime.of(2023, 12, 14, 14, 14, 14));
        booking2.setEnd(LocalDateTime.of(2023, 12, 15, 15, 15, 15));
        entityManager.persist(booking2);

        //when
        List<BookingDto> sourceBookingDtos = bookingService.readAllByOwnerId(owner.getId(), BookingsState.ALL);

        //then
        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.booker.id = :id order by b.end desc",
                        Booking.class);
        List<Booking> targetBookings = query.setParameter("id", booker.getId()).getResultList();

        assertThat(targetBookings.get(1), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(sourceBookingDtos.get(0).getStart())),
                hasProperty("end", equalTo(sourceBookingDtos.get(0).getEnd())),
                hasProperty("status", equalTo(sourceBookingDtos.get(0).getStatus()))
        ));

        assertThat(targetBookings.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(sourceBookingDtos.get(1).getStart())),
                hasProperty("end", equalTo(sourceBookingDtos.get(1).getEnd())),
                hasProperty("status", equalTo(sourceBookingDtos.get(1).getStatus()))
        ));
    }

    @DisplayName("Должен создавать бронирование")
    @Test
    void shouldCreateBooking() {
        // given
        User booker = new User();
        booker.setName("Ivan");
        booker.setEmail("ivan@mail.ru");
        entityManager.persist(booker);

        User owner = new User();
        owner.setName("Petr");
        owner.setEmail("petr@mail.ru");
        entityManager.persist(owner);

        Item item1 = new Item();
        item1.setName("Item1 name");
        item1.setDescription("Item1 description");
        item1.setOwner(owner);
        item1.setAvailable(true);
        entityManager.persist(item1);

        entityManager.flush();

        BookingParamDto bookingParamDto = new BookingParamDto();
        bookingParamDto.setBooker(booker);
        bookingParamDto.setStart(LocalDateTime.of(2024, 12, 12, 12, 12, 12));
        bookingParamDto.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        bookingParamDto.setItem(item1);
        bookingParamDto.setItemId(item1.getId());

        //when
        BookingDto sourceBookingDto = bookingService.save(booker.getId(), bookingParamDto);

        //then
        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.id = :id",
                        Booking.class);
        Booking targetBooking = query.setParameter("id", sourceBookingDto.getId()).getSingleResult();

        assertThat(targetBooking.getStart(), equalTo(sourceBookingDto.getStart()));
        assertThat(targetBooking.getEnd(), equalTo(sourceBookingDto.getEnd()));
        assertThat(targetBooking.getItem().getName(), equalTo(sourceBookingDto.getItem().getName()));
    }

    @DisplayName("Должен менять статус бронирования на APPROVED")
    @Test
    void shouldSetApprovedInStatus() {
        // given
        User booker = new User();
        booker.setName("Ivan");
        booker.setEmail("ivan@mail.ru");
        entityManager.persist(booker);

        User owner = new User();
        owner.setName("Petr");
        owner.setEmail("petr@mail.ru");
        entityManager.persist(owner);

        Item item1 = new Item();
        item1.setName("Item1 name");
        item1.setDescription("Item1 description");
        item1.setOwner(owner);
        item1.setAvailable(true);
        entityManager.persist(item1);


        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.of(2024, 12, 12, 12, 12, 12));
        booking.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        booking.setItem(item1);
        booking.setStatus(BookingStatus.WAITING);

        entityManager.persist(booking);

        entityManager.flush();

        //when
        BookingDto sourceBookingDto = bookingService.setApproved(booker.getId(), owner.getId(), true);

        //then
        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.id = :id",
                        Booking.class);
        Booking targetBooking = query.setParameter("id", sourceBookingDto.getId()).getSingleResult();

        assertThat(targetBooking.getStatus(), equalTo(sourceBookingDto.getStatus()));
    }
}
