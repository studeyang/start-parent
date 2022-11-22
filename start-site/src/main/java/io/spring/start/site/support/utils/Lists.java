package io.spring.start.site.support.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/11/22
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Lists {

    public static <T> ArrayList<T> newArrayList(T element) {
        ArrayList<T> list = new ArrayList<>();
        list.add(element);
        return list;
    }

}
