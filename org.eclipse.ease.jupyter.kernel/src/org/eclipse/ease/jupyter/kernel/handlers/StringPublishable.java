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

import java.util.HashMap;
import java.util.Map;

/**
 * Dummy implementation of {@link IJupyterPublishable} for strings.
 */
public class StringPublishable implements IJupyterPublishable {
	/**
	 * Temporarily holds string value.
	 */
	private final String fValue;

	/**
	 * Constructor only stores parameters to member.
	 * 
	 * @param value
	 *            Value to be published as string.
	 */
	public StringPublishable(String value) {
		fValue = value;
	}

	@Override
	public Map<String, Object> toMimeTypeDict() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("text/plain", fValue);
		return data;
	}

}
