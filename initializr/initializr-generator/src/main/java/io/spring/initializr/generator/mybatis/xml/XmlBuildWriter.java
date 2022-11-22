package io.spring.initializr.generator.mybatis.xml;

import io.spring.initializr.generator.io.IndentingWriter;

import java.util.List;
import java.util.Map;

import static io.spring.initializr.generator.utils.StringUtils.encodeText;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
public class XmlBuildWriter {

    public void writeTo(IndentingWriter writer, XmlBuild build) {
        writeProject(writer, build.getNamespace(), () -> {
            writeResultMap(writer, build);
            writeSql(writer, build);
            writeSelect(writer, build);
        });
    }

    private void writeSelect(IndentingWriter writer, XmlBuild build) {
        writeSingleElement(writer, "select", build.selectAttributes(), build.getSelectElement().getValues());
    }

    private void writeSql(IndentingWriter writer, XmlBuild build) {
        writeSingleElement(writer, "sql", build.sqlAttributes(), build.getSqlElement().getValues());
    }

    private void writeResultMap(IndentingWriter writer, XmlBuild build) {
        writeSingleElement(writer, "resultMap", build.resultMapAttributes(), build.getResultMapElement().getValues());
    }

    protected void writeSingleElement(IndentingWriter writer, String name, Map<String, String> attributes,
                                      List<String> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        StringBuilder builder = new StringBuilder("<").append(name);
        attributes.forEach(
                (k, v) -> builder.append(" ").append(k).append("=").append("\"").append(encodeText(v)).append("\"")
        );
        writer.print(builder.toString());
        if (values.isEmpty()) {
            writer.println("/>");
            return;
        }
        writer.println(">");
        writer.indented(() -> values.forEach(writer::println));
        writer.println(String.format("</%s>", name));

    }

    private void writeProject(IndentingWriter writer, String namespace, Runnable whenWritten) {
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
        writer.println(String.format("<mapper namespace=\"%s\">", namespace));
        writer.indented(whenWritten);
        writer.println("</mapper>");
    }


}
