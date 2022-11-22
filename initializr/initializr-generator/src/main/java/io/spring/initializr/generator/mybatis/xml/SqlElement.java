package io.spring.initializr.generator.mybatis.xml;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SqlElement extends BaseElement {

    /**
     *     <sql id="Base_Column_List">
     *       id, order_status, order_create_date
     *     </sql>
     */
    private final String id;

}
