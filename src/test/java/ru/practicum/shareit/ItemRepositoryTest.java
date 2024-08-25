package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemRepositoryImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ItemRepositoryTest {

    private ItemRepository itemRepository;

    static final Long TEST_USER1_ID = 1L;
    static final Long TEST_ITEM1_ID = 1L;
    static final Long TEST_ITEM2_ID = 2L;
    static final Long TEST_ITEM3_ID = 3L;
    static final String UPDATED_ITEM_NAME = "updItem1Name";
    static final int SEARCH_RESULT_SIZE = 2;

    @BeforeEach
    void setUp() {
        itemRepository = new ItemRepositoryImpl();
    }

    @Test
    void contextLoads() {

    }

    static Item getTestItem1() {
        return Item.builder()
                .id(TEST_ITEM1_ID)
                .name("item1Name")
                .description("item1Description")
                .available(true)
                .owner(User.builder()
                        .id(TEST_USER1_ID)
                        .email("user1@mail.com")
                        .name("user1Name")
                        .build())
                .build();
    }

    static Item getTestItem2() {
        return Item.builder()
                .id(TEST_ITEM2_ID)
                .name("item2Name")
                .description("item2Description")
                .available(true)
                .owner(User.builder()
                        .id(TEST_USER1_ID)
                        .email("user1@mail.com")
                        .name("user1Name")
                        .build())
                .build();
    }

    static Item getTestItem3() {
        return Item.builder()
                .id(TEST_ITEM3_ID)
                .name("Name3")
                .description("Description3")
                .available(true)
                .owner(User.builder()
                        .id(TEST_USER1_ID)
                        .email("user1@mail.com")
                        .name("user1Name")
                        .build())
                .build();
    }

    @Test
    @DisplayName("Должен возвратить id владельца, если указать id вещи")
    public void shouldReturnOwnerIdWhenSpecifiedItemId() {
        itemRepository.create(getTestItem1());
        assertThat(itemRepository.getOwnerId(1L)).isEqualTo(TEST_USER1_ID);
    }

    @Test
    @DisplayName("Должен записывать вещь в репозиторий")
    public void shouldCreateUser() {
        assertThat(itemRepository.create(getTestItem1())).isEqualTo(Item.builder()
                .id(TEST_ITEM1_ID)
                .name("item1Name")
                .description("item1Description")
                .available(true)
                .owner(User.builder()
                        .id(TEST_USER1_ID)
                        .email("user1@mail.com")
                        .name("user1Name")
                        .build())
                .build());
    }

    @Test
    @DisplayName("Должен возвращать вещь по id")
    public void shouldReadUser() {
        itemRepository.create(getTestItem1());
        ;
        assertThat(itemRepository.read(TEST_ITEM1_ID)).isEqualTo(getTestItem1());
    }

    @Test
    @DisplayName("Должен обновлять вещь по id")
    public void shouldUpdateUser() {
        itemRepository.create(getTestItem1());
        assertThat(itemRepository.update(Item.builder()
                .id(TEST_ITEM1_ID)
                .name("updItem1Name")
                .description("updItem1Description")
                .available(true)
                .owner(User.builder()
                        .id(TEST_USER1_ID)
                        .email("user1@mail.com")
                        .name("user1Name")
                        .build())
                .build()
        ).getName()).isEqualTo("updItem1Name");

        assertThat(itemRepository.read(TEST_ITEM1_ID)
                .getName()).isEqualTo(UPDATED_ITEM_NAME);
    }

    @Test
    @DisplayName("Должен удалять вещь по id")
    public void shouldDeleteItem() {
        itemRepository.create(getTestItem1());
        itemRepository.delete(TEST_ITEM1_ID);
        assertThat(itemRepository.read(TEST_ITEM1_ID)).isNull();
    }

    @Test
    @DisplayName("Должен возвращать результат поиска по полям name и description")
    public void shouldReturnSearchResult() {
        itemRepository.create(getTestItem1());
        itemRepository.create(getTestItem2());
        itemRepository.create(getTestItem3());

        Set<Item> searchResult = itemRepository.search("item");

        assertThat(searchResult.size()).isEqualTo(SEARCH_RESULT_SIZE);
    }
}
