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

package org.eclipse.ease.jupyter.kernel;

import static org.junit.Assert.*;

import java.io.IOException;

import org.eclipse.ease.jupyter.kernel.Config;
import org.eclipse.ease.jupyter.kernel.Dispatcher;
import org.eclipse.ease.jupyter.kernel.util.Utility;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Test cases for {@link Dispatcher}.
 * <p>
 * Test cases focus on correct parsing of received connection files.
 */
public class DispatcherTest {

	/**
	 * Tests that config file is parsed correctly.
	 * <p>
	 * Loads config content based on /resources/valid_connection.json. this file
	 * is based on the values from the <a href=
	 * "https://ipython.org/ipython-doc/3/development/kernels.html#connection-files">Jupyter
	 * documentation</a>.
	 * 
	 * @throws Exception
	 *             If file could not be parsed.
	 */
	@Test
	public void testValidConnectionFile() throws Exception {
		// Load connection file
		byte[] validContent = Utility.loadResource("/dispatcher/valid_connection.json").getBytes();

		// Parse config
		Config config = null;
		try {
			config = Dispatcher.parseConfig(validContent);
		} catch (IOException e) {
			fail("Could not parse valid connection file.");
		}

		// Assert that data was parsed
		assertNotNull(config);

		// Assert that data was parsed to correct member.
		assertEquals(new Integer(50160), config.getControlPort());
		assertEquals(new Integer(57503), config.getShellPort());
		assertEquals("tcp", config.getTransport());
		assertEquals("hmac-sha256", config.getSignatureScheme());
		assertEquals(new Integer(52597), config.getStdinPort());
		assertEquals(new Integer(42540), config.getHbPort());
		assertEquals("127.0.0.1", config.getIp());
		assertEquals(new Integer(40885), config.getIopubPort());
		assertEquals("a0436f6c-1916-498b-8eb9-e81ab9368e84", config.getKey());
	}

	/**
	 * Tests that {@link Dispatcher} does not parse empty file.
	 * <p>
	 * Test asserts that {@link IOException} is being thrown and
	 * {@link Dispatcher} will not return invalid content.
	 * 
	 * @throws IOException
	 *             Expected exception to occur.
	 */
	@Test(expected = IOException.class)
	public void testEmptyConnectionFile() throws IOException {
		byte[] emptyContent = new byte[0];
		Dispatcher.parseConfig(emptyContent);
		fail("Config was parsed without throwing exception.");
	}

	/**
	 * Tests that {@link Dispatcher} does not parse file with invalid JSON
	 * content.
	 * <p>
	 * Test asserts that {@link JsonParseException} is being thrown and
	 * {@link Dispatcher} will not return invalid content.
	 * 
	 * @throws Exception
	 *             If any unexpected error occurred.
	 */
	@Test
	public void testInvalidJsonConnectionFile() throws Exception {
		byte[] invalidContent = Utility.loadResource("/dispatcher/invalid_connection.json").getBytes();

		try {
			Dispatcher.parseConfig(invalidContent);
			fail("Dispatcher did not throw Exception although invalid data received.");
		} catch (JsonParseException e) {

		} catch (IOException e) {
			fail("Dispatcher threw wrong exception");
		}
	}

	/**
	 * Tests that {@link Dispatcher} does not parse file with missing data.
	 * <p>
	 * Test asserts that {@link JsonMappingException} is being thrown and
	 * {@link Dispatcher} will not return invalid content.
	 * 
	 * @throws Exception
	 *             If any unexpected error occurred.
	 */
	@Test
	public void testInvalidConnectionFileNoControl() throws Exception {
		byte[] invalidContent = Utility.loadResource("/dispatcher/invalid_connection_no_control.json").getBytes();
		try {
			Dispatcher.parseConfig(invalidContent);
			fail("Dispatcher did not throw Exception although invalid data received.");
		} catch (JsonMappingException e) {

		} catch (IOException e) {
			fail("Dispatcher threw wrong exception.");
		}
	}

	/**
	 * Tests that {@link Dispatcher} does not parse file with missing data.
	 * <p>
	 * Test asserts that {@link JsonMappingException} is being thrown and
	 * {@link Dispatcher} will not return invalid content.
	 * 
	 * @throws Exception
	 *             If any unexpected error occurred.
	 */
	@Test
	public void testInvalidConnectionFileNoShell() throws Exception {
		byte[] invalidContent = Utility.loadResource("/dispatcher/invalid_connection_no_shell.json").getBytes();
		try {
			Dispatcher.parseConfig(invalidContent);
			fail("Dispatcher did not throw Exception although invalid data received.");
		} catch (JsonMappingException e) {

		} catch (IOException e) {
			fail("Dispatcher threw wrong exception.");
		}
	}

