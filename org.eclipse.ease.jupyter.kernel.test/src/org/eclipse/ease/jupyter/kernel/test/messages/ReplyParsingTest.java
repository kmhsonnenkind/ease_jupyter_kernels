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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.AbstractDocument.Content;

import org.eclipse.ease.jupyter.kernel.messages.ExecuteInput;
import org.eclipse.ease.jupyter.kernel.messages.ExecuteResult;
import org.eclipse.ease.jupyter.kernel.messages.HelpLink;
import org.eclipse.ease.jupyter.kernel.messages.HistoryReply;
import org.eclipse.ease.jupyter.kernel.messages.IsCompleteReply;
import org.eclipse.ease.jupyter.kernel.messages.KernelInfoReply;
import org.eclipse.ease.jupyter.kernel.messages.LanguageInfo;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test cases for parsing different {@link Content} objects to and from byte[].
 */
public class ReplyParsingTest {
	/**
	 * Constant used for all "code" nodes in JSON contents.
	 */
	private static final String CODE = "code";

	/**
	 * Constant used for all "execution_count" nodes in JSON contents.
	 */
	private static final Integer EXECUTION_COUNT = new Integer(1);

	/**
	 * {@link ObjectMapper} for parsing data from and to byte[].
	 */
	private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

	/**
	 * Constant used as single key for all "data" nodes in JSON contents.
	 */
	private static final String DATA_KEY = "data-key";

	/**
	 * Constant used as single value for all "data" nodes in JSON contents.
	 */
	private static final String DATA_VALUE = "data-value";

	/**
	 * Constant used as single key for all "metadata" nodes in JSON contents.
	 */
	private static final String METADATA_KEY = "metadata-key";

	/**
	 * Constant used as single value for all "data" nodes in JSON contents.
	 */
	private static final String METADATA_VALUE = "metadata-value";

	/**
	 * Constant used as single entry in all "history" JSON contents.
	 */
	private static final String HISTORY_ENTRY = "history entry.";

	/**
	 * Constant used for all "status" nodes in JSON contents.
	 */
	private static final IsCompleteReply.Status STATUS = IsCompleteReply.Status.COMPLETE;

	/**
	 * Constant used for all "indent" nodes in JSON contents.
	 */
	private static final String INDENT = "indent";

	/**
	 * Constant used for all "protocol_version" nodes in JSON contents.
	 */
	private static final String PROTOCOL_VERSION = "protocol-version";

	/**
	 * Constant used for all "implementation" nodes in JSON contents.
	 */
	private static final String IMPLEMENTATION = "implementation";

	/**
	 * Constant used for all "implementation_version" nodes in JSON contents.
	 */
	private static final String IMPLEMENATION_VERSION = "implementation-version";

	/**
	 * Constant used for all "langauge_info" nodes in JSON contents.
	 */
	private static final LanguageInfo LANGUAGE_INFO = new LanguageInfo();

	/**
	 * Constant used for all "banner" nodes in JSON contents.
	 */
	private static final String BANNER = "banner";

	/**
	 * Constant used for all "help_link" nodes in JSON contents.
	 */
	private static final HelpLink HELP_LINK = new HelpLink();

