package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
interface ItemRepository extends JpaRepository<Item, Long> {

    @EntityGraph(attributePaths = {"comments"})
    Optional<Item> findItemById(Long id);

    @EntityGraph(attributePaths = {"comments"})
    List<Item> findItemsByOwnerId(Long ownerId, Pageable page);

    @Query(value = """
        select it from Item it
        where it.available = true and (
                lower(it.name) like concat('%', lower(:text), '%') or
                lower(it.description) like concat('%', lower(:text), '%')
            )
    """)
    List<Item> searchAvailableItems(@Param("text") String text, Pageable page);

}
