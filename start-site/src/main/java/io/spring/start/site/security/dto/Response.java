package io.spring.start.site.security.dto;

import lombok.Data;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/14
 */
@Data
public class Response<T> {

    public static <T> Response<T> data(T data) {
        Response<T> response = new Response<>();
        response.setAction(Action.DATA);
        response.setData(data);
        return response;
    }

    public static Response<String> redirect(String url) {
        Response<String> response = new Response<>();
        response.setAction(Action.REDIRECT);
        response.setData(url);
        return response;
    }

    public static <T> Response<T> instance(boolean success, T data) {
        Response<T> response = new Response<>();
        if (success) {
            response.setAction(Action.SUCCESS);
        } else {
            response.setAction(Action.FAIL);
        }
        response.setData(data);
        return response;
    }

    private Action action;
    private T data;

    private enum Action {
        REDIRECT, DATA,
        SUCCESS, FAIL
    }

}
