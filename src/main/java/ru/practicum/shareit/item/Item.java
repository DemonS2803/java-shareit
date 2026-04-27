package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
public class Item {

    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private boolean available;

}
