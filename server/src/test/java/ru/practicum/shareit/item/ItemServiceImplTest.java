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
import ru.practicum.shareit.exception.NoAvailableFieldException;
import ru.practicum.shareit.exception.NoItemBookingsForThisUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentParamDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoParam;
import ru.practicum.shareit.item.model.Comment;
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

    @DisplayName("Должен создавать вещь")
    @Test
    void shouldCreateItem() {
        // given
        User owner = new User();
        owner.setName("Owner User");
        owner.setEmail("ivan@mail.ru");
        entityManager.persist(owner);
        entityManager.flush();

        ItemDtoParam itemDtoParam = ItemDtoParam.builder()
                .description("Item1 description")
                .name("Item1 name")
                .available(true)
                .build();

        // when
        ItemDto sourceItem = itemService.create(owner.getId(), itemDtoParam);

        // then
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item targetItem = query.setParameter("id", sourceItem.getId()).getSingleResult();

        assertThat(targetItem.getName(), equalTo(itemDtoParam.getName()));
        assertThat(targetItem.getDescription(), equalTo(itemDtoParam.getDescription()));

    }


    @DisplayName("Должен выюрасывать исключение, если не указано поле available при создании")
    @Test
    void shouldThrowNoAvailableFieldExceptionWhenNoAvailableFieldWhenCreateItem() {
        // given
        User owner = new User();
        owner.setName("Owner User");
        owner.setEmail("ivan@mail.ru");
        entityManager.persist(owner);
        entityManager.flush();

        ItemDtoParam itemDtoParam = ItemDtoParam.builder()
                .description("Item1 description")
                .name("Item1 name")
                .build();

        // when then
        assertThrows(NoAvailableFieldException.class, () -> {
            itemService.create(owner.getId(), itemDtoParam);
        }, "Отсутствие поля available должно приводить к исключению");

    }

    @DisplayName("Должен считывать данные вещи с указанным id")
    @Test
    void shouldReadItemWhenSpecifiedId() {
        // given
        User owner = new User();
        owner.setName("Owner User");
        owner.setEmail("ivan@mail.ru");
        entityManager.persist(owner);


        Item item1 = new Item();
        item1.setName("Item1 name");
        item1.setDescription("Item1 description");
        item1.setOwner(owner);
        item1.setAvailable(true);
        entityManager.persist(item1);

        entityManager.flush();

        // when
        ItemDto sourceItem = itemService.read(item1.getId());

        // then
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item targetItem = query.setParameter("id", item1.getId()).getSingleResult();

        assertThat(targetItem.getName(), equalTo(sourceItem.getName()));
        assertThat(targetItem.getDescription(), equalTo(sourceItem.getDescription()));

    }

    @DisplayName("Должен обновлять данные вещи")
    @Test
    void shouldUpdateItem() {
        // given
        User owner = new User();
        owner.setName("Owner User");
        owner.setEmail("ivan@mail.ru");
        entityManager.persist(owner);

        Item item = new Item();
        item.setName("Item1 name");
        item.setDescription("Item1 description");
        item.setOwner(owner);
        item.setAvailable(true);
        entityManager.persist(item);

        entityManager.flush();

        Item updatedItem = new Item();
        updatedItem.setName("Updated Item1 name");
        updatedItem.setDescription("Updated Item1 description");
        updatedItem.setAvailable(true);

        // when
        ItemDto sourceItem = itemService.update(item.getId(), owner.getId(), updatedItem);

        // then
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item targetItem = query.setParameter("id", sourceItem.getId()).getSingleResult();

        assertThat(targetItem.getName(), equalTo(updatedItem.getName()));
        assertThat(targetItem.getDescription(), equalTo(updatedItem.getDescription()));

    }

    @DisplayName("Должен удалять вещь")
    @Test
    void shouldDeleteItem() {
        // given
        User owner = new User();
        owner.setName("Owner User");
        owner.setEmail("ivan@mail.ru");
        entityManager.persist(owner);

        Item item = new Item();
        item.setName("Item1 name");
        item.setDescription("Item1 description");
        item.setOwner(owner);
        item.setAvailable(true);
        entityManager.persist(item);

        entityManager.flush();

        // when
        itemService.delete(owner.getId(), item.getId());

        // then
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :id", Item.class);
        query.setParameter("id", item.getId()).getResultList();

        assertThat(query.setParameter("id", item.getId())
                .getResultList()
                .isEmpty(), equalTo(true));
    }

    @DisplayName("Должен выбрасывать исключение, при удалении вещи с заданым неверно идентификатором пользователя")
    @Test
    void shouldThrowNotFoundExceptionWhenDeleteItemWithWrongSharerId() {
        // given
        User owner = new User();
        owner.setName("Owner User");
        owner.setEmail("ivan@mail.ru");
        entityManager.persist(owner);

        Item item = new Item();
        item.setName("Item1 name");
        item.setDescription("Item1 description");
        item.setOwner(owner);
        item.setAvailable(true);
        entityManager.persist(item);

        entityManager.flush();

        // when then
        assertThrows(NotFoundException.class, () -> {
            itemService.delete(999, item.getId());
        }, "Неверно указанный идентификатор пользователя должен приводить к исключению");

    }

    @DisplayName("Должен искать вещи")
    @Test
    void shouldSearchItems() {
        // given
        String text = "Item1 name";

        User owner = new User();
        owner.setName("Owner User");
        owner.setEmail("ivan@mail.ru");
        entityManager.persist(owner);

        Item item1 = new Item();
        item1.setName("Item1 name");
        item1.setDescription("Item1 description");
        item1.setOwner(owner);
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setName("Item2 name");
        item2.setDescription("Item2 description");
        item2.setOwner(owner);
        item2.setAvailable(true);

        Item item3 = new Item();
        item3.setName("Item3 name");
        item3.setDescription("Item3 description");
        item3.setOwner(owner);
        item3.setAvailable(true);

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);

        entityManager.flush();

        // when
        List<ItemDto> sourceItemDtos = itemService.search(owner.getId(), text).stream().toList();

        // then
        TypedQuery<Item> query = entityManager.createQuery("select i from Item i where (upper(i.name) " +
                "like upper(:text) or upper(i.description) " +
                "like upper(:text)) and i.available=true", Item.class);
        List<Item> targetItems = query.setParameter("text", text).getResultList();

        assertThat(targetItems, hasSize(1));

        assertThat(targetItems.get(0), allOf(
                hasProperty("name", equalTo(sourceItemDtos.get(0).getName())),
                hasProperty("description", equalTo(sourceItemDtos.get(0).getDescription())),
                hasProperty("available", equalTo(sourceItemDtos.get(0).getAvailable()))
        ));

    }

    @DisplayName("Должен искать вещи")
    @Test
    void shouldReturnEmptyListWhenSpecifiedEmptySearchString() {
        // given
        String text = "";

        User owner = new User();
        owner.setName("Owner User");
        owner.setEmail("ivan@mail.ru");
        entityManager.persist(owner);

        Item item1 = new Item();
        item1.setName("Item1 name");
        item1.setDescription("Item1 description");
        item1.setOwner(owner);
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setName("Item2 name");
        item2.setDescription("Item2 description");
        item2.setOwner(owner);
        item2.setAvailable(true);

        Item item3 = new Item();
        item3.setName("Item3 name");
        item3.setDescription("Item3 description");
        item3.setOwner(owner);
        item3.setAvailable(true);

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);

        entityManager.flush();

        // when
        itemService.search(owner.getId(), text).stream().toList();

        // then
        TypedQuery<Item> query = entityManager.createQuery("select i from Item i where (upper(i.name) " +
                "like upper(:text) or upper(i.description) " +
                "like upper(:text)) and i.available=true", Item.class);
        List<Item> targetItems = query.setParameter("text", text).getResultList();

        assertThat(targetItems, hasSize(0));
    }

    @DisplayName("Должен создавать комментарии")
    @Test
    void shouldCreateComment() {
        // given
        User owner = new User();
        owner.setName("Owner User");
        owner.setEmail("ivan@mail.ru");
        entityManager.persist(owner);

        User booker = new User();
        booker.setName("Booker User");
        booker.setEmail("petr@mail.ru");
        entityManager.persist(booker);

        Item item = new Item();
        item.setName("Item1 name");
        item.setDescription("Item1 description");
        item.setOwner(owner);
        item.setAvailable(true);
        entityManager.persist(item);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.of(2023, 12, 12, 12, 12, 12));
        booking.setEnd(LocalDateTime.of(2023, 12, 13, 13, 13, 13));
        entityManager.persist(booking);

        entityManager.flush();

        CommentParamDto commentParamDto = CommentParamDto.builder()
                .text("Comment text")
                .build();

        //when
        CommentDto sourceCommentDto = itemService.createComment(booker.getId(), item.getId(), commentParamDto);

        // then
        TypedQuery<Comment> query = entityManager
                .createQuery("Select c from Comment c where c.id = :id", Comment.class);
        Comment targetComment = query.setParameter("id", sourceCommentDto.getId()).getSingleResult();

        assertThat(targetComment.getText(), equalTo(sourceCommentDto.getText()));
    }

    @DisplayName("Должен выбрасывать исключение если пользователь невалидный")
    @Test
    void shouldThrowNoItemBookingsForThisUserExceptionWhenUserNotValid() {
        // given
        User owner = new User();
        owner.setName("Owner User");
        owner.setEmail("ivan@mail.ru");
        entityManager.persist(owner);

        User booker = new User();
        booker.setName("Booker User");
        booker.setEmail("petr@mail.ru");
        entityManager.persist(booker);

        Item item = new Item();
        item.setName("Item1 name");
        item.setDescription("Item1 description");
        item.setOwner(owner);
        item.setAvailable(true);
        entityManager.persist(item);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.of(2023, 12, 12, 12, 12, 12));
        booking.setEnd(LocalDateTime.of(2023, 12, 13, 13, 13, 13));
        entityManager.persist(booking);

        entityManager.flush();

        CommentParamDto commentParamDto = CommentParamDto.builder()
                .text("Comment text")
                .build();

        //when then
        assertThrows(NoItemBookingsForThisUserException.class, () -> {
            itemService.createComment(999, item.getId(), commentParamDto);
        }, "Если не найдены бронирования для пользователя, то выбрасывается исключение");
    }
}