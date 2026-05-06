package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findItemById(Long id);

    List<Item> findItemsByOwnerId(Long ownerId);

    @Query(value = """
        select it from Item it
        where it.available = true and (
                lower(it.name) like concat('%', lower(:text), '%') or
                lower(it.description) like concat('%', lower(:text), '%')
            )
    """)
    List<Item> searchAvailableItems(@Param("text") String text);

}
