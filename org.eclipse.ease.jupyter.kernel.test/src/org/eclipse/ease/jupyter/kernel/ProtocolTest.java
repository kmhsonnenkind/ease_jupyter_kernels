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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ease.jupyter.kernel.messages.Header;
import org.eclipse.ease.jupyter.kernel.messages.Message;
import org.junit.Test;

/**
 * Test cases for {@link Protocol}.
 * <p>
 * Test cases focus on correct parsing from and to ZMQ bytes.
 */
public class ProtocolTest {

	/**
	 * Default signature algorithm for message signing.
	 */
	private static final String SIGNATURE_ALGORITHM = "hmac-sha256";

	/**
	 * Default signature key for message signing.
	 */
	private static final String SIGNATURE_KEY = "a0436f6c-1916-498b-8eb9-e81ab9368e84";

	/**
	 * Default message ID for {@link Header}.
	 */
	private static final String MSG_ID = "test-id";

	/**
	 * Default message type for {@link Header}.
	 */
	private static final String MSG_TYPE = "test-type";

	/**
	 * Default date for {@link Header}.
	 */
	private static final String DATE = new Date().toString();

	/**
	 * Default session for {@link Header}.
	 */
	private static final String SESSION = "test-session";

	/**
	 * Default username for {@link Header}.
	 */
	private static final String USER_NAME = "test-username";

	/**
	 * Default version for {@link Header}.
	 */
	private static final String VERSION = Protocol.VERSION;

	/**
	 * Default dictionary key for metadata.
	 */
	private static final String METADATA_KEY = "test-key";

	/**
	 * Default dictionary value for metadata.
	 */
	private static final String METADATA_VALUE = "test-value";

	/**
	 * Default dictionary key for content.
	 */
	private static final String CONTENT_KEY = "test-content-key";

	/**
	 * Default dictionary value for content.
	 */
	private static final String CONTENT_VALUE = "test-content-value";

	/**
	 * {@link Protocol} object for en- and decoding of data.
	 */
	private static final Protocol PROTOCOL = new Protocol(SIGNATURE_KEY, SIGNATURE_ALGORITHM);

	/**
	 * Creates a new {@link Header} object with default values from members.
	 * 
	 * @return new {@link Header} with default values from members.
	 */
	private static Header defaultHeader() {
		return new Header().withMsgId(MSG_ID).withMsgType(MSG_TYPE).withDate(DATE).withSession(SESSION)
				.withUsername(USER_NAME).withVersion(VERSION);
	}

	/**
	 * Creates new metadata dictionary with default values from members.
	 * 
	 * @return new metadata dictionary with default values from members.
	 */
	private static Map<String, Object> defaultMetadata() {
		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put(METADATA_KEY, METADATA_VALUE);
		return metadata;
	}

	/**
	 * Creates new content dictionary with default values from members.
	 * 
	 * @return new content dictionary with default values from members.
	 */
	private static Map<String, Object> defaultContent() {
		Map<String, Object> content = new HashMap<String, Object>();
		content.put(CONTENT_KEY, CONTENT_VALUE);
		return content;
	}

	/**
	 * Converts a given {@link Message} to byte[] frames using
	 * {@link Protocol#toFrames(Message)} and creates new {@link Message} object
	 * from said frames using {@link Protocol#fromFrames(List)}.
	 * 
	 * @param original
	 *            {@link Message} to be converted back and forth.
	 * @return {@link Message} created from temporary byte[] frames.
	 * @throws IOException
	 *             If message could not be en- or decoded.
	 */
	private static Message convert(Message original) throws IOException {
		List<byte[]> frames = PROTOCOL.toFrames(original);
		return PROTOCOL.fromFrames(frames);
	}

	/**
	 * Compares two {@link Header} objects for equality using assertions.
	 * 
	 * @param first
	 *            First {@link Header} for comparison.
	 * @param second
	 *            Second {@link Header} for comparison.
	 */
	private static void compare(Header first, Header second) {
		// Check that header data stayed the same
		assertEquals(first.getMsgId(), second.getMsgId());
		assertEquals(first.getMsgType(), second.getMsgType());
		assertEquals(first.getDate(), second.getDate());
		assertEquals(first.getSession(), second.getSession());
		assertEquals(first.getUsername(), second.getUsername());
		assertEquals(first.getVersion(), second.getVersion());
	}

	/**
	 * Compares two metadata objects for equality using assertions.
	 * 
	 * @param first
	 *            First metadata object for comparison.
	 * @param second
	 *            Second metadata object for comparison.
	 */
	private static void compare(Map<String, Object> first, Map<String, Object> second) {
		// Check that metadata stayed the same
		for (String key : first.keySet()) {
			assertEquals(first.get(key), second.get(key));
		}
		for (String key : second.keySet()) {
			assertEquals(first.get(key), second.get(key));
		}

	}

