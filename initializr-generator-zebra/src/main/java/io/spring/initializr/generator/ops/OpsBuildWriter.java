package io.spring.initializr.generator.ops;

import io.spring.initializr.generator.io.IndentingWriter;

public class OpsBuildWriter {

    public void writeTo(IndentingWriter writer, DockerBuild build) {
        writeDir(writer, build.getDir());
    }

    public void writeTo(IndentingWriter writer, ProjectBuild build) {
        writeAction(writer, build.getAction());
        for (Dir dir : build.getDirs()) {
            writeDir(writer, dir);
        }
    }

    private void writeDir(IndentingWriter writer, Dir dir) {
        writer.println("dir('" + dir.getName() + "') {");
        if (dir.getRunDockerBuild() != null) {
            writeRunDockerBuild(writer, dir.getRunDockerBuild());
        }
        if (dir.getAction() != null) {
            writer.indented(() -> writeAction(writer, dir.getAction()));
        }
        writer.println("}");
    }

    private void writeRunDockerBuild(IndentingWriter writer, RunDockerBuild runDockerBuild) {
        writer.indented(() -> {
            writer.println("runDockerBuild(");
            writer.indented(
                    () -> runDockerBuild.forEach(
                            (key, value) -> writer.println(key + ": " + value)
                    )
            );
            writer.print(")");
        });
        writer.println();
    }

    private void writeAction(IndentingWriter writer, Action action) {
        writer.println(action.getName() + "(");
        writer.indented(() -> {
            writer.print("'mvnActions': [");
            for (int i = 0; i < action.getMvnActions().size(); i++) {
                String mvnAction = action.getMvnActions().get(i);
                writer.print("'" + mvnAction + "'");
                if (i != action.getMvnActions().size() - 1) {
                    writer.print(", ");
                }
            }
            writer.println("]");
        });
        writer.println(")");
    }

}
