package ru.practicum.shareit.common.web.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


class BaseClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BaseClient baseClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        baseClient = spy(new BaseClient(restTemplate)); // spy чтобы проверять делегирование
    }

    @Test
    void get_withPath_callsMakeAndSendRequestGet() {
        String path = "/test";
        ResponseEntity<Object> expected = ResponseEntity.ok("ok");
        doReturn(expected).when(baseClient).get(path, null, null);

        ResponseEntity<Object> actual = baseClient.get(path);

        assertEquals(expected, actual);
        verify(baseClient).get(path, null, null);
    }

    @Test
    void get_withPathAndUserId_callsMakeAndSendRequestGet() {
        String path = "/abc";
        long userId = 5L;
        ResponseEntity<Object> expected = ResponseEntity.ok("X");
        doReturn(expected).when(baseClient).get(path, userId, null);

        ResponseEntity<Object> actual = baseClient.get(path, userId);

        assertEquals(expected, actual);
        verify(baseClient).get(path, userId, null);
    }

    @Test
    void post_withPathAndBody_invokesMakeAndSendRequestPost() {
        String path = "/new";
        String body = "{\"a\":1}";
        ResponseEntity<Object> expected = ResponseEntity.status(201).body(Map.of("r", 1));
        doReturn(expected).when(baseClient).post(path, null, null, body);

        ResponseEntity<Object> actual = baseClient.post(path, body);

        assertEquals(expected, actual);
        verify(baseClient).post(path, null, null, body);
    }

    @Test
    void delete_withPath_callsMakeAndSendRequestDelete() {
        String path = "/del";
        ResponseEntity<Object> expected = ResponseEntity.ok().build();
        doReturn(expected).when(baseClient).delete(path, null, null);

        ResponseEntity<Object> actual = baseClient.delete(path);

        assertEquals(expected, actual);
        verify(baseClient).delete(path, null, null);
    }

    @Test
    void makeAndSendRequest_successful2xx() {
        String path = "/ok";
        Long userId = 7L;
        String body = "testBody";
        ResponseEntity<Object> response = ResponseEntity.ok("body");

        when(restTemplate.exchange(
                eq(path), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        ResponseEntity<Object> actual = callMakeAndSendRequest(HttpMethod.POST, path, userId, null, body);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals("body", actual.getBody());
    }

    @Test
    void makeAndSendRequest_errorStatusPassesBody() {
        String path = "/fail";
        Long userId = null;
        String body = null;
        ResponseEntity<Object> response = ResponseEntity.status(418).body("failure!");

        when(restTemplate.exchange(
                eq(path), eq(HttpMethod.DELETE),
                any(HttpEntity.class), eq(Object.class)))
                .thenReturn(response);

        ResponseEntity<Object> actual = callMakeAndSendRequest(HttpMethod.DELETE, path, userId, null, body);

        assertEquals(HttpStatus.I_AM_A_TEAPOT, actual.getStatusCode());
        assertEquals("failure!", actual.getBody());
    }

    @Test
    void makeAndSendRequest_httpStatusCodeExceptionReturnedAsBody() {
        String path = "/ex";
        Long userId = 1L;

        HttpStatusCodeException ex = new HttpStatusCodeException(HttpStatus.NOT_FOUND, "Not found", null, "err".getBytes(), StandardCharsets.UTF_8) {};
        when(restTemplate.exchange(
                eq(path), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(Object.class)))
                .thenThrow(ex);

        ResponseEntity<Object> actual = callMakeAndSendRequest(HttpMethod.GET, path, userId, null, null);

        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertArrayEquals("err".getBytes(), (byte[]) actual.getBody());
    }

    @Test
    void defaultHeaders_setsContentTypeAndUserId() {
        var headers = invokeDefaultHeaders(123L);

        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals(List.of(MediaType.APPLICATION_JSON), headers.getAccept());
        assertEquals("123", headers.getFirst("X-Sharer-User-Id"));
    }

    @Test
    void buildParametersFromMap_worksCorrectly() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("a", "Hello World");
        params.put("b", 15);

        String result = baseClient.buildParametersFromMap(params);
        assertTrue(result.contains("a=Hello+World") || result.contains("a=Hello%20World")); // depends on URLEncoder
        assertTrue(result.contains("b=15"));
        assertTrue(result.startsWith("?"));
        assertTrue(result.contains("&"));
    }

    // -- helpers --

    // Делает вызов приватного метода через Reflection (или можно переложить на protected/package-visible для тестов).
    private ResponseEntity<Object> callMakeAndSendRequest(HttpMethod method, String path, Long userId, Map<String, Object> params, Object body) {
        try {
            var methodObj = BaseClient.class.getDeclaredMethod(
                    "makeAndSendRequest",
                    HttpMethod.class, String.class, Long.class, Map.class, Object.class
            );
            methodObj.setAccessible(true);
            return (ResponseEntity<Object>) methodObj.invoke(baseClient, method, path, userId, params, body);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HttpHeaders invokeDefaultHeaders(Long userId) {
        try {
            var methodObj = BaseClient.class.getDeclaredMethod("defaultHeaders", Long.class);
            methodObj.setAccessible(true);
            return (HttpHeaders) methodObj.invoke(baseClient, userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}