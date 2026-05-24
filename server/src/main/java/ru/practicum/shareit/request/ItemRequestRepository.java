package ru.practicum.shareit.request;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @EntityGraph(attributePaths = {"responses", "responses.owner"})
    Optional<ItemRequest> findItemRequestById(Long id);

    @EntityGraph(attributePaths = {"responses", "responses.owner"})
    List<ItemRequest> findByUserIdIsNotOrderByCreatedDesc(Long userId, Pageable page);

    @EntityGraph(attributePaths = {"responses", "responses.owner"})
    List<ItemRequest> findByUserIdOrderByCreatedDesc(Long userId, Pageable page);

    @Modifying
    @Query(nativeQuery = true, value = """
    insert into item_request_responses (item_id, item_request_id)
        values (:itemId, :requestId)
    """)
    void addResponseForItemRequest(@Param("itemId") Long itemId, @Param("requestId") Long requestId);
}
