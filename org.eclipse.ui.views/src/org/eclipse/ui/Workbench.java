package org.eclipse.ui;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.commands.IWorkbenchCommandSupport;
import org.eclipse.ui.contexts.IWorkbenchContextSupport;
import org.eclipse.ui.help.IContext;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.testing.TestableObject;
import org.eclipse.ui.themes.IThemeManager;
import org.eclipse.ui.views.IViewRegistry;
import org.eclipse.ui.wizards.IWizardRegistry;

public class Workbench implements IWorkbench {

	public static Workbench getInstance() {
		// TODO Auto-generated method stub
		return new Workbench();
	}

	private IWorkbenchHelpSystem helpSystem;
	private IWorkbenchWindow activeWorkbenchWindow;

	public boolean isRunning() {
		return false;
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getService(Class api) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasService(Class api) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Display getDisplay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProgressService getProgressService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addWorkbenchListener(IWorkbenchListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeWorkbenchListener(IWorkbenchListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addWindowListener(IWindowListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeWindowListener(IWindowListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IWorkbenchWindow getActiveWorkbenchWindow() {
		return activeWorkbenchWindow;
	}

	@Override
	public IEditorRegistry getEditorRegistry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWorkbenchOperationSupport getOperationSupport() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPerspectiveRegistry getPerspectiveRegistry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreferenceManager getPreferenceManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISharedImages getSharedImages() {
		return ISharedImages.STUB;
	}

	@Override
	public int getWorkbenchWindowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IWorkbenchWindow[] getWorkbenchWindows() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWorkingSetManager getWorkingSetManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILocalWorkingSetManager createLocalWorkingSetManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWorkbenchWindow openWorkbenchWindow(String perspectiveId,
			IAdaptable input) throws WorkbenchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWorkbenchWindow openWorkbenchWindow(IAdaptable input)
			throws WorkbenchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean restart() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IWorkbenchPage showPerspective(String perspectiveId,
			IWorkbenchWindow window) throws WorkbenchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWorkbenchPage showPerspective(String perspectiveId,
			IWorkbenchWindow window, IAdaptable input)
			throws WorkbenchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDecoratorManager getDecoratorManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean saveAllEditors(boolean confirm) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IElementFactory getElementFactory(String factoryId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWorkbenchActivitySupport getActivitySupport() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWorkbenchCommandSupport getCommandSupport() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWorkbenchContextSupport getContextSupport() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IThemeManager getThemeManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IIntroManager getIntroManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWorkbenchHelpSystem getHelpSystem() {
		if (helpSystem == null) {
			helpSystem = new IWorkbenchHelpSystem() {

				@Override
				public boolean hasHelpUI() {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void displayHelp() {
					// TODO Auto-generated method stub

				}

				@Override
				public void displaySearch() {
					// TODO Auto-generated method stub

				}

				@Override
				public void displayDynamicHelp() {
					// TODO Auto-generated method stub

				}

				@Override
				public void search(String expression) {
					// TODO Auto-generated method stub

				}

				@Override
				public void displayContext(IContext context, int x, int y) {
					// TODO Auto-generated method stub

				}

				@Override
				public void displayHelpResource(String href) {
					// TODO Auto-generated method stub

				}

				@Override
				public void displayHelp(String contextId) {
					// TODO Auto-generated method stub

				}

				@Override
				public void displayHelp(IContext context) {
					// TODO Auto-generated method stub

				}

				@Override
				public boolean isContextHelpDisplayed() {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void setHelp(IAction action, String contextId) {
					// TODO Auto-generated method stub

				}

				@Override
				public void setHelp(Control control, String contextId) {
					// TODO Auto-generated method stub

				}

				@Override
				public void setHelp(Menu menu, String contextId) {
					// TODO Auto-generated method stub

				}

				@Override
				public void setHelp(MenuItem item, String contextId) {
					// TODO Auto-generated method stub

				}

			};
		}
		return helpSystem;
	}

	@Override
	public IWorkbenchBrowserSupport getBrowserSupport() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isStarting() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isClosing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IExtensionTracker getExtensionTracker() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IViewRegistry getViewRegistry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWizardRegistry getNewWizardRegistry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWizardRegistry getImportWizardRegistry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWizardRegistry getExportWizardRegistry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean saveAll(IShellProvider shellProvider,
			IRunnableContext runnableContext, ISaveableFilter filter,
			boolean confirm) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IShellProvider getModalDialogShellProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	public static int createAndRunWorkbench(Display display,
			WorkbenchAdvisor advisor) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static Display createDisplay() {
		// TODO Auto-generated method stub
		return null;
	}

	public static TestableObject getWorkbenchTestable() {
		// TODO Auto-generated method stub
		return null;
	}
}
