package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getItems(Long sharerId) {
        return get("", sharerId);
    }

    public ResponseEntity<Object> createItem(Long sharerId, ItemRequestDto itemRequestDto) {
        return post("", sharerId, itemRequestDto);
    }

    public ResponseEntity<Object> patchItem(Long itemId, Long userId, ItemRequestDto itemRequestDto) {
        return patch("/" + itemId, userId, itemRequestDto);
    }

    public ResponseEntity<Object> getItem(long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> searchItems(Long sharerId, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("?text={text}", sharerId, parameters);
    }

    public ResponseEntity<Object> createComment(Long sharerId, Long itemId, CommentRequestDto commentRequestDto) {
        return post("/" + itemId + "/comment", sharerId, commentRequestDto);
    }

}
