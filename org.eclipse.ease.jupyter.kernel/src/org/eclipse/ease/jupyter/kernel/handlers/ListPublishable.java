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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reference implementation of {@link IJupyterPublishable} for arrays.
 * 
 * Currently only String representation of arrays are created.
 */
public class ListPublishable implements IJupyterPublishable {
	/**
	 * Size threshold for array formatting.
	 * 
	 * If array length is greater than this, not all values are printed.
	 */
	protected static final int SIZE_THRESHOLD = 20;

	/**
	 * Temporarily holds string value.
	 */
	private final List<?> fValue;

	/**
	 * Constructor only stores parameters to member.
	 * 
	 * @param value
	 *            Value to be published as string.
	 */
	public ListPublishable(List<?> value) {
		fValue = value;
	}

	@Override
	public Map<String, Object> toMimeTypeDict() {
		Map<String, Object> data = new HashMap<String, Object>();

		String representation;

		// Check if too much data
		if (fValue.size() > SIZE_THRESHOLD) {
			representation = String.format("List[%d]", fValue.size());
		} else {
			// Otherwise create string for each element
			List<String> asList = new ArrayList<String>();
			for (Object value : fValue) {
				asList.add(value.toString());
			}
			representation = String.format("[%s]", String.join(", ", asList));
		}

		data.put("text/plain", representation);
		return data;
	}

}
