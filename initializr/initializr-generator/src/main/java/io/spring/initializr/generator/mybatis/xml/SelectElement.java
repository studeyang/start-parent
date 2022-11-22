package io.spring.initializr.generator.mybatis.xml;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.List;

@Getter
@Accessors(chain = true)
public class SelectElement extends BaseElement {

    private String id;

    @Setter
    private String parameterType;
    @Setter
    private String resultMap;

    public SelectElement(String id) {
        this.id = id;
    }

}
