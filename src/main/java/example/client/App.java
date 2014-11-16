package example.client;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import example.client.shapes.ShapesEditorPaletteFactory;
import example.client.shapes.model.RectangularShape;
import example.client.shapes.model.ShapesDiagram;
import example.client.shapes.parts.ShapesEditPartFactory;

public class App implements EntryPoint {

	@Override
	public void onModuleLoad() {
		VerticalPanel panel = new VerticalPanel();

		GraphicalViewer viewer = new ScrollingGraphicalViewer();
		ScalableFreeformRootEditPart root = new ScalableFreeformRootEditPart();
		viewer.setRootEditPart(root);
		viewer.setEditPartFactory(new ShapesEditPartFactory());
		viewer.setEditDomain(new EditDomain());
		viewer.setContents(createContent());

		Composite c1 = new Composite(null, SWT.NONE);
		c1.setSize(1024, 1024);
		viewer.createControl(c1);

		PaletteRoot paletteRoot = ShapesEditorPaletteFactory.createPalette();
		PaletteViewerProvider provider = new PaletteViewerProvider(viewer.getEditDomain());

		Composite c2 = new Composite(null, SWT.NONE);
		PaletteViewer paletteViewer = provider.createPaletteViewer(c2);
		paletteViewer.setContents(paletteRoot);
		c2.setSize(120, 200);

		panel.add(c1.getGwtWidget());
		panel.add(c2.getGwtWidget());
		RootPanel.get().add(panel);
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
