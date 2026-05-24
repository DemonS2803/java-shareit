package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TODO Sprint add-item-requests.
 */
@Setter
@Getter
@Entity
@Table(name = "item_requests")
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    @ManyToOne
    private User user;
    @ManyToOne
    private Item item;
    private LocalDateTime created;
    @ManyToMany
    @JoinTable(
            name = "item_request_responses",
            joinColumns = @JoinColumn(name = "item_request_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> responses;

    public ItemRequest(String description, User user) {
        this.description = description;
        this.user = user;
    }

}
