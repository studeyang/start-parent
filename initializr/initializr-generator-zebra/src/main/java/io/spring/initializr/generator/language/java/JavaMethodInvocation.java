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
 * An invocation of a method.
 *
 * @author Andy Wilkinson
 * @author <a href="https://github.com/studeyang">studeyang</a>
 */
@Getter
public class JavaMethodInvocation extends JavaExpression {

    private final Variable variable;

    private final String target;

    private final String name;

    private final List<String> arguments;

    /**
     * stream invocation
     */
    public static JavaMethodInvocation method(String name, String... arguments) {
        return new JavaMethodInvocation(null, null, name, arguments);
    }

    public JavaMethodInvocation(String target, String name, String... arguments) {
        this(null, target, name, arguments);
    }

    public JavaMethodInvocation(Variable variable, String target, String name, String... arguments) {
        this.variable = variable;
        this.target = target;
        this.name = name;
        this.arguments = Arrays.asList(arguments);
    }

}
