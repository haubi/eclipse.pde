/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.iproduct;


public interface IProductModelFactory {
	
	IProduct createProduct();
	
	IAboutInfo createAboutInfo();
	
	IProductPlugin createPlugin();
	
	IProductFeature createFeature();
	
	IConfigurationFileInfo createConfigFileInfo();
	
	IWindowImages createWindowImages();
	
	ISplashInfo createSplashInfo();
	
	ILauncherInfo createLauncherInfo();
	
	IArgumentsInfo createLauncherArguments();
	
	IIntroInfo createIntroInfo();
	
	IJVMInfo createJVMInfo();
	
}
