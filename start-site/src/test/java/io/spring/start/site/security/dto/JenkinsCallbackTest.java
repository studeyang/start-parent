package io.spring.start.site.security.dto;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/10/25
 */
public class JenkinsCallbackTest {

    @Test
    public void testStageNext() {
        Assert.assertEquals(JenkinsCallback.Stage.CI, JenkinsCallback.Stage.PRECI.next());
        Assert.assertThrows(IllegalStateException.class, JenkinsCallback.Stage.CD::next);
    }

}