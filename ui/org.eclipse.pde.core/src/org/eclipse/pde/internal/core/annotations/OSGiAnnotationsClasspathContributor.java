/*******************************************************************************
 * Copyright (c) 2022 Christoph Läubrich and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.annotations;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.pde.core.IClasspathContributor;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.internal.core.ClasspathUtilCore;
import org.osgi.resource.Resource;

/**
 * This makes the <a href=
 * "https://docs.osgi.org/specification/osgi.core/7.0.0/framework.api.html#org.osgi.annotation.bundle">OSGi
 * Bundle Annotations</a> and <a href=
 * "https://docs.osgi.org/specification/osgi.core/7.0.0/framework.api.html#org.osgi.annotation.versioning">OSGi
 * Versioning Annotations</a> available to plugin projects if they are part of
 * the target platform.
 */
public class OSGiAnnotationsClasspathContributor implements IClasspathContributor {

	private static final Collection<String> OSGI_ANNOTATIONS = List.of("org.osgi.annotation.versioning", //$NON-NLS-1$
			"org.osgi.annotation.bundle", "org.osgi.service.component.annotations", //$NON-NLS-1$ //$NON-NLS-2$
			"org.osgi.service.metatype.annotations"); //$NON-NLS-1$

	@Override
	public List<IClasspathEntry> getInitialEntries(BundleDescription project) {
		IPluginModelBase projectModel = PluginRegistry.findModel((Resource) project);
		if (projectModel != null) {
			return ClasspathUtilCore
					.classpathEntries(annotations().filter(model -> !model.equals(projectModel)))
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	/**
	 * @return s stream of all current available annotations in the current
	 *         plugin registry
	 */
	public static Stream<IPluginModelBase> annotations() {
		return OSGI_ANNOTATIONS.stream().map(PluginRegistry::findModel).filter(Objects::nonNull)
				.filter(IPluginModelBase::isEnabled);
	}

	@Override
	public List<IClasspathEntry> getEntriesForDependency(BundleDescription project, BundleDescription addedDependency) {
		return Collections.emptyList();
	}

}
