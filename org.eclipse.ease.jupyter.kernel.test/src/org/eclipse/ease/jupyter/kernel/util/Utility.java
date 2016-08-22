/*******************************************************************************
 * Copyright (c) 2016 Martin Kloesch and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Kloesch - initial API and implementation
 *******************************************************************************/

package org.eclipse.ease.jupyter.kernel.util;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

/**
 * Utility class for different tasks in test classes.
 */
public class Utility {
	/**
	 * Loads a given resource as a string.
	 * 
	 * @param resource
	 *            Path to resource to be loaded.
	 * @return Resource data as String.
	 * @throws IOException
	 *             If resource could not be loaded.
	 */
	public static String loadResource(String resource) throws IOException {
		return IOUtils.toString(Utility.class.getResourceAsStream(resource), "UTF-8");
	}
}
