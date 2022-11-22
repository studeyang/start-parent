package io.spring.initializr.zebra.contributor.service;

import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.mybatis.xml.*;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.initializr.zebra.contributor.support.ContributorSupport;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.spring.initializr.zebra.contributor.support.ContributorSupport.DEFAULT_CODE_CONTRIBUTOR_ORDER;

/**
 * Service 模块 service/biz OrderMapper.xml 生成
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
public class OrderMapperXmlProjectContributor implements ProjectContributor {

    private final ProjectDescription description;
    private final XmlBuild xmlBuild;
    private final XmlBuildWriter xmlBuildWriter;
    private final IndentingWriterFactory indentingWriterFactory;

    public OrderMapperXmlProjectContributor(ProjectDescription description) {
        this.description = description;
        this.xmlBuild = new XmlBuild();
        this.xmlBuildWriter = new XmlBuildWriter();
        this.indentingWriterFactory = IndentingWriterFactory.withDefaultSettings();
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        String packageName = description.getPackageName().replace(".", "/");
        Path mapperXml = ContributorSupport.getServicePath(projectRoot, description.getArtifactId())
                .resolve("src/main/java")
                .resolve(packageName)
                .resolve("service/dao/xml/OrderMapper.xml");
        if (Files.notExists(mapperXml)) {
            Files.createDirectories(mapperXml.getParent());
            Files.createFile(mapperXml);
        }
        customize();

        Writer out = Files.newBufferedWriter(mapperXml);
        try (IndentingWriter writer = this.indentingWriterFactory.createIndentingWriter("OrderMapper", out)) {
            xmlBuildWriter.writeTo(writer, xmlBuild);
        }
    }

    @Override
    public int getOrder() {
        return DEFAULT_CODE_CONTRIBUTOR_ORDER;
    }

    private void customize() {
        String packageName = this.description.getPackageName();
        xmlBuild.setNamespace(OrderMapperCodeProjectContributor.referenceClass(packageName));
        xmlBuild.setResultMapElement(
                new ResultMapElement("BaseResultMap", OrderEntityCodeProjectContributor.referenceClass(packageName))
                        .addValue("<id column=\"id\" jdbcType=\"INTEGER\" property=\"id\"/>")
                        .addValue("<result column=\"order_status\" jdbcType=\"TINYINT\" property=\"orderStatus\"/>")
                        .addValue("<result column=\"order_create_date\" jdbcType=\"TIMESTAMP\" property=\"orderCreateDate\"/>")
        );
        xmlBuild.setSqlElement(
                new SqlElement("Base_Column_List")
                        .addValue("id, order_status, order_create_date")
        );
        xmlBuild.setSelectElement(
                new SelectElement("selectById")
                        .setParameterType("java.lang.Integer")
                        .setResultMap("BaseResultMap")
                        .addValue("select")
                        .addValue("<include refid=\"Base_Column_List\"/>")
                        .addValue("from tbl_order")
                        .addValue("where id = #{orderId}")
        );
    }

}
