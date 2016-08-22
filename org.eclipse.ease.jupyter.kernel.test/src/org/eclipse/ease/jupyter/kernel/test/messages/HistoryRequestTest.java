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

import org.eclipse.ease.jupyter.kernel.messages.HistoryRequest;
import org.eclipse.ease.jupyter.kernel.messages.HistoryRequest.HistAccessType;
import org.eclipse.ease.jupyter.kernel.util.Utility;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test cases for {@link HistoryRequest}.
 * <p>
 * Test cases focus on correct parsing from JSON data.
 */
public class HistoryRequestTest {
	/**
	 * Default value for pattern portion of {@link HistoryRequest}.
	 */
	private static final String PATTERN = "pattern";

	/**
	 * Tests that values from JSON data are parsed to the correct members.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test
	public void testHistoryRequestParserFull() throws Exception {
		// Load content from resource
		String content = Utility.loadResource("/messages/history/full_request.json");

		// Create request
		HistoryRequest request = new ObjectMapper().readValue(content, HistoryRequest.class);
		request.validate();

		// Check for values
		assertTrue(request.getOutput());
		assertFalse(request.getRaw());
		assertEquals(HistAccessType.RANGE, request.getHistAccessType());
		assertEquals(new Integer(1), request.getSession());
		assertEquals(new Integer(2), request.getStart());
		assertEquals(new Integer(3), request.getStop());
		assertEquals(new Integer(4), request.getN());
		assertEquals(PATTERN, request.getPattern());
		assertFalse(request.getUnique());
	}

	/**
	 * Tests that default values for JSON data are correctly loaded.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test
	public void testHistoryRequestMinimal() throws Exception {
		// Load content from resource
		String content = Utility.loadResource("/messages/history/minimal_request.json");

		// Create request
		HistoryRequest request = null;
		try {
			request = new ObjectMapper().readValue(content, HistoryRequest.class);
			request.validate();
		} catch (JsonMappingException e) {
			fail("Threw mapping exception although all required values should be present.");
		}

		// Check for default values
		assertTrue(request.getOutput());
		assertFalse(request.getRaw());
		assertEquals(HistAccessType.RANGE, request.getHistAccessType());
		assertNull(request.getSession());
		assertNull(request.getStart());
		assertNull(request.getStop());
		assertNull(request.getN());
		assertNull(request.getPattern());
		assertFalse(request.getUnique());

	}

	/**
	 * Tests that enum value for {@link HistAccessType} is parsed correctly.
	 * 
	 * @throws Exception
	 *             in case of error.
	 */
	@Test
	public void testHistAccessTypeRange() throws Exception {
		// Load content from resource
		String content = Utility.loadResource("/messages/history/range_request.json");

		// Create request
		HistoryRequest request = new ObjectMapper().readValue(content, HistoryRequest.class);
		request.validate();

		// Check that enum was parsed correctly
		assertEquals(HistAccessType.RANGE, request.getHistAccessType());
	}

	/**
	 * Tests that enum value for {@link HistAccessType} is parsed correctly.
	 * 
	 * @throws Exception
	 *             in case of error.
	 */
	@Test
	public void testHistAccessTypeTail() throws Exception {
		// Load content from resource
		String content = Utility.loadResource("/messages/history/tail_request.json");

		// Create request
		HistoryRequest request = new ObjectMapper().readValue(content, HistoryRequest.class);
		request.validate();

		// Check that enum was parsed correctly
		assertEquals(HistAccessType.TAIL, request.getHistAccessType());
	}

	/**
	 * Tests that enum value for {@link HistAccessType} is parsed correctly.
	 * 
	 * @throws Exception
	 *             in case of error.
	 */
	@Test
	public void testHistAccessTypeSearch() throws Exception {
		// Load content from resource
		String content = Utility.loadResource("/messages/history/search_request.json");

		// Create request
		HistoryRequest request = new ObjectMapper().readValue(content, HistoryRequest.class);
		request.validate();

		// Check that enum was parsed correctly
		assertEquals(HistAccessType.SEARCH, request.getHistAccessType());
	}

	/**
	 * Tests that invalid enum value for {@link HistAccessType} is not parsed.
	 * <p>
	 * Input file contains invalid value for hist_access_type and parser
	 * therefore needs to throw {@link JsonMappingException}.
	 * 
	 * @throws Exception
	 *             in case of error.
	 */
	@Test(expected = JsonMappingException.class)
	public void testHistAccessTypeInvalid() throws Exception {
		// Load content from resource
		String content = Utility.loadResource("/messages/history/invalid_histaccesstype.json");

		// Create request
		HistoryRequest request = new ObjectMapper().readValue(content, HistoryRequest.class);
		request.validate();

		fail("Did not throw Exception although invalid hist_access_type given.");
	}

	/**
	 * Tests that required values from JSON data are detected correctly.
	 * <p>
	 * Input file does not contain required "output" value and parser therefore
	 * needs to throw {@link JsonMappingException}.
	 *
	 * @throws Exception
	 *             In case of error.
	 */
	@Test(expected = JsonMappingException.class)
	public void testNoOutput() throws Exception {
		// Load content from resource
		String content = Utility.loadResource("/messages/history/no_output.json");

		// Create request
		HistoryRequest request = new ObjectMapper().readValue(content, HistoryRequest.class);
		request.validate();

		fail("Did not throw Exception although parameter 'output' missing.");
	}

	/**
	 * Tests that required values from JSON data are detected correctly.
	 * <p>
	 * Input file does not contain required "raw" value and parser therefore
	 * needs to throw {@link JsonMappingException}.
	 *
	 * @throws Exception
	 *             In case of error.
	 */
	@Test(expected = JsonMappingException.class)
	public void testNoRaw() throws Exception {
		// Load content from resource
		String content = Utility.loadResource("/messages/history/no_raw.json");

		// Create request
		HistoryRequest request = new ObjectMapper().readValue(content, HistoryRequest.class);
		request.validate();

		fail("Did not throw Exception although parameter 'output' missing.");
	}

	/**
	 * Tests that required values from JSON data are detected correctly.
	 * <p>
	 * Input file does not contain required "hist_access_type" value and parser
	 * therefore needs to throw {@link JsonMappingException}.
	 *
	 * @throws Exception
	 *             In case of error.
	 */
	@Test(expected = JsonMappingException.class)
	public void testNoHistAccessType() throws Exception {
		// Load content from resource
		String content = Utility.loadResource("/messages/history/no_histaccesstype.json");

		// Create request
		HistoryRequest request = new ObjectMapper().readValue(content, HistoryRequest.class);
		request.validate();

		fail("Did not throw Exception although parameter 'output' missing.");
	}
}