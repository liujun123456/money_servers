package org.example.dto;

import lombok.Data;

@Data
public class ResponseDTO<T> {
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public ResponseDTO() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ResponseDTO<T> success(T data) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setCode(200);
        response.setMessage("成功");
        response.setData(data);
        return response;
    }

    public static <T> ResponseDTO<T> success(String message, T data) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setCode(200);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static ResponseDTO<?> error(Integer code, String message) {
        ResponseDTO<?> response = new ResponseDTO<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}