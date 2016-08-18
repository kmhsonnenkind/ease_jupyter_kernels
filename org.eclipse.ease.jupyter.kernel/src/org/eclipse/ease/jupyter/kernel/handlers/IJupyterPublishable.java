package org.eclipse.ease.jupyter.kernel.handlers;

import java.util.Map;

import org.eclipse.ease.jupyter.kernel.channels.IOPubChannel;

/**
 * Interface for data that can be published over Jupyter {@link IOPubChannel}.
 * 
 * Offers methods to turn data into a dictionary of different representations.
 * 
 * @author kloeschmartin
 *
 */
public interface IJupyterPublishable {
	/**
	 * Convert the value of the object to a dictionary from MIME type to the
	 * corresponding value.
	 * 
	 * To be compliant with most clients it is highly recommended to at least
	 * have a key for "text/plain".
	 * 
	 * @return Different representations as MIME type dictionary.
	 */
	Map<String, Object> toMimeTypeDict();
}
