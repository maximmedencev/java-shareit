package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByOwnerId(long ownerId);

    @Query(value = "select i from Item i " +
            "where (upper(i.name) like upper(%:text%) or upper(i.description) like upper(%:text%)) " +
            "and i.available=true")
    Set<Item> search(String text);

}
