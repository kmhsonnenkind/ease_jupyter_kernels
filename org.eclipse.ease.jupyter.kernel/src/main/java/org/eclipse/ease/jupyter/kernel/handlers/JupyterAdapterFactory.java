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
