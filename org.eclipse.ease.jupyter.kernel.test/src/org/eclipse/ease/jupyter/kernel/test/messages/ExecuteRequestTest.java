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

package org.eclipse.ease.jupyter.kernel.test.messages;

import static org.junit.Assert.*;

import org.eclipse.ease.jupyter.kernel.messages.ExecuteRequest;
import org.eclipse.ease.jupyter.kernel.util.Utility;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test cases for {@link ExecuteRequest}.
 * <p>
 * Test cases focus on correct parsing from JSON data.
 */
public class ExecuteRequestTest {
	/**
	 * Default value for code portion of {@link ExecuteRequest}.
	 */
	private static final String CODE = "code";

	/**
	 * Tests that values from JSON data are parsed to the correct members.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test
	public void testExecuteRequestParserFull() throws Exception {
		// Load content from resource
		String content = Utility.loadResource("/messages/execute/full_request.json");

		// Create request
		ExecuteRequest request = new ObjectMapper().readValue(content, ExecuteRequest.class);
		request.validate();

		// Check for default values
		assertEquals(CODE, request.getCode());
		assertFalse(request.getSilent());
		assertFalse(request.getStoreHistory());
		assertTrue(request.getAllowStdin());
		assertFalse(request.getStopOnError());
	}

	/**
	 * Tests that default values for JSON data are correctly loaded.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test
	public void testExecuteRequestParserMinimal() throws Exception {
		// Load content from resource
		String content = Utility.loadResource("/messages/execute/minimal_request.json");

		// Create request
		ExecuteRequest request = new ObjectMapper().readValue(content, ExecuteRequest.class);
		request.validate();

		// Check for default values
		assertEquals(CODE, request.getCode());
		assertFalse(request.getSilent());
		assertTrue(request.getStoreHistory());
		assertTrue(request.getAllowStdin());
		assertFalse(request.getStopOnError());
	}

	/**
	 * Tests that required values from JSON data are detected correctly.
	 * <p>
	 * Input file does not contain required "code" value and parser therefore
	 * needs to throw {@link JsonMappingException}.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test(expected = JsonMappingException.class)
	public void testExecuteRequestParserInvalid() throws Exception {
		// Load content from resource
		String content = Utility.loadResource("/messages/execute/invalid_request.json");

		// Create request
		ExecuteRequest request = new ObjectMapper().readValue(content, ExecuteRequest.class);
		request.validate();

		fail("Parser did not throw Exception.");
	}

}