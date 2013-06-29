package BestSoFar.framework.core;

import BestSoFar.framework.core.helper.Mediator;
import BestSoFar.framework.immutables.ImmutableList;
import BestSoFar.framework.core.helper.TypeData;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of {@link WorkflowContainer}.
 * <p/>
 * Concrete WorkflowContainer implementations can derive from this to let it handle the internal
 * list of {@link Workflow} objects (and all the cloning that comes from modifications to it),
 * along with boilerplate code (accessors for parent and {@link TypeData}, and
 * {@link BestSoFar.framework.core.common.ProcessObserver} management).
 */
public abstract class AbstractWorkflowContainer<I, O>
        extends AbstractElement<I, O> implements WorkflowContainer<I, O> {

    @Getter private final ImmutableList<Workflow<I, O>> workflows;

    public AbstractWorkflowContainer(Workflow<?, ?> parent, TypeData<I, O> typeData) {
        super(parent, typeData);
        workflows = new ImmutableList<>(this);
    }

    public AbstractWorkflowContainer(AbstractWorkflowContainer<I, O> oldWorkflowContainer, TypeData<I, O> typeData) {
        super(oldWorkflowContainer, typeData);
        workflows = oldWorkflowContainer.workflows.assignReplacementTo(this);
    }

    @Override
    public boolean isValid() {
        for (Workflow<I, O> workflow : workflows) {
            if (!workflow.isValid())
                return false;
        }

        return true;
    }

    @Override
    public List<Mediator<O>> processTrainingBatch(List<Mediator<?>> inputs) {
        List<Mediator<O>> outputs = new ArrayList<>();

        for (Workflow<I, O> workflow : workflows)
            outputs.addAll(workflow.processTrainingBatch(inputs));

        return outputs;
    }

    @Override
    public <I2, O2> void replaceSelfWithClone(Processor<I2, O2> clone) {
        AbstractWorkflowContainer<I, O> replacement = (AbstractWorkflowContainer<I, O>) clone;

        for (Workflow<I, O> workflow : workflows)
            workflow.setParent(replacement);

        super.replaceSelfWithClone(clone);
    }

}
