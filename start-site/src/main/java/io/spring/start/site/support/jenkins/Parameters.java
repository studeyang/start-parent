package io.spring.start.site.support.jenkins;

import lombok.Data;

import java.util.List;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/10/10
 */
@Data
public class Parameters {

    private List<Choice> choiceList;

    public List<String> getChoiceOfServiceName() {
        for (Choice choice : choiceList) {
            if ("SERVICE_NAME".equals(choice.getName())) {
                return choice.getChoices();
            }
        }
        throw new JenkinsfileParseException("未找到 'SERVICE_NAME' 的 choice.");
    }

    public void addChoiceOfServiceName(String newService) {
        for (Choice choice : choiceList) {
            if ("SERVICE_NAME".equals(choice.getName())) {
                choice.addChoice(newService);
            }
        }
    }

}
