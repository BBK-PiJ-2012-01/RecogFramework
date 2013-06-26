package BestSoFar.framework.core;

import BestSoFar.framework.helper.Mediator;
import BestSoFar.immutables.TypeData;
import org.junit.Test;

/**
 * User: Sam Wright Date: 26/06/2013 Time: 20:17
 */
public class WorkflowImplTest {
    private static TypeData<String,String> stringType = new TypeData<>(String.class, String.class);

    private static class SimpleContainer extends AbstractWorkflowContainer<String,String> {

        public SimpleContainer() {
            super((Workflow<?, ?>) null, stringType);
        }

        public SimpleContainer(AbstractWorkflowContainer<String, String> oldWorkflowContainer, TypeData<String, String> typeData) {
            super(oldWorkflowContainer, typeData);
        }

        @Override
        public Mediator<String> process(Mediator<?> input) {
            return getWorkflows().get(0).process(input);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <I2, O2> Processor<I2, O2> cloneAs(TypeData<I2, O2> typeData) {
            return (Processor<I2, O2>) new SimpleContainer(this, getTypeData());
        }
    }

    @Test
    public void testSimple() throws Exception {
        SimpleContainer container = new SimpleContainer();
        Workflow<String, String> workflow = new WorkflowImpl<>(container, stringType);

        container.getWorkflows().add(workflow);

    }
}