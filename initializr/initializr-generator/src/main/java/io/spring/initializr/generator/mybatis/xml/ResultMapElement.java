package io.spring.initializr.generator.mybatis.xml;

import lombok.Getter;

@Getter
public class ResultMapElement extends BaseElement {

    /**
     * <resultMap id="BaseResultMap" type="com.yourcompany.service.entity.OrderEntity">
     *   <id column="id" jdbcType="INTEGER" property="id"/>
     *   <result column="order_status" jdbcType="TINYINT" property="orderStatus"/>
     *   <result column="order_create_date" jdbcType="TIMESTAMP" property="orderCreateDate"/>
     * </resultMap>
     */
    private final String id;

    private final String type;

    public ResultMapElement(String id, String type) {
        this.id = id;
        this.type = type;
    }

}
