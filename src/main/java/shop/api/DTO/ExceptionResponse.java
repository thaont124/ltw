package shop.api.DTO;


import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class ExceptionResponse {
    private String error;
    private Integer status;
    private String messages;
    private Instant timestamp;

    public ExceptionResponse( String messages, String error, Integer status) {
       this.messages=messages;
        this.error = error;
        this.status = status;
        this.timestamp = Instant.now();
    }




}