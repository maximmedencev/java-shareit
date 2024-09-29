package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.WrongUserException;
import ru.practicum.shareit.item.dto.ItemIdAndNameDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class ItemRequestServiceImplTest {
    private final EntityManager entityManager;
    private final ItemRequestService itemRequestService;

    @Test
    @DisplayName("Должен возвращать все запросы пользователя")
    void shouldReadAllUserItems() {
        // given
        User user = new User();
        user.setName("Ivan");
        user.setEmail("ivan@mail.ru");
        entityManager.persist(user);

        User owner = new User();
        owner.setName("Petr");
        owner.setEmail("petr@mail.ru");
        entityManager.persist(owner);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setRequestor(user);
        itemRequest1.setCreated(LocalDateTime.of(2024, 12, 12, 12, 12, 12));
        itemRequest1.setDescription("request1 description");
        entityManager.persist(itemRequest1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setRequestor(user);
        itemRequest2.setCreated(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        itemRequest2.setDescription("request2 description");
        entityManager.persist(itemRequest2);

        Item item1 = new Item();
        item1.setName("Item1 name");
        item1.setDescription("Item1 description");
        item1.setOwner(owner);
        item1.setAvailable(true);
        item1.setRequest(itemRequest1);
        entityManager.persist(item1);

        Item item2 = new Item();
        item2.setName("Item2 name");
        item2.setDescription("Item2 description");
        item2.setOwner(owner);
        item2.setAvailable(true);
        item2.setRequest(itemRequest2);
        entityManager.persist(item2);

        entityManager.flush();
        //when
        List<ItemRequestDto> sourceUserItemRequests = itemRequestService
                .getAllUserRequests(user.getId())
                .stream().toList();

        //then
        TypedQuery<ItemRequest> queryItemRequests = entityManager
                .createQuery("Select i from ItemRequest i where i.requestor.id = :id", ItemRequest.class);
        List<ItemRequest> targetItemRequests = queryItemRequests
                .setParameter("id", user.getId())
                .getResultList();

        TypedQuery<Item> queryItems = entityManager
                .createQuery("Select i from Item i where i.request.id = :id", Item.class);

        List<Item> targetItems1 = queryItems
                .setParameter("id", targetItemRequests.get(0).getId())
                .getResultList();

        List<Item> targetItems2 = queryItems
                .setParameter("id", targetItemRequests.get(1).getId())
                .getResultList();

        assertThat(targetItemRequests.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("description", equalTo(sourceUserItemRequests.get(0).getDescription())),
                hasProperty("requestor",
                        hasProperty("id", equalTo(sourceUserItemRequests.get(0).getRequestor().getId()))),
                hasProperty("created", equalTo(itemRequest1.getCreated()))
        ));

        assertThat(targetItemRequests.get(1), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("description", equalTo(sourceUserItemRequests.get(1).getDescription())),
                hasProperty("requestor",
                        hasProperty("id", equalTo(sourceUserItemRequests.get(1).getRequestor().getId()))),
                hasProperty("created", equalTo(targetItemRequests.get(1).getCreated()))
        ));


        List<ItemIdAndNameDto> sourceItems1 = sourceUserItemRequests.get(0).getItems().stream().toList();
        assertThat(targetItems1.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(sourceItems1.get(0).getName()))
        ));

        List<ItemIdAndNameDto> sourceItems2 = sourceUserItemRequests.get(1).getItems().stream().toList();
        assertThat(targetItems2.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(sourceItems2.get(0).getName()))
        ));

    }

    @Test
    @DisplayName("Должен возвращать запрос по id")
    void shouldReturnItemRequestWhenSpecifiedId() {
        // given
        User user = new User();
        user.setName("Ivan");
        user.setEmail("ivan@mail.ru");
        entityManager.persist(user);

        User requestor = new User();
        requestor.setName("Petr");
        requestor.setEmail("petr@mail.ru");
        entityManager.persist(requestor);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setRequestor(requestor);
        itemRequest1.setCreated(LocalDateTime.of(2024, 12, 12, 12, 12, 12));
        itemRequest1.setDescription("request1 description");
        entityManager.persist(itemRequest1);

        Item item1 = new Item();
        item1.setName("Item1 name");
        item1.setDescription("Item1 description");
        item1.setOwner(user);
        item1.setAvailable(true);
        item1.setRequest(itemRequest1);
        entityManager.persist(item1);

        entityManager.flush();

        //when
        ItemRequestDto sourceItemRequest = itemRequestService.getRequest(itemRequest1.getId());
        //then
        TypedQuery<ItemRequest> queryItemRequest = entityManager
                .createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        ItemRequest targetItemRequest = queryItemRequest
                .setParameter("id", itemRequest1.getId())
                .getSingleResult();

        assertThat(targetItemRequest.getDescription(), equalTo(sourceItemRequest.getDescription()));
    }

    @Test
    @DisplayName("Должен возвращать запросы других пользователей")
    void shouldReadAllRequestsOfUsers() {
        // given
        User user1 = new User();
        user1.setName("Ivan");
        user1.setEmail("ivan@mail.ru");
        entityManager.persist(user1);

        User requestor1 = new User();
        requestor1.setName("Petr");
        requestor1.setEmail("petr@mail.ru");
        entityManager.persist(requestor1);

        User requestor2 = new User();
        requestor2.setName("Semen");
        requestor2.setEmail("semen@mail.ru");
        entityManager.persist(requestor2);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setRequestor(requestor1);
        itemRequest1.setCreated(LocalDateTime.of(2024, 12, 12, 12, 12, 12));
        itemRequest1.setDescription("request1 description");
        entityManager.persist(itemRequest1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setRequestor(requestor2);
        itemRequest2.setCreated(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        itemRequest2.setDescription("request2 description");
        entityManager.persist(itemRequest2);

        ItemRequest itemRequest3 = new ItemRequest();
        itemRequest3.setRequestor(user1);
        itemRequest3.setCreated(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        itemRequest3.setDescription("request3 description");
        entityManager.persist(itemRequest3);

        Item item1 = new Item();
        item1.setName("Item1 name");
        item1.setDescription("Item1 description");
        item1.setOwner(user1);
        item1.setAvailable(true);
        item1.setRequest(itemRequest1);
        entityManager.persist(item1);

        Item item2 = new Item();
        item2.setName("Item2 name");
        item2.setDescription("Item2 description");
        item2.setOwner(user1);
        item2.setAvailable(true);
        item2.setRequest(itemRequest2);
        entityManager.persist(item2);

        Item item3 = new Item();
        item3.setName("Item3 name");
        item3.setDescription("Item3 description");
        item3.setOwner(user1);
        item3.setAvailable(true);
        item3.setRequest(itemRequest3);
        entityManager.persist(item3);

        entityManager.flush();
        //when
        List<ItemRequestDto> sourceItemRequestsOfUsers = itemRequestService
                .getAllRequestsOfUsers(user1.getId())
                .stream().toList();
        //then
        TypedQuery<ItemRequest> queryItemRequests = entityManager
                .createQuery("Select i from ItemRequest i where i.requestor.id <> :id", ItemRequest.class);
        List<ItemRequest> targetItemRequestsOfUsers = queryItemRequests
                .setParameter("id", user1.getId())
                .getResultList();

        //then
        assertThat(targetItemRequestsOfUsers.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("description", equalTo(sourceItemRequestsOfUsers.get(0).getDescription())),
                hasProperty("requestor",
                        hasProperty("id", equalTo(sourceItemRequestsOfUsers.get(0).getRequestor().getId()))),
                hasProperty("created", equalTo(sourceItemRequestsOfUsers.get(0).getCreated()))
        ));

        assertThat(targetItemRequestsOfUsers.get(1), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("description", equalTo(sourceItemRequestsOfUsers.get(1).getDescription())),
                hasProperty("requestor",
                        hasProperty("id", equalTo(sourceItemRequestsOfUsers.get(1).getRequestor().getId()))),
                hasProperty("created", equalTo(sourceItemRequestsOfUsers.get(1).getCreated()))
        ));

    }

    @Test
    @DisplayName("Должен возвращать запросы других пользователей")
    void shouldThrowWrongUserExceptionWhenReadAllRequestsOfUsersWhenSpecifiedWrongUserId() {
        // given
        User user1 = new User();
        user1.setName("Ivan");
        user1.setEmail("ivan@mail.ru");
        entityManager.persist(user1);

        User requestor1 = new User();
        requestor1.setName("Petr");
        requestor1.setEmail("petr@mail.ru");
        entityManager.persist(requestor1);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setRequestor(requestor1);
        itemRequest1.setCreated(LocalDateTime.of(2024, 12, 12, 12, 12, 12));
        itemRequest1.setDescription("request1 description");
        entityManager.persist(itemRequest1);


        Item item1 = new Item();
        item1.setName("Item1 name");
        item1.setDescription("Item1 description");
        item1.setOwner(user1);
        item1.setAvailable(true);
        item1.setRequest(itemRequest1);
        entityManager.persist(item1);

        entityManager.flush();

        //when then
        assertThrows(WrongUserException.class, () -> {
            itemRequestService.getAllRequestsOfUsers(111);
        }, "Если не найден пользователь, то выбрасывается исключение");
    }

}
