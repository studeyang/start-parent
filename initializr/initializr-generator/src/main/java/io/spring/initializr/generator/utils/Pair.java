package io.spring.initializr.generator.utils;

import lombok.Getter;

@Getter
public class Pair<L, R> {

    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

}
