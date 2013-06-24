package attempt2;

import java.util.List;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 12:00
 */
public interface ProcessorObserver<T> {
    /**
     * Notify this ProcessorObserver that a Processor it observes is about to output
     * a mediator object.
     *
     * @param mediator the mediator the observed Processor is about to output.
     */
    void notify(Mediator<T> mediator);

    /**
     * Notify this ProcessorObserver that a Processor it observes is about to output
     * a batch of mediator objects (ie. a training batch).
     *
     * @param mediators the batch of mediators the observed Processor is about to output.
     */
    void notify(List<Mediator<T>> mediators);
}
