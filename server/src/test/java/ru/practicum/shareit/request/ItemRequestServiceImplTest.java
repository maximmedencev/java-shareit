package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
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

        User requestor = new User();
        requestor.setName("Petr");
        requestor.setEmail("petr@mail.ru");
        entityManager.persist(requestor);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setRequestor(requestor);
        itemRequest1.setCreated(LocalDateTime.of(2024, 12, 12, 12, 12, 12));
        itemRequest1.setDescription("request1 description");
        entityManager.persist(itemRequest1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setRequestor(requestor);
        itemRequest2.setCreated(LocalDateTime.of(2024, 12, 13, 13, 13, 13));
        itemRequest2.setDescription("request2 description");
        entityManager.persist(itemRequest2);

        Item item1 = new Item();
        item1.setName("Item1 name");
        item1.setDescription("Item1 description");
        item1.setOwner(user);
        item1.setAvailable(true);
        item1.setRequest(itemRequest1);
        entityManager.persist(item1);

        Item item2 = new Item();
        item2.setName("Item2 name");
        item2.setDescription("Item2 description");
        item2.setOwner(user);
        item2.setAvailable(true);
        item2.setRequest(itemRequest2);
        entityManager.persist(item2);

        entityManager.flush();
        //when
        List<ItemRequestDto> targetUserItemRequests = itemRequestService
                .getAllUserRequests(requestor.getId())
                .stream().toList();
        System.out.println(targetUserItemRequests);
        //then
        TypedQuery<ItemRequest> queryItemRequests = entityManager
                .createQuery("Select i from ItemRequest i where i.requestor.id = :id", ItemRequest.class);
        List<ItemRequest> targetItemRequests = queryItemRequests
                .setParameter("id", requestor.getId())
                .getResultList();

        TypedQuery<Item> queryItems = entityManager
                .createQuery("Select i from Item i where i.request.id = :id", Item.class);

        List<Item> targetItems1 = queryItems
                .setParameter("id", targetItemRequests.get(0).getId())
                .getResultList();

        List<Item> targetItems2 = queryItems
                .setParameter("id", targetItemRequests.get(1).getId())
                .getResultList();

        //then
        assertThat(targetItemRequests.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("description", equalTo(itemRequest1.getDescription())),
                hasProperty("requestor",
                        hasProperty("id", equalTo(itemRequest1.getRequestor().getId()))),
                hasProperty("created", equalTo(itemRequest1.getCreated()))
        ));

        assertThat(targetItemRequests.get(1), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("description", equalTo(itemRequest2.getDescription())),
                hasProperty("requestor",
                        hasProperty("id", equalTo(itemRequest2.getRequestor().getId()))),
                hasProperty("created", equalTo(itemRequest2.getCreated()))
        ));

        assertThat(targetItems1.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(item1.getName())),
                hasProperty("description", equalTo(item1.getDescription()))
        ));

        assertThat(targetItems2.get(0), allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(item2.getName())),
                hasProperty("description", equalTo(item2.getDescription()))
        ));

    }

}
