package io.spring.initializr.generator.ops;

import lombok.Data;

import java.util.function.Consumer;

@Data
public class Dir {

    private String name;
    private RunDockerBuild runDockerBuild;
    private Action action;

    private Dir(String name) {
        this.name = name;
    }

    public static Dir newInstanceWithRunDockerBuild(String name, Consumer<RunDockerBuild> action) {
        Dir dir = new Dir(name);
        RunDockerBuild runDockerBuild = new RunDockerBuild();
        dir.setRunDockerBuild(runDockerBuild);
        action.accept(runDockerBuild);
        return dir;
    }

    public static Dir newInstanceWithAction(String name, Consumer<Action> consumer) {
        Dir dir = new Dir(name);
        Action action = new Action();
        dir.setAction(action);
        consumer.accept(action);
        return dir;
    }

}
