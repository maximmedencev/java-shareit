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
import ru.practicum.shareit.exception.BookingApproveByWrongUserException;
import ru.practicum.shareit.exception.BookingUnavailableItemException;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongUserException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    private final BookingService bookingService;
    private final EntityManager entityManager;

    @DisplayName("Должен находить бронирование по id")
    @Test
    void shouldFindBookingWhenSpecifiedId() {
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
        entityManager.persist(booking);

        entityManager.flush();

        //when
        BookingDto sourceBookingDto = bookingService.read(owner.getId(), booking.getId());

        //then
        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.id = :id",
                        Booking.class);
        Booking targetBooking = query.setParameter("id", sourceBookingDto.getId()).getSingleResult();

        assertThat(targetBooking.getStart(), equalTo(sourceBookingDto.getStart()));
        assertThat(targetBooking.getEnd(), equalTo(sourceBookingDto.getEnd()));
        assertThat(targetBooking.getItem().getName(), equalTo(sourceBookingDto.getItem().getName()));
    }

    @DisplayName("Должен находить бронирование не найдено по id")
    @Test
    void shouldThrowNotFoundExceptionWhenSpecifiedWrongId() {
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
        entityManager.persist(booking);

        entityManager.flush();
        //when then
        assertThrows(NotFoundException.class, () -> {
            bookingService.read(owner.getId(), 999L);
        }, "Если не найдено бронирование по id, то выбрасывается исключение");
    }


    @DisplayName("Должен выбрасывать исключение при неправильномы id пользователя при бронировании")
    @Test
    void shouldThrowNotFoundExceptionWhenSpecifiedWrongUserId() {
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
        entityManager.persist(booking);

        entityManager.flush();

        //when then
        assertThrows(NotFoundException.class, () -> {
            bookingService.read(888L, booking.getId());
        }, "Если не найден пользователь по id, то выбрасывается исключение");
    }

    @DisplayName("Должен выбрасывать исключение, если неправильный id пользователя при бронировании")
    @Test
    void shouldThrowNotFoundExceptionWhenSpecifiedWrongUserIdWhenSave() {
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

        //when then
        assertThrows(NotFoundException.class, () -> {
            bookingService.save(999L, bookingParamDto);
        }, "Если не найден пользователь по id, то выбрасывается исключение");
    }

    @DisplayName("Должен выбрасывать исключение, если дата начала равно null при создании")
    @Test
    void shouldThrowInvalidDataExceptionWhenSpecifiedNullStartDateWhenSave() {
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
        bookingParamDto.setStart(null);
        bookingParamDto.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        bookingParamDto.setItem(item1);
        bookingParamDto.setItemId(item1.getId());

        //when then
        assertThrows(InvalidDataException.class, () -> {
            bookingService.save(booker.getId(), bookingParamDto);
        }, "Если дата начала null, то выбрасывается исключение");
    }

    @DisplayName("Должен выбрасывать исключение, если указан несуществуещий id вещи")
    @Test
    void shouldThrowNotFoundExceptionWhenSpecifiedWrongItemId() {
        // given
        User booker = new User();
        booker.setName("Ivan");
        booker.setEmail("ivan@mail.ru");
        entityManager.persist(booker);

        User owner = new User();
        owner.setName("Petr");
        owner.setEmail("petr@mail.ru");
        entityManager.persist(owner);

        entityManager.flush();

        Item item1 = new Item();
        item1.setId(333L);
        item1.setName("Item1 name");
        item1.setDescription("Item1 description");
        item1.setOwner(owner);
        item1.setAvailable(true);


        BookingParamDto bookingParamDto = new BookingParamDto();
        bookingParamDto.setBooker(booker);
        bookingParamDto.setStart(LocalDateTime.of(2024, 11, 13, 13, 13, 13));
        bookingParamDto.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        bookingParamDto.setItem(item1);
        bookingParamDto.setItemId(item1.getId());

        //when then
        assertThrows(NotFoundException.class, () -> {
            bookingService.save(booker.getId(), bookingParamDto);
        }, "Если указан несуществуещий id вещи, то выбрасывается исключение");
    }


    @DisplayName("Должен выбрасывать исключение, если дата начала равно null при создании")
    @Test
    void shouldThrowInvalidDataExceptionWhenSpecifiedNullEndDateWhenSave() {
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
        bookingParamDto.setStart(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        bookingParamDto.setEnd(null);
        bookingParamDto.setItem(item1);
        bookingParamDto.setItemId(item1.getId());

        //when then
        assertThrows(InvalidDataException.class, () -> {
            bookingService.save(booker.getId(), bookingParamDto);
        }, "Если дата конца null, то выбрасывается исключение");
    }

    @DisplayName("Должен выбрасывать исключение, если дата начала равна дате конца при создании")
    @Test
    void shouldThrowInvalidDataExceptionWhenSpecifiedStartDateEqualEndDateWhenSave() {
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
        bookingParamDto.setStart(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        bookingParamDto.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        bookingParamDto.setItem(item1);
        bookingParamDto.setItemId(item1.getId());

        //when then
        assertThrows(InvalidDataException.class, () -> {
            bookingService.save(booker.getId(), bookingParamDto);
        }, "Если дата начала равна дате конца, то выбрасывается исключение");
    }

    @DisplayName("Должен выбрасывать исключение, если дата конца в прошлом при создании")
    @Test
    void shouldThrowInvalidDataExceptionWhenSpecifiedEndDateInPastWhenSave() {
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
        bookingParamDto.setStart(LocalDateTime.of(2023, 11, 13, 13, 13, 13));
        bookingParamDto.setEnd(LocalDateTime.of(2023, 12, 13, 13, 13, 13));
        bookingParamDto.setItem(item1);
        bookingParamDto.setItemId(item1.getId());

        //when then
        assertThrows(InvalidDataException.class, () -> {
            bookingService.save(booker.getId(), bookingParamDto);
        }, "Если дата конца в прошлом, то выбрасывается исключение");
    }

    @DisplayName("Должен выбрасывать исключение, если вещь недоступна при создании")
    @Test
    void shouldThrowInvalidDataExceptionWhenItemUnavailableWhenSave() {
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
        item1.setAvailable(false);
        entityManager.persist(item1);

        entityManager.flush();

        BookingParamDto bookingParamDto = new BookingParamDto();
        bookingParamDto.setBooker(booker);
        bookingParamDto.setStart(LocalDateTime.of(2024, 11, 13, 13, 13, 13));
        bookingParamDto.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        bookingParamDto.setItem(item1);
        bookingParamDto.setItemId(item1.getId());

        //when then
        assertThrows(BookingUnavailableItemException.class, () -> {
            bookingService.save(booker.getId(), bookingParamDto);
        }, "Если вещь недоступна, то выбрасывается исключение");
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
        BookingDto sourceBookingDto = bookingService.setApproved(booking.getId(), owner.getId(), true);

        //then
        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.id = :id",
                        Booking.class);
        Booking targetBooking = query.setParameter("id", sourceBookingDto.getId()).getSingleResult();

        assertThat(targetBooking.getStatus(), equalTo(sourceBookingDto.getStatus()));
    }

    @DisplayName("Должен менять статус бронирования на REJECTED")
    @Test
    void shouldSetRejectedInStatus() {
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
        BookingDto sourceBookingDto = bookingService.setApproved(booking.getId(), owner.getId(), false);

        //then
        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.id = :id",
                        Booking.class);
        Booking targetBooking = query.setParameter("id", sourceBookingDto.getId()).getSingleResult();

        assertThat(targetBooking.getStatus(), equalTo(sourceBookingDto.getStatus()));
    }

    @DisplayName("Должен выбрасывать исключение, если id пользователя не найдено при одобрении бронирования")
    @Test
    void shouldThrowWrongUserExceptionWhenSetApprovedWhenWrongSharerId() {
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

        //when then
        assertThrows(WrongUserException.class, () -> {
            bookingService.setApproved(booking.getId(), 999L, true);
        }, "Если указан несуществующий id пользователя, то выбрасывается исключение");
    }

    @DisplayName("Должен выбрасывать исключение, если id бронирования не найдено при одобрении бронирования")
    @Test
    void shouldThrowNotFoundExceptionWhenSetApprovedWhenWrongBookingId() {
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

        //when then
        assertThrows(NotFoundException.class, () -> {
            bookingService.setApproved(999L, owner.getId(), true);
        }, "Если указан несуществующий id бронирования, то выбрасывается исключение");
    }

    @DisplayName("Должен выбрасывать исключение, если id пользователя неверно спри одобрении бронирования")
    @Test
    void shouldThrowBookingApproveByWrongUserExceptionWhenSetApprovedWhenWrongOwnerId() {
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

        //when then
        assertThrows(BookingApproveByWrongUserException.class, () -> {
            bookingService.setApproved(booking.getId(), booker.getId(), true);
        }, "Если указан неверный id пользователя, то выбрасывается исключение");
    }

    @DisplayName("Должен выбрасывать исключение, если id пользователя неверно спри одобрении бронирования")
    @Test
    void shouldNotFoundExceptionWhenSetApprovedWhenWrongItemId() {
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

        //when then
        assertThrows(BookingApproveByWrongUserException.class, () -> {
            bookingService.setApproved(booking.getId(), booker.getId(), true);
        }, "Если указан неверный id пользователя, то выбрасывается исключение");
    }

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
        TypedQuery<Long> queryOwnerItemIds = entityManager
                .createQuery("select i.id from Item i where i.owner.id = :ownerId", Long.class);

        List<Long> ownerItemIds = queryOwnerItemIds
                .setParameter("ownerId", owner.getId())
                .getResultList();


        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.item.id " +
                                "in(:ownerItemsIds) order by b.end desc",
                        Booking.class);


        List<Booking> targetBookings = query
                .setParameter("ownerItemsIds", ownerItemIds)
                .getResultList();

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

    @DisplayName("Должен возвращать все прошлые бронирования пользователя")
    @Test
    void shouldReadPastBookingsByOwnerId() {
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
        booking1.setStart(LocalDateTime.of(2024, 8, 12, 12, 12, 12));
        booking1.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        entityManager.persist(booking1);

        Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setBooker(booker);
        booking2.setStart(LocalDateTime.of(2023, 12, 14, 14, 14, 14));
        booking2.setEnd(LocalDateTime.of(2023, 12, 15, 15, 15, 15));
        entityManager.persist(booking2);

        entityManager.flush();

        //when
        List<BookingDto> sourceBookingDtos = bookingService.readAllByOwnerId(owner.getId(), BookingsState.PAST);

        //then
        TypedQuery<Long> queryOwnerItemsIds = entityManager
                .createQuery("select i.id from Item i where i.owner.id = :ownerId", Long.class);

        List<Long> ownerItemsIds = queryOwnerItemsIds
                .setParameter("ownerId", owner.getId())
                .getResultList();


        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.item.id " +
                                "in(:ownerItemsIds) and b.end <:now order by b.end desc",
                        Booking.class);


        List<Booking> targetBookings = query
                .setParameter("ownerItemsIds", ownerItemsIds)
                .setParameter("now", LocalDateTime.now())
                .getResultList();

        assertThat(targetBookings, hasSize(1));

        assertThat(targetBookings.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(sourceBookingDtos.get(0).getStart())),
                hasProperty("end", equalTo(sourceBookingDtos.get(0).getEnd())),
                hasProperty("status", equalTo(sourceBookingDtos.get(0).getStatus()))
        ));
    }

    @DisplayName("Должен возвращать текущие бронирования пользователя")
    @Test
    void shouldReadCurrentBookingsByOwnerId() {
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
        booking1.setStart(LocalDateTime.of(2024, 8, 12, 12, 12, 12));
        booking1.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        entityManager.persist(booking1);

        Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setBooker(booker);
        booking2.setStart(LocalDateTime.of(2023, 12, 14, 14, 14, 14));
        booking2.setEnd(LocalDateTime.of(2023, 12, 15, 15, 15, 15));
        entityManager.persist(booking2);

        entityManager.flush();

        //when
        List<BookingDto> sourceBookingDtos = bookingService.readAllByOwnerId(owner.getId(), BookingsState.CURRENT);

        //then
        TypedQuery<Long> queryOwnerItemsIds = entityManager
                .createQuery("select i.id from Item i where i.owner.id = :ownerId", Long.class);

        List<Long> ownerItemsIds = queryOwnerItemsIds
                .setParameter("ownerId", owner.getId())
                .getResultList();

        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.item.id " +
                                "in(:ownerItemsIds) and b.start <= :now and b.end >= :now order by b.end desc",
                        Booking.class);

        List<Booking> targetBookings = query
                .setParameter("ownerItemsIds", ownerItemsIds)
                .setParameter("now", LocalDateTime.now())
                .getResultList();

        assertThat(targetBookings, hasSize(1));

        assertThat(targetBookings.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(sourceBookingDtos.get(0).getStart())),
                hasProperty("end", equalTo(sourceBookingDtos.get(0).getEnd())),
                hasProperty("status", equalTo(sourceBookingDtos.get(0).getStatus()))
        ));
    }

    @DisplayName("Должен возвращать будущие бронирования пользователя")
    @Test
    void shouldReadFutureBookingsByOwnerId() {
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
        booking1.setStart(LocalDateTime.of(2024, 8, 12, 12, 12, 12));
        booking1.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        entityManager.persist(booking1);

        Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setBooker(booker);
        booking2.setStart(LocalDateTime.of(2025, 12, 14, 14, 14, 14));
        booking2.setEnd(LocalDateTime.of(2025, 12, 15, 15, 15, 15));
        entityManager.persist(booking2);

        entityManager.flush();

        //when
        List<BookingDto> sourceBookingDtos = bookingService.readAllByOwnerId(owner.getId(), BookingsState.FUTURE);

        //then
        TypedQuery<Long> queryOwnerItemsIds = entityManager
                .createQuery("select i.id from Item i where i.owner.id = :ownerId", Long.class);

        List<Long> ownerItemsIds = queryOwnerItemsIds
                .setParameter("ownerId", owner.getId())
                .getResultList();

        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.item.id " +
                                "in(:ownerItemsIds) and b.start > :now order by b.end desc",
                        Booking.class);

        List<Booking> targetBookings = query
                .setParameter("ownerItemsIds", ownerItemsIds)
                .setParameter("now", LocalDateTime.now())
                .getResultList();

        assertThat(targetBookings, hasSize(1));

        assertThat(targetBookings.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(sourceBookingDtos.get(0).getStart())),
                hasProperty("end", equalTo(sourceBookingDtos.get(0).getEnd())),
                hasProperty("status", equalTo(sourceBookingDtos.get(0).getStatus()))
        ));
    }

    @DisplayName("Должен возвращать WAITING бронирования пользователя")
    @Test
    void shouldReadWaitingBookingsByOwnerId() {
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
        booking1.setStart(LocalDateTime.of(2024, 8, 12, 12, 12, 12));
        booking1.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        entityManager.persist(booking1);

        Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setBooker(booker);
        booking2.setStart(LocalDateTime.of(2025, 12, 14, 14, 14, 14));
        booking2.setEnd(LocalDateTime.of(2025, 12, 15, 15, 15, 15));
        entityManager.persist(booking2);

        entityManager.flush();

        //when
        List<BookingDto> sourceBookingDtos = bookingService.readAllByOwnerId(owner.getId(), BookingsState.WAITING);

        //then
        TypedQuery<Long> queryOwnerItemsIds = entityManager
                .createQuery("select i.id from Item i where i.owner.id = :ownerId", Long.class);

        List<Long> ownerItemsIds = queryOwnerItemsIds
                .setParameter("ownerId", owner.getId())
                .getResultList();

        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.item.id " +
                                "in(:ownerItemsIds) and b.status = :bookingStatus order by b.end desc",
                        Booking.class);

        List<Booking> targetBookings = query
                .setParameter("ownerItemsIds", ownerItemsIds)
                .setParameter("bookingStatus", BookingStatus.WAITING)
                .getResultList();

        assertThat(targetBookings, hasSize(1));

        assertThat(targetBookings.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(sourceBookingDtos.get(0).getStart())),
                hasProperty("end", equalTo(sourceBookingDtos.get(0).getEnd())),
                hasProperty("status", equalTo(sourceBookingDtos.get(0).getStatus()))
        ));
    }

    @DisplayName("Должен возвращать REJECTED бронирования пользователя")
    @Test
    void shouldReadRejectedBookingsByOwnerId() {
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
        booking1.setStart(LocalDateTime.of(2024, 8, 12, 12, 12, 12));
        booking1.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        entityManager.persist(booking1);

        Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setStatus(BookingStatus.REJECTED);
        booking2.setBooker(booker);
        booking2.setStart(LocalDateTime.of(2025, 12, 14, 14, 14, 14));
        booking2.setEnd(LocalDateTime.of(2025, 12, 15, 15, 15, 15));
        entityManager.persist(booking2);

        entityManager.flush();

        //when
        List<BookingDto> sourceBookingDtos = bookingService.readAllByOwnerId(owner.getId(), BookingsState.REJECTED);

        //then
        TypedQuery<Long> queryOwnerItemsIds = entityManager
                .createQuery("select i.id from Item i where i.owner.id = :ownerId", Long.class);

        List<Long> ownerItemsIds = queryOwnerItemsIds
                .setParameter("ownerId", owner.getId())
                .getResultList();

        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.item.id " +
                                "in(:ownerItemsIds) and b.status = :bookingStatus order by b.end desc",
                        Booking.class);

        List<Booking> targetBookings = query
                .setParameter("ownerItemsIds", ownerItemsIds)
                .setParameter("bookingStatus", BookingStatus.REJECTED)
                .getResultList();

        assertThat(targetBookings, hasSize(1));

        assertThat(targetBookings.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(sourceBookingDtos.get(0).getStart())),
                hasProperty("end", equalTo(sourceBookingDtos.get(0).getEnd())),
                hasProperty("status", equalTo(sourceBookingDtos.get(0).getStatus()))
        ));
    }

    @DisplayName("Должен выбрасывать исключение, если пользователь с указанным id  не найден")
    @Test
    void shouldThrowWrongUserExceptionWhenBookingsByOwnerIdWhenWrongUserId() {
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
        booking1.setStart(LocalDateTime.of(2024, 8, 12, 12, 12, 12));
        booking1.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        entityManager.persist(booking1);

        Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setStatus(BookingStatus.REJECTED);
        booking2.setBooker(booker);
        booking2.setStart(LocalDateTime.of(2025, 12, 14, 14, 14, 14));
        booking2.setEnd(LocalDateTime.of(2025, 12, 15, 15, 15, 15));
        entityManager.persist(booking2);

        entityManager.flush();

        //when then
        assertThrows(WrongUserException.class, () -> {
            bookingService.readAllByOwnerId(555L, BookingsState.REJECTED);
        }, "Если не найден пользователь с указанным id, то выбрасывается исключение");

    }

    @DisplayName("Должен возвращать все бронирования букера")
    @Test
    void shouldReturnAllByBookerId() {
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
        List<BookingDto> sourceBookingDtos = bookingService.readAllByBookerId(booker.getId(), BookingsState.ALL);

        //then

        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.booker.id = :bookerId order by b.end desc",
                        Booking.class);


        List<Booking> targetBookings = query
                .setParameter("bookerId", booker.getId())
                .getResultList();

        assertThat(targetBookings.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(sourceBookingDtos.get(0).getStart())),
                hasProperty("end", equalTo(sourceBookingDtos.get(0).getEnd())),
                hasProperty("status", equalTo(sourceBookingDtos.get(0).getStatus()))
        ));

        assertThat(targetBookings.get(1), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(sourceBookingDtos.get(1).getStart())),
                hasProperty("end", equalTo(sourceBookingDtos.get(1).getEnd())),
                hasProperty("status", equalTo(sourceBookingDtos.get(1).getStatus()))
        ));
    }

    @DisplayName("Должен возвращать все бронирования букера в прошлом")
    @Test
    void shouldReadPastBookingsByBookerId() {
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
        booking1.setStart(LocalDateTime.of(2024, 8, 12, 12, 12, 12));
        booking1.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        entityManager.persist(booking1);

        Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setBooker(booker);
        booking2.setStart(LocalDateTime.of(2023, 12, 14, 14, 14, 14));
        booking2.setEnd(LocalDateTime.of(2023, 12, 15, 15, 15, 15));
        entityManager.persist(booking2);

        entityManager.flush();

        //when
        List<BookingDto> sourceBookingDtos = bookingService.readAllByBookerId(booker.getId(), BookingsState.PAST);

        //then

        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.booker.id = :bookerId " +
                                "and b.end < :now order by b.end desc",
                        Booking.class);


        List<Booking> targetBookings = query
                .setParameter("now", LocalDateTime.now())
                .setParameter("bookerId", booker.getId())
                .getResultList();

        assertThat(targetBookings, hasSize(1));

        assertThat(targetBookings.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(sourceBookingDtos.get(0).getStart())),
                hasProperty("end", equalTo(sourceBookingDtos.get(0).getEnd())),
                hasProperty("status", equalTo(sourceBookingDtos.get(0).getStatus()))
        ));
    }

    @DisplayName("Должен возвращать текущие бронирования букера")
    @Test
    void shouldReadCurrentBookingsByBookerId() {
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
        booking1.setStart(LocalDateTime.of(2024, 8, 12, 12, 12, 12));
        booking1.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        entityManager.persist(booking1);

        Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setBooker(booker);
        booking2.setStart(LocalDateTime.of(2023, 12, 14, 14, 14, 14));
        booking2.setEnd(LocalDateTime.of(2023, 12, 15, 15, 15, 15));
        entityManager.persist(booking2);

        entityManager.flush();

        //when
        List<BookingDto> sourceBookingDtos = bookingService.readAllByBookerId(booker.getId(), BookingsState.CURRENT);

        //then
        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.booker.id =:bookerId " +
                                "and b.start<=:now and b.end>=:now order by b.end desc",
                        Booking.class);

        List<Booking> targetBookings = query
                .setParameter("bookerId", booker.getId())
                .setParameter("now", LocalDateTime.now())
                .getResultList();

        assertThat(targetBookings, hasSize(1));

        assertThat(targetBookings.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(sourceBookingDtos.get(0).getStart())),
                hasProperty("end", equalTo(sourceBookingDtos.get(0).getEnd())),
                hasProperty("status", equalTo(sourceBookingDtos.get(0).getStatus()))
        ));
    }

    @DisplayName("Должен возвращать будущие бронирования букера")
    @Test
    void shouldReadFutureBookingsByBookerId() {
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
        booking1.setStart(LocalDateTime.of(2024, 8, 12, 12, 12, 12));
        booking1.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        entityManager.persist(booking1);

        Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setBooker(booker);
        booking2.setStart(LocalDateTime.of(2025, 12, 14, 14, 14, 14));
        booking2.setEnd(LocalDateTime.of(2025, 12, 15, 15, 15, 15));
        entityManager.persist(booking2);

        entityManager.flush();

        //when
        List<BookingDto> sourceBookingDtos = bookingService.readAllByBookerId(booker.getId(), BookingsState.FUTURE);

        //then
        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.booker.id = :bookerId " +
                                "and b.start > :now order by b.end desc",
                        Booking.class);

        List<Booking> targetBookings = query
                .setParameter("bookerId", booker.getId())
                .setParameter("now", LocalDateTime.now())
                .getResultList();

        assertThat(targetBookings, hasSize(1));

        assertThat(targetBookings.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(sourceBookingDtos.get(0).getStart())),
                hasProperty("end", equalTo(sourceBookingDtos.get(0).getEnd())),
                hasProperty("status", equalTo(sourceBookingDtos.get(0).getStatus()))
        ));
    }

    @DisplayName("Должен возвращать WAITING бронирования букера")
    @Test
    void shouldReadWaitingBookingsByBookerId() {
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
        booking1.setStart(LocalDateTime.of(2024, 8, 12, 12, 12, 12));
        booking1.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        entityManager.persist(booking1);

        Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setBooker(booker);
        booking2.setStart(LocalDateTime.of(2025, 12, 14, 14, 14, 14));
        booking2.setEnd(LocalDateTime.of(2025, 12, 15, 15, 15, 15));
        entityManager.persist(booking2);

        entityManager.flush();

        //when
        List<BookingDto> sourceBookingDtos = bookingService.readAllByBookerId(booker.getId(), BookingsState.WAITING);

        //then

        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.booker.id = :bookerId " +
                                "and b.status = :bookingStatus order by b.end desc",
                        Booking.class);

        List<Booking> targetBookings = query
                .setParameter("bookerId", booker.getId())
                .setParameter("bookingStatus", BookingStatus.WAITING)
                .getResultList();

        assertThat(targetBookings, hasSize(1));

        assertThat(targetBookings.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(sourceBookingDtos.get(0).getStart())),
                hasProperty("end", equalTo(sourceBookingDtos.get(0).getEnd())),
                hasProperty("status", equalTo(sourceBookingDtos.get(0).getStatus()))
        ));
    }

    @DisplayName("Должен возвращать REJECTED бронирования букера")
    @Test
    void shouldReadRejectedBookingsByBookerId() {
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
        booking1.setStart(LocalDateTime.of(2024, 8, 12, 12, 12, 12));
        booking1.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        entityManager.persist(booking1);

        Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setStatus(BookingStatus.REJECTED);
        booking2.setBooker(booker);
        booking2.setStart(LocalDateTime.of(2025, 12, 14, 14, 14, 14));
        booking2.setEnd(LocalDateTime.of(2025, 12, 15, 15, 15, 15));
        entityManager.persist(booking2);

        entityManager.flush();

        //when
        List<BookingDto> sourceBookingDtos = bookingService.readAllByBookerId(booker.getId(), BookingsState.REJECTED);

        //then
        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.booker.id = :bookerId " +
                                "and b.status = :bookingStatus order by b.end desc",
                        Booking.class);

        List<Booking> targetBookings = query
                .setParameter("bookerId", booker.getId())
                .setParameter("bookingStatus", BookingStatus.REJECTED)
                .getResultList();

        assertThat(targetBookings, hasSize(1));

        assertThat(targetBookings.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(sourceBookingDtos.get(0).getStart())),
                hasProperty("end", equalTo(sourceBookingDtos.get(0).getEnd())),
                hasProperty("status", equalTo(sourceBookingDtos.get(0).getStatus()))
        ));
    }

    @DisplayName("Должен выбрасывать исключение, если пользователь с указанными id не найден")
    @Test
    void shouldThrowWrongUserExceptionWhenBookerIdIsWrong() {
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
        booking1.setStart(LocalDateTime.of(2024, 8, 12, 12, 12, 12));
        booking1.setEnd(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        entityManager.persist(booking1);

        Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setStatus(BookingStatus.REJECTED);
        booking2.setBooker(booker);
        booking2.setStart(LocalDateTime.of(2025, 12, 14, 14, 14, 14));
        booking2.setEnd(LocalDateTime.of(2025, 12, 15, 15, 15, 15));
        entityManager.persist(booking2);

        entityManager.flush();

        //when then
        assertThrows(WrongUserException.class, () -> {
            bookingService.readAllByBookerId(888L, BookingsState.REJECTED);
        }, "Если не найден пользователь по id, то выбрасывается исключение");
    }


}
