package org.eclipse.core.internal;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;

/**
 * Abstract super-class for scope context object contributed by the Platform.
 * 
 * @since 3.0
 */
public abstract class AbstractScope implements IScopeContext {

	/*
	 * @see org.eclipse.core.runtime.preferences.IScopeContext#getName()
	 */
	public abstract String getName();

	/*
	 * Default path hierarchy for nodes is /<scope>/<qualifier>.
	 * 
	 * @see
	 * org.eclipse.core.runtime.preferences.IScopeContext#getNode(java.lang.
	 * String)
	 */
	public IEclipsePreferences getNode(String qualifier) {
		// if (qualifier == null)
		// throw new IllegalArgumentException();
		// return (IEclipsePreferences) PreferencesService.getDefault()
		// .getRootNode().node(getName()).node(qualifier);
		return null;
	}

	/*
	 * @see org.eclipse.core.runtime.preferences.IScopeContext#getLocation()
	 */
	public abstract IPath getLocation();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof IScopeContext))
			return false;
		IScopeContext other = (IScopeContext) obj;
		if (!getName().equals(other.getName()))
			return false;
		IPath location = getLocation();
		return location == null ? other.getLocation() == null : location
				.equals(other.getLocation());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getName().hashCode();
	}
}