	/**
	 * Tests that {@link Dispatcher} does not parse file with missing data.
	 * <p>
	 * Test asserts that {@link JsonMappingException} is being thrown and
	 * {@link Dispatcher} will not return invalid content.
	 * 
	 * @throws Exception
	 *             If any unexpected error occurred.
	 */
	@Test
	public void testInvalidConnectionFileNoTransport() throws Exception {
		byte[] invalidContent = Utility.loadResource("/dispatcher/invalid_connection_no_transport.json").getBytes();
		try {
			Dispatcher.parseConfig(invalidContent);
			fail("Dispatcher did not throw Exception although invalid data received.");
		} catch (JsonMappingException e) {

		} catch (IOException e) {
			fail("Dispatcher threw wrong exception.");
		}
	}

	/**
	 * Tests that {@link Dispatcher} does not parse file with missing data.
	 * <p>
	 * Test asserts that {@link JsonMappingException} is being thrown and
	 * {@link Dispatcher} will not return invalid content.
	 * 
	 * @throws Exception
	 *             If any unexpected error occurred.
	 */
	@Test
	public void testInvalidConnectionFileNoSignature() throws Exception {
		byte[] invalidContent = Utility.loadResource("/dispatcher/invalid_connection_no_signature.json").getBytes();
		try {
			Dispatcher.parseConfig(invalidContent);
			fail("Dispatcher did not throw Exception although invalid data received.");
		} catch (JsonMappingException e) {

		} catch (IOException e) {
			fail("Dispatcher threw wrong exception.");
		}
	}

	/**
	 * Tests that {@link Dispatcher} does not parse file with missing data.
	 * <p>
	 * Test asserts that {@link JsonMappingException} is being thrown and
	 * {@link Dispatcher} will not return invalid content.
	 * 
	 * @throws Exception
	 *             If any unexpected error occurred.
	 */
	@Test
	public void testInvalidConnectionFileNoStdin() throws Exception {
		byte[] invalidContent = Utility.loadResource("/dispatcher/invalid_connection_no_stdin.json").getBytes();
		try {
			Dispatcher.parseConfig(invalidContent);
			fail("Dispatcher did not throw Exception although invalid data received.");
		} catch (JsonMappingException e) {

		} catch (IOException e) {
			fail("Dispatcher threw wrong exception.");
		}
	}

	/**
	 * Tests that {@link Dispatcher} does not parse file with missing data.
	 * <p>
	 * Test asserts that {@link JsonMappingException} is being thrown and
	 * {@link Dispatcher} will not return invalid content.
	 * 
	 * @throws Exception
	 *             If any unexpected error occurred.
	 */
	@Test
	public void testInvalidConnectionFileNoHb() throws Exception {
		byte[] invalidContent = Utility.loadResource("/dispatcher/invalid_connection_no_hb.json").getBytes();
		try {
			Dispatcher.parseConfig(invalidContent);
			fail("Dispatcher did not throw Exception although invalid data received.");
		} catch (JsonMappingException e) {

		} catch (IOException e) {
			fail("Dispatcher threw wrong exception.");
		}
	}

	/**
	 * Tests that {@link Dispatcher} does not parse file with missing data.
	 * <p>
	 * Test asserts that {@link JsonMappingException} is being thrown and
	 * {@link Dispatcher} will not return invalid content.
	 * 
	 * @throws Exception
	 *             If any unexpected error occurred.
	 */
	@Test
	public void testInvalidConnectionFileNoIp() throws Exception {
		byte[] invalidContent = Utility.loadResource("/dispatcher/invalid_connection_no_ip.json").getBytes();
		try {
			Dispatcher.parseConfig(invalidContent);
			fail("Dispatcher did not throw Exception although invalid data received.");
		} catch (JsonMappingException e) {

		} catch (IOException e) {
			fail("Dispatcher threw wrong exception.");
		}
	}

	/**
	 * Tests that {@link Dispatcher} does not parse file with missing data.
	 * <p>
	 * Test asserts that {@link JsonMappingException} is being thrown and
	 * {@link Dispatcher} will not return invalid content.
	 * 
	 * @throws Exception
	 *             If any unexpected error occurred.
	 */
	@Test
	public void testInvalidConnectionFileNoIoPub() throws Exception {
		byte[] invalidContent = Utility.loadResource("/dispatcher/invalid_connection_no_iopub.json").getBytes();
		try {
			Dispatcher.parseConfig(invalidContent);
			fail("Dispatcher did not throw Exception although invalid data received.");
		} catch (JsonMappingException e) {

		} catch (IOException e) {
			fail("Dispatcher threw wrong exception.");
		}
	}

	/**
	 * Tests that {@link Dispatcher} does not parse file with missing data.
	 * <p>
	 * Test asserts that {@link JsonMappingException} is being thrown and
	 * {@link Dispatcher} will not return invalid content.
	 * 
	 * @throws Exception
	 *             If any unexpected error occurred.
	 */
	@Test
	public void testInvalidConnectionFileNoKey() throws Exception {
		byte[] invalidContent = Utility.loadResource("/dispatcher/invalid_connection_no_key.json").getBytes();
		try {
			Dispatcher.parseConfig(invalidContent);
			fail("Dispatcher did not throw Exception although invalid data received.");
		} catch (JsonMappingException e) {

		} catch (IOException e) {
			fail("Dispatcher threw wrong exception.");
		}
	}
}