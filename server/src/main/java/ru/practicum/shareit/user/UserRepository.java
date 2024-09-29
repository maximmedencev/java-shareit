package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsById(long id);

    @Transactional
    @Modifying
    @Query("update User set name=?2 where id=?1")
    void updateName(Long id, String name);

    @Transactional
    @Modifying
    @Query("update User set email=?2 where id=?1")
    void updateEmail(Long id, String email);
}
