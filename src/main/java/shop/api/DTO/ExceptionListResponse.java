package shop.api.DTO;


import lombok.Data;

import java.time.Instant;
import java.util.*;

@Data
public class ExceptionListResponse {
    private String error;
    private Integer status;
    private Map<String,String> messages;
    private Instant timestamp;

    public ExceptionListResponse(Map<String,String> messages, String error, Integer status) {
        setMessages(messages);
        this.error = error;
        this.status = status;
        this.timestamp = Instant.now();
    }

    public Map<String,String> getMessages() {

        return messages == null ? null : new HashMap<>(messages);
    }

    public final void setMessages(Map<String,String> messages) {

        if (messages == null) {
            this.messages = null;
        } else {
            this.messages = Collections.unmodifiableMap(messages);
        }
    }

}