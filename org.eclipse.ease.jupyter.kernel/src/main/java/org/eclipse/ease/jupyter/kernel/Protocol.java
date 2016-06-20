package org.eclipse.ease.jupyter.kernel;

import com.fasterxml.jackson.core.JsonEncoding;

import org.apache.commons.codec.digest.HmacUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.eclipse.ease.jupyter.kernel.messages.Header;
import org.eclipse.ease.jupyter.kernel.messages.Message;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Protocol implementation for Jupyter message.
 * 
 * Handles message parsing either creating {@link Message} objects from byte
 * arrays or creates byte arrays from {@link Message} objects.
 *
 */
public class Protocol {
	/**
	 * {@link ObjectMapper} to create object based on JSON strings.
	 */
	protected static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

	/**
	 * Jupyter protocol version used.
	 */
	public static final String VERSION = "5.0";

	/**
	 * Protocol delimiter separating the ZMQ id information from the actual
	 * message.
	 */
	public static final String DELIMITER = "<IDS|MSG>";

	/**
	 * Encoding used by messages (default UTF-8)
	 */
	public static final Charset ENCODING = Charset.forName(JsonEncoding.UTF8
			.getJavaName());

	/**
	 * {@link #DELIMITER} in byte form for easier use.
	 */
	private static final byte[] DELIMITER_BYTES = DELIMITER.getBytes(ENCODING);

	/**
	 * Empty signature in case no signatue algorithm used.
	 */
	private static final byte[] EMPTY_SIGNATURE = new byte[0];

	/**
	 * HMAC key to be used for signing messages.
	 */
	private final byte[] fHmacKey;

	/**
	 * HMAC algorithm to be used for signing messages.
	 */
	private final HmacAlgorithms fHmacAlgorithm;

	/**
	 * Default constructor for protocol without message signatures.
	 */
	public Protocol() {
		this(null, (HmacAlgorithms) null);
	}

	/**
	 * Constructor for protocol with message signatures.
	 * 
	 * @param hmacKey
	 *            HMAC key to be used for signing messages.
	 * @param hmacAlgorithm
	 *            HMAC algorithm to be used for signing messages.
	 */
	public Protocol(final byte[] hmacKey, final HmacAlgorithms hmacAlgorithm) {
		this.fHmacAlgorithm = hmacAlgorithm;
		this.fHmacKey = hmacKey;
	}

	/**
	 * Overload of {@link #Protocol(byte[], HmacAlgorithms)} parsing strings to
	 * correct format.
	 * 
	 * @param key
	 *            HMAC key to be used for signing messages.
	 * @param signaturScheme
	 *            HMAC algorithm to be used for signing messages.
	 */
	public Protocol(final String key, final String signaturScheme) {
		this(getKeyBytes(key), getHmacAlgorithm(signaturScheme));
	}

	/**
	 * Utility method parsing key-string to key in byte[] form.
	 * 
	 * @param key
	 *            Key to be parsed to byte[].
	 * @return <code>byte[]</code> with key, or <code>null</code> if invalid
	 *         input given.
	 */
	private static byte[] getKeyBytes(final String key) {
		return key != null && !key.isEmpty() ? key.getBytes() : null;
	}

	/**
	 * Utility method parsing the given signature scheme string to an actual
	 * {@link HmacAlgorithms} object.
	 * 
	 * @param signatureScheme
	 *            Signature scheme string to be parsed to {@link HmacAlgorithms}
	 * @return {@link HmacAlgorithms} based on input or <code>null</code> if no
	 *         matching algorithm found.
	 */
	private static HmacAlgorithms getHmacAlgorithm(final String signatureScheme) {
		if (signatureScheme == null || signatureScheme.isEmpty()) {
			return null;
		}

		final String algo = signatureScheme.replace("-", "");

		for (final HmacAlgorithms ha : HmacAlgorithms.values()) {
			if (algo.equalsIgnoreCase(ha.toString())) {
				return ha;
			}
		}

		throw new IllegalArgumentException("Unsupported signature scheme: "
				+ signatureScheme);
	}

	/**
	 * Getter to check if messages are going to be signed.
	 * 
	 * @return <code>true</code> if messages are being signed.
	 */
	public boolean isSigning() {
		return fHmacKey != null && fHmacAlgorithm != null;
	}

