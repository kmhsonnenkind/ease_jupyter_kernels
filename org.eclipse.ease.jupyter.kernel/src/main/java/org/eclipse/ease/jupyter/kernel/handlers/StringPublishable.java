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
