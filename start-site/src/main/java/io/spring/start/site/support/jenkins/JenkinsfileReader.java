package io.spring.start.site.support.jenkins;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/10/10
 */
public class JenkinsfileReader {

    private static final String TO_REPLACE = "###START.CASSTIME.COM###";
    private static final String CHOICE = "choice";
    private static final String SERVICE_NAME = "SERVICE_NAME";
    private final Parameters parameters;
    private String jenkinsfileContent;

    public JenkinsfileReader(Path filePath) throws IOException {
        jenkinsfileContent = String.join("\n", Files.readAllLines(filePath, Charset.defaultCharset()));
        parameters = parseParmeters();
    }

    public JenkinsfileReader(String content) {
        jenkinsfileContent = content;
        parameters = parseParmeters();
    }

    public JenkinsfileReader addChoices(String serviceName) {
        for (Choice choice : parameters.getChoiceList()) {
            if (SERVICE_NAME.equals(choice.getName())) {
                choice.addChoice(serviceName);
                replaceContent(TO_REPLACE, serialize(choice));
            }
        }
        return this;
    }

    public String getContent() {
        return jenkinsfileContent;
    }

    private void replaceContent(String target, String replacement) {
        jenkinsfileContent = jenkinsfileContent.replace(target, replacement);
    }

    private String serialize(Choice choice) {
        StringBuilder content = new StringBuilder();
        content.append("choice(name: '").append(choice.getName()).append("', ").append("choices: [").append("\n");
        for (int i = 0; i < choice.getChoices().size(); i++) {
            String service = choice.getChoices().get(i);
            content.append("      '").append(service).append("'");
            if (i != choice.getChoices().size() - 1) {
                content.append(",");
            }
            content.append("\n");
        }
        content.append("    ], description: '").append(choice.getDescription()).append("')");
        return content.toString();
    }

    private Parameters parseParmeters() {

        Parameters p = new Parameters();

        int startIndex = jenkinsfileContent.indexOf("parameters");
        if (startIndex > 0) {
            int endIndex = jenkinsfileContent.indexOf('}', startIndex) + 1;
            String parametersContent = jenkinsfileContent.substring(startIndex, endIndex);
            List<Choice> choices = parseChoices(parametersContent);
            p.setChoiceList(choices);
        }
        return p;
    }

    private List<Choice> parseChoices(String content) {
        List<Choice> choiceList = new ArrayList<>();

        int startIndex = content.indexOf(CHOICE);
        while (startIndex > 0) {
            int endIndex = startIndex + CHOICE.length();
            // 括号集合
            Stack<String> brackets = new Stack<>();
            while (true) {
                if (content.charAt(endIndex) == '(') {
                    brackets.add("(");
                } else if (content.charAt(endIndex) == ')') {
                    brackets.pop();
                } else if (brackets.isEmpty()) {
                    break;
                }
                endIndex++;
            }
            Choice choice = parseChoice(content.substring(startIndex, endIndex));
            choiceList.add(choice);

            startIndex = content.indexOf(CHOICE, endIndex);
        }
        return choiceList;
    }

    private Choice parseChoice(String content) {

        Choice choice = new Choice();

        int startIndex = content.indexOf(SERVICE_NAME);
        if (startIndex > 0) {
            startIndex = content.indexOf("[") + 1;
            int endIndex = content.indexOf(']', startIndex);
            List<String> choices = parseChoicesList(content.substring(startIndex, endIndex));

            choice.setName(SERVICE_NAME);
            choice.setChoices(choices);

            // 替换一下
            replaceContent(content, TO_REPLACE);

            int descIndex = content.indexOf("description");
            if (descIndex > 0) {
                int endDescIndex = content.lastIndexOf('\'') + 1;
                String description = content.substring(descIndex, endDescIndex);
                int startQuote = description.indexOf('\'') + 1;
                int endQuote = description.lastIndexOf('\'');
                choice.setDescription(description.substring(startQuote, endQuote));
            }
        }
        return choice;
    }

    private List<String> parseChoicesList(String content) {

        List<String> serviceNameList = new ArrayList<>();

        char[] array = content.toCharArray();
        for (int i = 0; i < array.length; i++) {
            StringBuilder serviceName = new StringBuilder();
            if (array[i] == '\'') {
                while (array[++i] != '\'') {
                    serviceName.append(array[i]);
                }
                serviceNameList.add(serviceName.toString());
            }
        }
        return serviceNameList;
    }

}