	/**
	 * Tests that {@link ExecuteInput} messages are parsed correctly.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test
	public void testExecuteInputValid() throws Exception {
		// Create original content
		ExecuteInput original = new ExecuteInput().withCode(CODE).withExecutionCount(EXECUTION_COUNT);
		original.validate();

		// Parse back and forth
		ExecuteInput parsed = JSON_OBJECT_MAPPER.readValue(JSON_OBJECT_MAPPER.writeValueAsBytes(original),
				ExecuteInput.class);
		parsed.validate();

		// Assert that values stayed the same
		assertEquals(original, parsed);
		assertEquals(original.getCode(), parsed.getCode());
		assertEquals(CODE, parsed.getCode());
		assertEquals(original.getExecutionCount(), parsed.getExecutionCount());
		assertEquals(EXECUTION_COUNT, parsed.getExecutionCount());
	}

	/**
	 * Tests that {@link ExecuteInput} messages handle required parameters
	 * correctly.
	 * <p>
	 * Creates invalid {@link ExecuteInput} (no code) and tries to encode them.
	 * Should throw {@link JsonMappingException}.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test(expected = JsonMappingException.class)
	public void testExecuteInputNoCode() throws Exception {
		ExecuteInput executeInput = new ExecuteInput().withExecutionCount(EXECUTION_COUNT);
		JSON_OBJECT_MAPPER.writeValueAsBytes(executeInput);
		executeInput.validate();
		fail("Did not detect missing parameter.");
	}

	/**
	 * Tests that {@link ExecuteInput} messages handle required parameters
	 * correctly.
	 * <p>
	 * Creates invalid {@link ExecuteInput} (no code) and tries to encode them.
	 * Should throw {@link JsonMappingException}.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test(expected = JsonMappingException.class)
	public void testExecuteInputNoExecutionCount() throws Exception {
		ExecuteInput executeInput = new ExecuteInput().withCode(CODE);
		JSON_OBJECT_MAPPER.writeValueAsBytes(executeInput);
		executeInput.validate();
		fail("Did not detect missing parameter.");
	}

	/**
	 * Tests that {@link ExecuteResult} messages are parsed correctly.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test
	public void testExecuteResultValid() throws Exception {
		// Create original content
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(DATA_KEY, DATA_VALUE);
		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put(METADATA_KEY, METADATA_VALUE);
		ExecuteResult original = new ExecuteResult().withData(data).withMetadata(metadata)
				.withExecutionCount(EXECUTION_COUNT);
		original.validate();

		// Parse back and forth
		ExecuteResult parsed = JSON_OBJECT_MAPPER.readValue(JSON_OBJECT_MAPPER.writeValueAsBytes(original),
				ExecuteResult.class);
		parsed.validate();

		// Assert that values stayed the same
		assertEquals(original, parsed);
		assertEquals(original.getData(), parsed.getData());
		assertEquals(1, parsed.getData().size());
		assertEquals(DATA_VALUE, parsed.getData().get(DATA_KEY));
		assertEquals(original.getMetadata(), parsed.getMetadata());
		assertEquals(1, parsed.getMetadata().size());
		assertEquals(METADATA_VALUE, parsed.getMetadata().get(METADATA_KEY));
		assertEquals(EXECUTION_COUNT, parsed.getExecutionCount());
	}

	/**
	 * Tests that {@link ExecuteResult} messages handle required parameters
	 * correctly.
	 * <p>
	 * Creates invalid {@link ExecuteResult} (no data) and tries to encode them.
	 * Should throw {@link JsonMappingException}.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test(expected = JsonMappingException.class)
	public void testExecuteReplyNoData() throws Exception {
		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put(METADATA_KEY, METADATA_VALUE);
		ExecuteResult original = new ExecuteResult().withMetadata(metadata).withExecutionCount(EXECUTION_COUNT);
		JSON_OBJECT_MAPPER.writeValueAsBytes(original);
		original.validate();
		fail("Did not detect missing parameter.");
	}

	/**
	 * Tests that {@link ExecuteResult} messages handle required parameters
	 * correctly.
	 * <p>
	 * Creates invalid {@link ExecuteResult} (no metadata) and tries to encode
	 * them. Should throw {@link JsonMappingException}.
	 *
	 * @throws Exception
	 *             In case of error.
	 */
	@Test(expected = JsonMappingException.class)
	public void testExecuteReplyNoMetadata() throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(DATA_KEY, DATA_VALUE);
		ExecuteResult original = new ExecuteResult().withData(data).withExecutionCount(EXECUTION_COUNT);
		JSON_OBJECT_MAPPER.writeValueAsBytes(original);
		original.validate();
		fail("Did not detect missing parameter.");
	}

	/**
	 * Tests that {@link ExecuteResult} messages handle required parameters
	 * correctly.
	 * <p>
	 * Creates invalid {@link ExecuteResult} (no execution_count) and tries to
	 * encode them. Should throw {@link JsonMappingException}.
	 *
	 * @throws Exception
	 *             In case of error.
	 */
	@Test(expected = JsonMappingException.class)
	public void testExecuteReplyNoExecutionCount() throws Exception {
		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put(METADATA_KEY, METADATA_VALUE);
		ExecuteResult original = new ExecuteResult().withMetadata(metadata);
		JSON_OBJECT_MAPPER.writeValueAsBytes(original);
		original.validate();
		fail("Did not detect missing parameter.");
	}

	/**
	 * Tests that {@link ExecuteResult} messages are parsed correctly.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test
	public void testHistoryReplyValid() throws Exception {
		// Create original content
		List<List<Object>> history = new ArrayList<List<Object>>();
		List<Object> entry = new ArrayList<Object>();
		entry.add(HISTORY_ENTRY);
		history.add(entry);
		HistoryReply original = new HistoryReply().withHistory(history);
		original.validate();

		// Parse back and forth
		HistoryReply parsed = JSON_OBJECT_MAPPER.readValue(JSON_OBJECT_MAPPER.writeValueAsBytes(original),
				HistoryReply.class);
		parsed.validate();

		// Assert that values stayed the same
		assertEquals(original, parsed);
		assertEquals(1, parsed.getHistory().size());
		assertEquals(1, parsed.getHistory().size());
		assertEquals(HISTORY_ENTRY, parsed.getHistory().get(0).get(0));
	}

	/**
	 * Tests that {@link HistoryReply} messages handle required parameters
	 * correctly.
	 * <p>
	 * Creates invalid {@link HistoryReply} (no history) and tries to encode
	 * them. Should throw {@link JsonMappingException}.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test(expected = JsonMappingException.class)
	public void testHistoryReplyNoHistory() throws Exception {
		ExecuteResult original = new ExecuteResult();
		JSON_OBJECT_MAPPER.writeValueAsBytes(original);
		original.validate();
		fail("Did not detect missing parameter.");
	}

	/**
	 * Tests that {@link IsCompleteReply} messages are parsed correctly.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test
	public void testIsCompleteReplyValid() throws Exception {
		// Create original content
		IsCompleteReply original = new IsCompleteReply().withStatus(STATUS).withIndent(INDENT);
		original.validate();

		// Parse back and forth
		IsCompleteReply parsed = JSON_OBJECT_MAPPER.readValue(JSON_OBJECT_MAPPER.writeValueAsBytes(original),
				IsCompleteReply.class);
		parsed.validate();

		// Assert that values stayed the same
		assertEquals(original, parsed);
		assertEquals(original.getStatus(), parsed.getStatus());
		assertEquals(STATUS, parsed.getStatus());
		assertEquals(original.getIndent(), parsed.getIndent());
		assertEquals(INDENT, parsed.getIndent());
	}

	/**
	 * Tests that {@link IsCompleteReply} messages handle required parameters
	 * correctly.
	 * <p>
	 * Creates invalid {@link IsCompleteReply} (no status) and tries to encode
	 * them. Should throw {@link JsonMappingException}.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test(expected = JsonMappingException.class)
	public void testIsCompleteReplyNoStatus() throws Exception {
		IsCompleteReply original = new IsCompleteReply().withIndent(INDENT);
		JSON_OBJECT_MAPPER.writeValueAsBytes(original);
		original.validate();
		fail("Did not detect missing parameter.");
	}

	/**
	 * Tests that {@link KernelInfoReply} messages are parsed correctly.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test
	public void testKernelInfoReplyValid() throws Exception {
		// Create original content
		List<HelpLink> helplinks = new ArrayList<HelpLink>();
		helplinks.add(HELP_LINK);
		KernelInfoReply original = new KernelInfoReply().withProtocolVersion(PROTOCOL_VERSION)
				.withImplementation(IMPLEMENTATION).withImplementationVersion(IMPLEMENATION_VERSION)
				.withLanguageInfo(LANGUAGE_INFO).withBanner(BANNER).withHelpLinks(helplinks);
		original.validate();

		// Parse back and forth
		KernelInfoReply parsed = JSON_OBJECT_MAPPER.readValue(JSON_OBJECT_MAPPER.writeValueAsBytes(original),
				KernelInfoReply.class);
		parsed.validate();

		// Assert that values stayed the sameoriginaoriginal.getLanguageInfo
		assertEquals(original, parsed);
		assertEquals(original.getProtocolVersion(), parsed.getProtocolVersion());
		assertEquals(PROTOCOL_VERSION, parsed.getProtocolVersion());
		assertEquals(original.getImplementation(), parsed.getImplementation());
		assertEquals(IMPLEMENTATION, parsed.getImplementation());
		assertEquals(original.getImplementationVersion(), parsed.getImplementationVersion());
		assertEquals(IMPLEMENATION_VERSION, parsed.getImplementationVersion());
		assertEquals(original.getLanguageInfo(), parsed.getLanguageInfo());
		assertEquals(LANGUAGE_INFO, parsed.getLanguageInfo());
		assertEquals(original.getBanner(), parsed.getBanner());
		assertEquals(BANNER, parsed.getBanner());
		assertEquals(original.getHelpLinks(), parsed.getHelpLinks());
		assertEquals(helplinks, parsed.getHelpLinks());
		assertEquals(1, parsed.getHelpLinks().size());
		assertEquals(HELP_LINK, parsed.getHelpLinks().get(0));
	}

	/**
	 * Tests that {@link KernelInfoReply} messages handle required parameters
	 * correctly.
	 * <p>
	 * Creates invalid {@link KernelInfoReply} (no protocol_version) and tries
	 * to encode them. Should throw {@link JsonMappingException}.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test(expected = JsonMappingException.class)
	public void testKernelInfoReplyNoProtocolVersion() throws Exception {
		List<HelpLink> helplinks = new ArrayList<HelpLink>();
		helplinks.add(HELP_LINK);
		KernelInfoReply original = new KernelInfoReply().withImplementation(IMPLEMENTATION)
				.withImplementationVersion(IMPLEMENATION_VERSION).withLanguageInfo(LANGUAGE_INFO).withBanner(BANNER)
				.withHelpLinks(helplinks);
		JSON_OBJECT_MAPPER.writeValueAsBytes(original);
		original.validate();
		fail("Did not detect missing parameter.");
	}

	/**
	 * Tests that {@link KernelInfoReply} messages handle required parameters
	 * correctly.
	 * <p>
	 * Creates invalid {@link KernelInfoReply} (no implemenation) and tries to
	 * encode them. Should throw {@link JsonMappingException}.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test(expected = JsonMappingException.class)
	public void testKernelInfoReplyNoImplementation() throws Exception {
		List<HelpLink> helplinks = new ArrayList<HelpLink>();
		helplinks.add(HELP_LINK);
		KernelInfoReply original = new KernelInfoReply().withProtocolVersion(PROTOCOL_VERSION)
				.withImplementationVersion(IMPLEMENATION_VERSION).withLanguageInfo(LANGUAGE_INFO).withBanner(BANNER)
				.withHelpLinks(helplinks);
		JSON_OBJECT_MAPPER.writeValueAsBytes(original);
		original.validate();
		fail("Did not detect missing parameter.");
	}

	/**
	 * Tests that {@link KernelInfoReply} messages handle required parameters
	 * correctly.
	 * <p>
	 * Creates invalid {@link KernelInfoReply} (no implemenation_version) and
	 * tries to encode them. Should throw {@link JsonMappingException}.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test(expected = JsonMappingException.class)
	public void testKernelInfoReplyNoImplementationVersion() throws Exception {
		List<HelpLink> helplinks = new ArrayList<HelpLink>();
		helplinks.add(HELP_LINK);
		KernelInfoReply original = new KernelInfoReply().withProtocolVersion(PROTOCOL_VERSION)
				.withImplementation(IMPLEMENTATION).withLanguageInfo(LANGUAGE_INFO).withBanner(BANNER)
				.withHelpLinks(helplinks);
		JSON_OBJECT_MAPPER.writeValueAsBytes(original);
		original.validate();
		fail("Did not detect missing parameter.");
	}

	/**
	 * Tests that {@link KernelInfoReply} messages handle required parameters
	 * correctly.
	 * <p>
	 * Creates invalid {@link KernelInfoReply} (no language_info) and tries to
	 * encode them. Should throw {@link JsonMappingException}.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test(expected = JsonMappingException.class)
	public void testKernelInfoReplyNoLanguageInfo() throws Exception {
		List<HelpLink> helplinks = new ArrayList<HelpLink>();
		helplinks.add(HELP_LINK);
		KernelInfoReply original = new KernelInfoReply().withProtocolVersion(PROTOCOL_VERSION)
				.withImplementation(IMPLEMENTATION).withImplementationVersion(IMPLEMENATION_VERSION).withBanner(BANNER)
				.withHelpLinks(helplinks);
		JSON_OBJECT_MAPPER.writeValueAsBytes(original);
		original.validate();
		fail("Did not detect missing parameter.");
	}

	/**
	 * Tests that {@link KernelInfoReply} messages handle required parameters
	 * correctly.
	 * <p>
	 * Creates invalid {@link KernelInfoReply} (no banner) and tries to encode
	 * them. Should throw {@link JsonMappingException}.
	 * 
	 * @throws Exception
	 *             In case of error.
	 */
	@Test(expected = JsonMappingException.class)
	public void testKernelInfoReplyNoBanner() throws Exception {
		List<HelpLink> helplinks = new ArrayList<HelpLink>();
		helplinks.add(HELP_LINK);
		KernelInfoReply original = new KernelInfoReply().withProtocolVersion(PROTOCOL_VERSION)
				.withImplementation(IMPLEMENTATION).withImplementationVersion(IMPLEMENATION_VERSION)
				.withLanguageInfo(LANGUAGE_INFO).withHelpLinks(helplinks);
		JSON_OBJECT_MAPPER.writeValueAsBytes(original);
		original.validate();
		fail("Did not detect missing parameter.");
	}
}
