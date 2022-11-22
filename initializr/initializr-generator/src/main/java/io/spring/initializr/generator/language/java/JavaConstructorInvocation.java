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

package io.spring.initializr.generator.language.java;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * An invocation of a constructor.
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
@Getter
public class JavaConstructorInvocation extends JavaExpression {

    /**
     * 返回值
     */
    private final Variable variable;

    private final String target;

    private final List<String> arguments;

    public JavaConstructorInvocation(String target, String... arguments) {
        this(null, target, arguments);
    }

    public JavaConstructorInvocation(Variable variable, String target, String... arguments) {
        this.variable = variable;
        this.target = target;
        this.arguments = Arrays.asList(arguments);
    }

}
