package io.spring.initializr.generator.ops;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * checkAndRunMaven(
 *   'mvnActions': ['clean', '-U', 'deploy']
 * )
 * <p>
 * runMaven(
 *   'mvnActions': ['clean', 'install']
 * )
 * <p>
 * checkAndRunMaven(
 *   'mvnActions': ['clean', '-U', 'deploy', '-N']
 * )
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/15
 */
@Getter
public class Action {

    @Setter
    private String name;
    private List<String> mvnActions;

    public Action() {
        this.mvnActions = new LinkedList<>();
    }

    public Action(String name, Consumer<List<String>> actions) {
        this.name = name;
        this.mvnActions = new LinkedList<>();
        actions.accept(mvnActions);
    }

    public void mvnAction(String action) {
        mvnActions.add(action);
    }

}
