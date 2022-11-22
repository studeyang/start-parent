package io.spring.initializr.generator.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/11
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

    public static String encodeText(CharSequence text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            switch (character) {
                case '\'':
                    sb.append("&apos;");
                    break;
                case '\"':
                    sb.append("&quot;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                default:
                    sb.append(character);
            }
        }
        return sb.toString();
    }

}
