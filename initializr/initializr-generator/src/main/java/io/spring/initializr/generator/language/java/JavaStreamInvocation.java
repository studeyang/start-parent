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
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * An stream invocation.
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
@Setter
@Getter
public class JavaStreamInvocation extends JavaExpression {

    /**
     * 返回值
     */
    private final Variable variable;

    private final List<JavaExpression> targets = new LinkedList<>();

    public JavaStreamInvocation(JavaExpression target) {
        this(null, target);
    }

    public JavaStreamInvocation(Variable variable, JavaExpression target) {
        this.variable = variable;
        this.targets.add(target);
    }

    public JavaStreamInvocation target(JavaExpression target) {
        targets.add(target);
        return this;
    }

}
