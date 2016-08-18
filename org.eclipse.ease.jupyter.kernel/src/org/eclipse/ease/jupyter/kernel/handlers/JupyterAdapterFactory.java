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
package org.eclipse.ease.jupyter.kernel.handlers;

import org.eclipse.core.runtime.IAdapterFactory;

public class JupyterAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adapterType.equals(IJupyterPublishable.class)) {
			if (adaptableObject instanceof Object[]) {
				return (T) new ArrayPublishable((Object[]) adaptableObject);
			}
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class[] { Object[].class };
	}

}