	/**
	 * Checks that the given {@link Header} object contains the default values
	 * from {@link #defaultHeader()}.
	 * 
	 * @param header
	 *            {@link Header} to be checked for default values.
	 */
	private static void hasDefaultValues(Header header) {
		// Check that header data has default values
		assertEquals(MSG_ID, header.getMsgId());
		assertEquals(MSG_TYPE, header.getMsgType());
		assertEquals(DATE, header.getDate());
		assertEquals(SESSION, header.getSession());
		assertEquals(USER_NAME, header.getUsername());
		assertEquals(VERSION, header.getVersion());
	}

	/**
	 * Overload for {@link #hasDefaultValues(Map, boolean)} for metadata.
	 * 
	 * @param metadata
	 *            Metadata dictionary to be checked.
	 */
	private static void hasDefaultValues(Map<String, Object> metadata) {
		hasDefaultValues(metadata, false);
	}

	/**
	 * Checks that the given dictionary object contains the default values from
	 * either {@link #defaultMetadata()} or {@link #defaultContent()}.
	 * 
	 * @param data
	 *            data object to be checked for default values.
	 */
	private static void hasDefaultValues(Map<String, Object> data, boolean checkContent) {
		if (checkContent) {
			// Check that content has default values
			assertEquals(1, data.keySet().size());
			assertEquals(CONTENT_VALUE, data.get(CONTENT_KEY));
		} else {
			// Check that metadata has default values
			assertEquals(1, data.keySet().size());
			assertEquals(METADATA_VALUE, data.get(METADATA_KEY));
		}

	}

	/**
	 * Tests that {@link Header} information is correctly parsed to and back
	 * from byte frames for {@link Message#getHeader()}.
	 * <p>
	 * First creates {@link Header}, then encodes it to List<byte[]> using
	 * {@link Protocol#toFrames(Message)}, then decodes it back using
	 * {@link Protocol#fromFrames(List)} and finally asserts that the data
	 * stayed the same.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test
	public void testHeaderParsing() throws Exception {
		// Create original data
		Header originalHeader = defaultHeader();

		// Parse to byte[] and then back
		Header convertedHeader = convert(new Message().withHeader(originalHeader)).getHeader();

		// Check that data stayed the same
		compare(originalHeader, convertedHeader);
		hasDefaultValues(convertedHeader);
	}

	/**
	 * Tests that {@link Header} information is correctly parsed to and back
	 * from byte frames for {@link Message#getParentHeader()}.
	 * <p>
	 * First creates {@link Header}, then encodes it to List<byte[]> using
	 * {@link Protocol#toFrames(Message)}, then decodes it back using
	 * {@link Protocol#fromFrames(List)} and finally asserts that the data
	 * stayed the same.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test
	public void testParentHeaderParsing() throws Exception {
		// Create original data
		Header originalParentHeader = defaultHeader();
		Header originalHeader = defaultHeader();

		// Parse to byte[] and then back
		Header convertedParentHeader = convert(
				new Message().withHeader(originalHeader).withParentHeader(originalParentHeader)).getParentHeader();

		// Check that data stayed the same
		compare(originalParentHeader, convertedParentHeader);
		hasDefaultValues(convertedParentHeader);
	}

	/**
	 * Tests that metadata information is correctly parsed to and back from byte
	 * frames for {@link Message#getMetadata()}.
	 * <p>
	 * First creates metadata, then encodes it to List<byte[]> using
	 * {@link Protocol#toFrames(Message)}, then decodes it back using
	 * {@link Protocol#fromFrames(List)} and finally asserts that the data
	 * stayed the same.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test
	public void testMetadata() throws Exception {
		// Create original data
		Header originalHeader = defaultHeader();
		Map<String, Object> originalMetadata = defaultMetadata();

		// Parse to byte[] and then back
		@SuppressWarnings("unchecked")
		Map<String, Object> convertedMetadata = (Map<String, Object>) convert(
				new Message().withHeader(originalHeader).withMetadata(originalMetadata)).getMetadata();

		// Check that data stayed the same
		compare(originalMetadata, convertedMetadata);
		hasDefaultValues(convertedMetadata);
	}

	/**
	 * Tests that content information is correctly parsed to and back from byte
	 * frames for {@link Message#getMetadata()}.
	 * <p>
	 * First creates content, then encodes it to List<byte[]> using
	 * {@link Protocol#toFrames(Message)}, then decodes it back using
	 * {@link Protocol#fromFrames(List)} and finally asserts that the data
	 * stayed the same.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test
	public void testContentDictionary() throws Exception {
		// Create original data
		Header originalHeader = defaultHeader();
		Map<String, Object> originalContent = defaultContent();

		// Parse to byte[] and then back
		@SuppressWarnings("unchecked")
		Map<String, Object> convertedContent = (Map<String, Object>) convert(
				new Message().withHeader(originalHeader).withContent(originalContent)).getContent();

		// Check that data stayed the same
		compare(originalContent, convertedContent);
		hasDefaultValues(convertedContent, true);
	}
}