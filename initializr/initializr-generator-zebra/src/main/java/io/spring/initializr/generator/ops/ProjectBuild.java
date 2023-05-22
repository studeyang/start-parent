package io.spring.initializr.generator.ops;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class ProjectBuild {

    private Action action;

    private List<Dir> dirs = new LinkedList<>();

    public void addDir(Dir dir) {
        dirs.add(dir);
    }

}
