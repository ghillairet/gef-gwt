package org.eclipse.jface.resource;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

public class ImageDescriptorHelper {

	Map<String, Image> imageMap;
	private static ImageDescriptorHelper imageDescriptorHelper;

	private ImageDescriptorHelper() {
		imageMap = new HashMap<String, Image>();
	}

	public static ImageDescriptorHelper getInstance() {
		if (imageDescriptorHelper == null) {
			imageDescriptorHelper = new ImageDescriptorHelper();
		}

		return imageDescriptorHelper;
	}

	public Image getImage(Class location, String name) {
		return imageMap.get(location + ":" + name);
	}

	public void registerImage(Class location, String name, Image image) {
		imageMap.put(location + ":" + name, image);
	}

}
