package org.eclipse.pde.ui.tests.classpathupdater;

import static org.junit.Assert.assertTrue;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.*;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.internal.ui.wizards.tools.UpdateClasspathJob;
import org.eclipse.pde.ui.tests.util.ProjectUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class ClasspathUpdaterTest {

	private static IProject project;

	/**
	 * The project name and bundle symbolic name of the test project
	 */
	public static final String bundleName = "classpathupdater";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		project = ProjectUtils.importTestProject("tests/projects/" + bundleName);
	}

	@Test
	public void testUpdateClasspath_PreserveAttributes() throws Exception {
		assertTrue("Project was not created", project.exists());
		IJavaProject javaPrj = JavaCore.create(project);
		final IClasspathEntry[] originalClasspath = javaPrj.getRawClasspath();
		try {
			assertTrue("Update Classpath Job failed", runUpdateClasspathJob().isOK());
			checkClasspathAttribute("JavaSE-17", IClasspathAttribute.MODULE, "true", Boolean::valueOf);
			checkClasspathAttribute("library.jar", IClasspathAttribute.TEST, "true", Boolean::valueOf);
			checkClasspathProperty("library.jar", "exported=true", (e) -> e.isExported());
			checkClasspathAttribute("A.jar", IClasspathAttribute.TEST, null, Boolean::valueOf);
			checkClasspathProperty("A.jar", "exported=false", (e) -> !e.isExported());
		} finally {
			javaPrj.setRawClasspath(originalClasspath, null);
		}
	}

	@Test
	public void testUpdateClasspath_CreateFromScratch() throws Exception {
		assertTrue("Project was not created", project.exists());
		IJavaProject javaPrj = JavaCore.create(project);
		final IClasspathEntry[] originalClasspath = javaPrj.getRawClasspath();
		try {
			javaPrj.setRawClasspath(new IClasspathEntry[0], null);
			assertTrue("Update Classpath Job failed", runUpdateClasspathJob().isOK());
			checkClasspathAttribute("JavaSE-17", IClasspathAttribute.MODULE, null, Boolean::valueOf);
			checkClasspathAttribute("library.jar", IClasspathAttribute.TEST, null, Boolean::valueOf);
			checkClasspathProperty("library.jar", "exported=false", (e) -> !e.isExported());
			checkClasspathAttribute("A.jar", IClasspathAttribute.TEST, null, Boolean::valueOf);
			checkClasspathProperty("A.jar", "exported=false", (e) -> !e.isExported());
		} finally {
			javaPrj.setRawClasspath(originalClasspath, null);
		}
	}

	private IStatus runUpdateClasspathJob() throws InterruptedException {
		IPluginModelBase model = PluginRegistry.findModel(project.getProject());
		UpdateClasspathJob job = new UpdateClasspathJob(new IPluginModelBase[] { model });
		job.schedule();
		job.join();
		return job.getResult();
	}

	private void checkClasspathAttribute(String entryName, String attrName, String expectedValue,
			Function<String, Object> parser) throws JavaModelException {
		IClasspathEntry entry = findClasspathEntry(entryName);
		assertTrue("Classpath entry for " + entryName + " is missing", entry != null);
		checkClasspathAttributeValue(entry, attrName, parser, expectedValue);
	}

	private void checkClasspathAttributeValue(IClasspathEntry entry, String attrName, Function<String, Object> parser,
			String expectedValue) {
		String title = "Classpath entry for '" + entry.getPath().lastSegment() + "': attribute '" + attrName + "'";
		String attrValue = findClasspathAttributeValue(entry, attrName);
		Object current = parser.apply(attrValue); // null: attribute not set
		Object expected = parser.apply(expectedValue);
		assertTrue(title + " is not '" + Objects.toString(expected) + "' any more", Objects.equals(current, expected));
	}

	private String findClasspathAttributeValue(IClasspathEntry entry, String attrName) {
		for (IClasspathAttribute attr : entry.getExtraAttributes()) {
			if (attrName.equals(attr.getName())) {
				return attr.getValue();
			}
		}
		return null;
	}

	private void checkClasspathProperty(String entryName, String expectedValue, Predicate<IClasspathEntry> checker)
			throws JavaModelException {
		String title = "Classpath entry for '" + entryName + "'";
		IClasspathEntry entry = findClasspathEntry(entryName);
		assertTrue(title + " is missing", entry != null);
		assertTrue(title + " has not set '" + expectedValue + "' any more", checker.test(entry));
	}

	private IClasspathEntry findClasspathEntry(String entryName) throws JavaModelException {
		IJavaProject javaPrj = JavaCore.create(project);
		IClasspathEntry[] rawClasspath = javaPrj.getRawClasspath();
		for (IClasspathEntry entry : rawClasspath) {
			if (entryName.equals(entry.getPath().lastSegment())) {
				return entry;
			}
		}
		return null;
	}
}
