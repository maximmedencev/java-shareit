package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    private long index = 0;

    private long getNewIndex() {
        return ++index;
    }

    @Override
    public User create(User user) {
        long index = getNewIndex();
        user.setId(index);
        users.put(index, user);
        return users.get(index);
    }

    @Override
    public User read(long userId) {
        return users.get(userId);
    }

    @Override
    public Collection<User> readAll() {
        return users.values();
    }

    @Override
    public User update(User updUser) {
        if (updUser.getName() != null) {
            users.get(updUser.getId()).setName(updUser.getName());
        }

        if (updUser.getEmail() != null) {
            users.get(updUser.getId()).setEmail(updUser.getEmail());
        }

        return users.get(updUser.getId());
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }

    @Override
    public boolean isEmailExistInRepository(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public boolean isUserExist(long id) {
        return users.containsKey(id);
    }

}
