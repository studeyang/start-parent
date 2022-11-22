package io.spring.start.site.support.jenkins;

import lombok.Data;

import java.util.List;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/10/10
 */
@Data
public class Choice {

    private String name;
    private List<String> choices;
    private String description;

    public void addChoice(String choice) {
        if (!choices.contains(choice)) {
            choices.add(choice);
        }
    }

}
