package ru.practicum.shareit.common.testutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void cleanAllTables() {
        cleanComments();
        cleanBookings();
        cleanItemRequestResponses();
        cleanItemRequests();
        cleanItems();
        cleanUsers();
    }

    public void cleanUsers() {
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
    }

    public void cleanItems() {
        jdbcTemplate.execute("DELETE FROM items");
        jdbcTemplate.execute("ALTER TABLE items ALTER COLUMN id RESTART WITH 1");
    }

    public void cleanItemRequests() {
        jdbcTemplate.execute("DELETE FROM item_requests");
        jdbcTemplate.execute("ALTER TABLE item_requests ALTER COLUMN id RESTART WITH 1");
    }

    public void cleanItemRequestResponses() {
        jdbcTemplate.execute("DELETE FROM item_request_responses");
    }

    public void cleanBookings() {
        jdbcTemplate.execute("DELETE FROM bookings");
        jdbcTemplate.execute("ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1");
    }

    public void cleanComments() {
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("ALTER TABLE comments ALTER COLUMN id RESTART WITH 1");
    }

}
