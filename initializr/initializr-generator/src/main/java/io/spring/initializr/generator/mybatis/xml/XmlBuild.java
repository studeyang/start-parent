package io.spring.initializr.generator.mybatis.xml;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class XmlBuild {

    private ResultMapElement resultMapElement;

    private SqlElement sqlElement;

    private SelectElement selectElement;

    private String namespace;

    public Map<String, String> resultMapAttributes() {
        Map<String, String> map = new LinkedHashMap<>(2);
        map.put("id", resultMapElement.getId());
        map.put("type", resultMapElement.getType());
        return map;
    }

    public Map<String, String> sqlAttributes() {
        Map<String, String> map = new LinkedHashMap<>(2);
        map.put("id", sqlElement.getId());
        return map;
    }

    public Map<String, String> selectAttributes() {
        Map<String, String> map = new LinkedHashMap<>(2);
        map.put("id", selectElement.getId());
        putValueIfNotNull(map, "parameterType", selectElement.getParameterType());
        putValueIfNotNull(map, "resultMap", selectElement.getResultMap());
        return map;
    }

    private void putValueIfNotNull(Map<String, String> map, String key, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        map.put(key, value);
    }

}
