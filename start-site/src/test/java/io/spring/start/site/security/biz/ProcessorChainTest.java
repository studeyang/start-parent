package io.spring.start.site.security.biz;

import io.spring.start.site.security.bo.ProcessRequest;
import org.junit.Test;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/14
 */
public class ProcessorChainTest {

    @Test
    public void test_doProcess() {
        ProcessorChain.beginWith(new Processor1())
                .then(new Processor2())
                .then(new Processor3())
                .startUp(new ProcessRequest());
    }

    private static class Processor1 implements TransactionProcessor<Void> {
        @Override
        public Void process(ProcessRequest processRequest) throws ProcessException {
            System.out.println("Processor 1...");
            return null;
        }

        @Override
        public void rollback(Void result) {
            System.out.println("rollback Processor 1...");
        }
    }

    private static class Processor2 implements TransactionProcessor<Void> {
        @Override
        public Void process(ProcessRequest processRequest) throws ProcessException {
            System.out.println("Processor 2...");
            return null;
        }

        @Override
        public void rollback(Void result) {
            System.out.println("rollback Processor 2...");
        }
    }

    private static class Processor3 implements TransactionProcessor<Void> {
        @Override
        public Void process(ProcessRequest processRequest) throws ProcessException {
            System.out.println("Processor 3...");
            throw new ProcessException("test");
        }

        @Override
        public void rollback(Void result) {
            System.out.println("rollback Processor 3...");
        }
    }

}