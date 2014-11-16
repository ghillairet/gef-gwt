package example.client;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

import example.client.shapes.model.RectangularShape;
import example.client.shapes.model.ShapesDiagram;
import example.client.shapes.parts.ShapesEditPartFactory;

public class App implements EntryPoint {

	@Override
	public void onModuleLoad() {
		GraphicalViewer viewer = new ScrollingGraphicalViewer();
		ScalableFreeformRootEditPart root = new ScalableFreeformRootEditPart();
		viewer.setRootEditPart(root);
		viewer.setEditPartFactory(new ShapesEditPartFactory());
		viewer.setEditDomain(new EditDomain());
		viewer.setContents(createContent());

		Composite c = new Composite(null, SWT.NONE);
		viewer.createControl(c);

		RootPanel.get().add(c.getGwtWidget());
	}

	public Object createContent() {
		ShapesDiagram diagram = new ShapesDiagram();
		RectangularShape r1 = new RectangularShape();
		r1.setLocation(new Point(10, 20));
		r1.setSize(new Dimension(40, 50));
		diagram.getChildren().add(r1);

		return diagram;
	}

}
