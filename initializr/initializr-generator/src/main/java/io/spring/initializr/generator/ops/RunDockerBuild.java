package io.spring.initializr.generator.ops;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * runDockerBuild(
 *   'appName': 'icec-cloud-zebra-service',
 *   'dockerRegistry': 'registry-vpc.cn-shenzhen.aliyuncs.com',
 *   'dockerImage': 'cassmall/icec-cloud-zebra-service',
 *   'serviceName': 'icec-cloud-zebra'
 * )
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/18
 */
@Data
public class RunDockerBuild {

    private Map<String, String> map = new LinkedHashMap<>();

    public RunDockerBuild add(String key, String value) {
        map.put(key, value);
        return this;
    }

    public void forEach(BiConsumer<String, String> action) {
        String[] keyArray = map.keySet().toArray(new String[0]);
        for (int i = 0; i < keyArray.length; i++) {
            String key = "'" + keyArray[i] + "'";
            String value = "'" + map.get(keyArray[i]) + "'";
            if (i != keyArray.length - 1) {
                value += ",";
            }
            action.accept(key, value);
        }
    }

}
