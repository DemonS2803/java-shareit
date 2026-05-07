package ru.practicum.shareit.item;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import ru.practicum.shareit.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "items")
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    private boolean available;
    @OneToMany
    @JoinColumn(name = "item_id")
    private List<Comment> comments;

    public Item(Long id, String name, String description, User owner, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.available = available;
    }
}
