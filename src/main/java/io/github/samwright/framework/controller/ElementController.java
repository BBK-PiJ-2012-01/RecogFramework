package io.github.samwright.framework.controller;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.common.ElementObserver;
import io.github.samwright.framework.model.helper.XMLHelper;
import io.github.samwright.framework.model.mock.TopProcessor;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import lombok.Getter;

import java.util.*;

/**
 * User: Sam Wright Date: 17/07/2013 Time: 22:53
 */
abstract public class ElementController extends JavaFXController {

    public final static DataFormat dataFormat
            = new DataFormat("io.github.samwright.framework.model.Element");

    private Map<Node, Pane> configNodesAndParent = new HashMap<>();
    @Getter private ElementLink elementLink;

    {
        setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if (getModel() == null)
                    throw new RuntimeException("handled node has no model");

                ToolboxController toolbox = (ToolboxController) MainApp.beanFactory.getBean("toolbox");
                TransferMode transferMode;

                if (toolbox.getChildren().contains(ElementController.this)) {
                    transferMode = TransferMode.COPY;
                } else {
                    if (mouseEvent.isAltDown() || mouseEvent.isSecondaryButtonDown())
                        transferMode = TransferMode.COPY;
                    else {
                        transferMode = TransferMode.MOVE;
                        setBeingDragged(true);
                        elementLink.setBeingDragged(false);
                    }
                }

                Dragboard db = startDragAndDrop(transferMode);
                ClipboardContent cb = new ClipboardContent();
                cb.put(dataFormat, XMLHelper.writeProcessorToString(getModel()));
                db.setContent(cb);

                mouseEvent.consume();
            }
        });

        setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                setBeingDragged(false);
                dragEvent.consume();
            }
        });

        setSelected(false);
    }

    public ElementController(String fxmlResource) {
        super(fxmlResource);
    }

    public ElementController(ElementController toClone) {
        super(toClone);
        if (toClone.getElementLink() != null)
            setElementLink(toClone.getElementLink().createClone());
    }

    public void setElementLink(ElementLink elementLink) {

        this.elementLink = elementLink;
        if (elementLink != null)
            elementLink.setController(this);

        registerLinkWithElement(getModel());
    }

    private void registerLinkWithElement(Element element) {
        ToolboxController toolbox = (ToolboxController) MainApp.beanFactory.getBean("toolbox");

        if (element != null && !toolbox.getChildren().contains(this)) {
            Set<ElementObserver> newObservers = new HashSet<>(element.getObservers());

            Iterator<ElementObserver> iterator = newObservers.iterator();
            while (iterator.hasNext()) {
                ElementObserver observer = iterator.next();
                if (observer instanceof ElementLink)
                    iterator.remove();
            }

            if (elementLink != null)
                newObservers.add(elementLink);

            if (!newObservers.equals(element.getObservers())) {
                Element newModel = element.withObservers(newObservers);
                TopProcessor topProcessor = getModel().getTopProcessor();
                if (topProcessor != null)
                    MainWindowController.getTopController().startTransientUpdateMode();
                try {
                    element.replaceWith(newModel);
                } finally {
                    if (topProcessor != null)
                        MainWindowController.getTopController().endTransientUpdateMode();
                }
            }
        }
    }

    @Override
    abstract public ElementController createClone();

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();
        registerLinkWithElement(getModel());
    }

    @Override
    public void setBeingDragged(boolean beingDragged) {
        super.setBeingDragged(beingDragged);
        getElementLink().setBeingDragged(beingDragged);
    }

    @Override
    public Element getModel() {
        return (Element) super.getModel();
    }

    @Override
    public void proposeModel(Processor proposedModel) {
        if (!(proposedModel instanceof Element))
            throw new RuntimeException("ElementController can only control Elements - not " +
                    proposedModel);
        super.proposeModel(proposedModel);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        for (Map.Entry<Node,Pane> e : configNodesAndParent.entrySet()) {
            Node configNode = e.getKey();
            Pane parent = e.getValue();

            boolean configVisible = parent.getChildren().contains(configNode);
            if (configVisible && !selected)
                parent.getChildren().remove(configNode);
            else if (!configVisible && selected)
                parent.getChildren().add(0, configNode);
        }

    }

    public void addConfigNode(Node configNode) {
        if (configNodesAndParent.containsKey(configNode))
            return;

        if (configNode.getParent() == null)
            throw new RuntimeException("config node has no parent!");
        configNodesAndParent.put(configNode, (Pane) configNode.getParent());
        setSelected(isSelected());
    }
}