	/**
	 * Parses a given {@link Message} object to its byte[] representation that
	 * can be send to Jupyter partner.
	 * 
	 * @param message
	 *            {@link Message} to be parsed to byte[].
	 * @return <code>byte[]</code> representing the given {@link Message}.
	 * @throws IOException
	 *             If message could not be parsed.
	 */
	public List<byte[]> toFrames(final Message message) throws IOException {
		// Patch the header for the given message
		message.getHeader().setVersion(VERSION);

		// Split the data into json frames according to the wire protocol
		final List<byte[]> jsonFrames = Arrays
				.asList(JSON_OBJECT_MAPPER.writeValueAsBytes(message
						.getHeader()), JSON_OBJECT_MAPPER
						.writeValueAsBytes(message.getParentHeader()),
						JSON_OBJECT_MAPPER.writeValueAsBytes(message
								.getMetadata()), JSON_OBJECT_MAPPER
								.writeValueAsBytes(message.getContent()));

		// Create the actual ZMQ message
		final List<byte[]> frames = new ArrayList<byte[]>();
		frames.addAll(message.getZmqIdentities());
		frames.add(DELIMITER_BYTES);
		frames.add(signature(jsonFrames));
		frames.addAll(jsonFrames);
		frames.addAll(message.getExtraData());
		return frames;
	}

	/**
	 * Creates the signature for the JSON frames to be send over ZMQ socket.
	 * 
	 * @param jsonFrames
	 *            Data to be signed.
	 * @return Signature as <code>byte[]</code>
	 */
	private byte[] signature(final List<byte[]> jsonFrames) {
		if (!isSigning()) {
			return EMPTY_SIGNATURE;
		}
		final Mac mac = HmacUtils.getInitializedMac(fHmacAlgorithm, fHmacKey);
		for (final byte[] jsonFrame : jsonFrames) {
			mac.update(jsonFrame);
		}
		return Hex.encodeHexString(mac.doFinal()).getBytes(ENCODING);
	}

	/**
	 * FIXME: Bit of a hack.
	 *
	 * Utility pseudo-command pattern to parse given byte[] to corresponding
	 * fields in {@link Message} object.
	 */
	@SuppressWarnings("unchecked")
	private enum FrameHandler {
		ZMQ_ID {
			@Override
			boolean handle(final byte[] frame, final List<byte[]> jsonFrames,
					final Message message) {
				if (Arrays.equals(frame, DELIMITER_BYTES)) {
					return true;
				} else {
					message.withZmqIdentity(frame);
					return false;
				}
			}
		},
		HMAC_SIGNATURE {
			@Override
			boolean handle(final byte[] frame, final List<byte[]> jsonFrames,
					final Message message) {
				message.withHmacSignature(frame);
				return true;
			}
		},
		HEADER {
			@Override
			boolean handle(final byte[] frame, final List<byte[]> jsonFrames,
					final Message message) throws IOException {
				jsonFrames.add(frame);
				message.withHeader(JSON_OBJECT_MAPPER.readValue(frame,
						Header.class));
				return true;
			}
		},
		PARENT_HEADER {
			@Override
			boolean handle(final byte[] frame, final List<byte[]> jsonFrames,
					final Message message) throws IOException {
				jsonFrames.add(frame);
				message.withParentHeader(JSON_OBJECT_MAPPER.readValue(frame,
						Header.class));
				return true;
			}
		},
		METADATA {
			@Override
			boolean handle(final byte[] frame, final List<byte[]> jsonFrames,
					final Message message) throws IOException {
				jsonFrames.add(frame);
				message.withMetadata(JSON_OBJECT_MAPPER.readValue(frame,
						Map.class));
				return true;
			}
		},
		CONTENT {
			@Override
			boolean handle(final byte[] frame, final List<byte[]> jsonFrames,
					final Message message) throws IOException {
				jsonFrames.add(frame);
				message.withContent(JSON_OBJECT_MAPPER.readValue(frame,
						Map.class));
				return true;
			}
		},
		EXTRA_DATA {
			@Override
			boolean handle(final byte[] frame, final List<byte[]> jsonFrames,
					final Message message) {
				message.withExtraDatum(frame);
				return false;
			}
		};

		abstract boolean handle(byte[] frame, final List<byte[]> jsonFrames,
				Message message) throws IOException;
	};

	/**
	 * Creates new {@link Message} object based on given bytes received from ZMQ
	 * socket.
	 * 
	 * @param frames
	 *            Data received from ZMQ socket.
	 * @return {@link Message} based on received data.
	 * @throws IOException
	 *             If message contains invalid data.
	 */
	public Message fromFrames(List<byte[]> frames) throws IOException {
		// Intialize empty message
		final Message message = new Message();

		// Get iterator for all frames in message
		final List<byte[]> jsonFrames = new ArrayList<byte[]>();
		final Iterator<FrameHandler> i = Arrays.asList(FrameHandler.values())
				.iterator();

		// Handle frames one at a time
		FrameHandler handler = i.next();
		for (final byte[] frame : frames) {
			if (handler.handle(frame, jsonFrames, message)) {
				handler = i.next();
			}
		}

		// Check if all frames successfully handled
		if (handler != FrameHandler.EXTRA_DATA) {
			throw new IOException("Not enough frames received, last frame: "
					+ handler);
		}

		// Check if signature correct.
		if (!Arrays.equals(message.getHmacSignature(), signature(jsonFrames))) {
			throw new IOException("Invalid HMAC signature in received message");
		}

		// Actually return message
		return message;
	}
}
