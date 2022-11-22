/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.condition;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectFormat;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/5
 */
class OnProjectFormatCondition extends ProjectGenerationCondition {

    @Override
    protected boolean matches(ProjectDescription description, ConditionContext context,
                              AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attributes = metadata
                .getAllAnnotationAttributes(ConditionalOnProjectFormat.class.getName());
        String value = (String) attributes.getFirst("value");
        ProjectFormat projectFormat = description.getProjectFormat();
        return projectFormat.format().equals(value);
    }

}
