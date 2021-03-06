package io.github.samwright.framework.javacv;

import io.github.samwright.framework.controller.WorkflowContainerControllerImpl;
import io.github.samwright.framework.controller.WorkflowControllerImpl;
import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.model.WorkflowContainer;

import java.util.Arrays;

/**
 * User: Sam Wright Date: 06/09/2013 Time: 22:45
 */
public class SkinDetectorController extends WorkflowContainerControllerImpl {

    {
        header.getChildren().remove(addButton);
        containerLabel.setText("Skin Detector");
    }

    public SkinDetectorController() {
        WorkflowContainer model = new SkinDetector();

        model = model.withChildren(Arrays.asList(new WorkflowControllerImpl().getModel(),
                new WorkflowControllerImpl().getModel()));
        model.replace(null);
        proposeModel(model);
        setElementLink(new ElementLink());
    }

    public SkinDetectorController(WorkflowContainerControllerImpl toClone) {
        super(toClone);
    }

    @Override
    public SkinDetectorController createClone() {
        return new SkinDetectorController(this);
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();

        relabelWorkflow(0, "Image");
        relabelWorkflow(1, "Skin Colour");
    }
}
