package io.spring.initializr.generator.mybatis.xml;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

public abstract class BaseElement {

    @Getter
    private final List<String> values = new LinkedList<>();

    public <T extends BaseElement> T addValue(String value) {
        this.values.add(value);
        return (T) this;
    }

}
