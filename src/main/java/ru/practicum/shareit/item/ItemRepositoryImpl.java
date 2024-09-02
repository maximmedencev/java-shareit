package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private HashMap<Long, Item> items = new HashMap<>();
    private long index = 0;

    private long getNewIndex() {
        return ++index;
    }

    @Override
    public Long getOwnerId(Long itemId) {
        return items.get(itemId).getOwner().getId();
    }

    @Override
    public Item create(Item item) {
        long index = getNewIndex();
        item.setId(index);
        items.put(index, item);
        return items.get(index);
    }

    @Override
    public Item read(long itemId) {
        return items.get(itemId);
    }

    @Override
    public Collection<Item> readAll(long sharerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null)
                .filter(item -> item.getOwner().getId() == sharerId)
                .toList();
    }

    @Override
    public Item update(Item item) {
        Item updatedItem = items.get(item.getId());

        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        if (item.getName() != null) {
            updatedItem.setOwner(item.getOwner());
        }
        if (item.getRequest() != null) {
            updatedItem.setRequest(item.getRequest());
        }

        return updatedItem;
    }

    @Override
    public void delete(long itemId) {
        items.remove(itemId);
    }

    @Override
    public Set<Item> search(String text) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null)
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toSet());
    }
}
