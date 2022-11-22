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

import java.util.*;

/**
 * An invocation of a static field/method.
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
@Getter
public class JavaStaticInvocation extends JavaExpression {

    /**
     * 返回值, nullable
     */
    private final Variable variable;

    private final String target;

    /**
     * 静态 field / method
     * <p>
     * 通过()来判断 field/method
     */
    private final String name;

    /**
     * 静态方法的参数, ${} 表示是一个变量
     */
    private final List<String> arguments = new LinkedList<>();

    public JavaStaticInvocation(String target, String name, String... argument) {
        this(null, target, name, argument);
    }

    public JavaStaticInvocation(Variable variable, String target, String name, String... argument) {
        this.variable = variable;
        this.target = target;
        this.name = name;
        this.arguments.addAll(Arrays.asList(argument));
    }

}
