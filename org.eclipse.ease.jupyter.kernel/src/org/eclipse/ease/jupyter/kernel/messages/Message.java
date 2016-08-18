package org.eclipse.ease.jupyter.kernel.messages;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base class for all messages that will be send over ZMQ sockets.
 *
 */
public class Message {
	/**
	 * {@link ObjectMapper} to parse to and from JSON.
	 */
	protected static ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

	/**
	 * List of ZMQ identities. At least one necessary for Jupyter.
	 */
	private final List<byte[]> fZmqIdentities;

	/**
	 * HMAC signature of Message.
	 */
	private byte[] fHmacSignature;

	/**
	 * Header of message.
	 */
	private Header fHeader;

	/**
	 * Header of parent message (might be <code>null</code>
	 */
	private Header fParentHeader;

	/**
	 * Additional metadata for message.
	 */
	private Map<String, Object> fMetadata;

	/**
	 * Actual content of message.
	 */
	private Map<String, Object> fContent;

	/**
	 * Extra byte blobs for message.
	 */
	private final List<byte[]> fExtraData;

	/**
	 * Default constructor initializes members.
	 */
	public Message() {
		fZmqIdentities = new ArrayList<byte[]>();
		fHmacSignature = new byte[0];
		fHeader = new Header();
		fParentHeader = new Header();
		fMetadata = new HashMap<String, Object>();
		fContent = new HashMap<String, Object>();
		fExtraData = new ArrayList<byte[]>();
	}

	/**
	 * Build pattern to add ZMQ identity.
	 * 
	 * @param zmqIdentity
	 *            ZMQ identity to be added.
	 * @return The object itself.
	 */
	public Message withZmqIdentity(byte[] zmqIdentity) {
		fZmqIdentities.add(zmqIdentity);
		return this;
	}

	/**
	 * Build pattern to set HMAC signature.
	 * 
	 * @param hmacSignature
	 *            HMAC signature to be used.
	 * @return The object itself.
	 */
	public Message withHmacSignature(byte[] hmacSignature) {
		fHmacSignature = hmacSignature;
		return this;
	}

	/**
	 * Build pattern to set header.
	 * 
	 * @param header
	 *            Header to be used.
	 * @return The object itself.
	 */
	public Message withHeader(Header header) {
		fHeader = header;
		return this;
	}

	/**
	 * Build pattern to set parent's header.
	 * 
	 * @param parentHeader
	 *            Parent's header to be used.
	 * @return The object itself.
	 */
	public Message withParentHeader(Header parentHeader) {
		fParentHeader = parentHeader;
		return this;
	}

	/**
	 * Build pattern to set Metadata.
	 * 
	 * @param metadata
	 *            Metadata to be used.
	 * @return The object itself.
	 */
	public Message withMetadata(Map<String, Object> metadata) {
		this.fMetadata = metadata;
		return this;
	}

	/**
	 * Build pattern to set content.
	 * 
	 * @param content
	 *            Content to be used.
	 * @return The object itself.
	 */
	public Message withContent(Map<String, Object> content) {
		fContent = content;
		return this;
	}

	/**
	 * Build pattern to set content.
	 * 
	 * @param content
	 *            Content to be used.
	 * @return The object itself.
	 */
	@SuppressWarnings("unchecked")
	public Message withContent(Content content) {
		this.fContent = JSON_OBJECT_MAPPER.convertValue(
				notNull(content, "content can't be null"), Map.class);
		return this;
	}

	/**
	 * Build pattern to add extra data blob.
	 * 
	 * @param extraData
	 *            Extra data blob to be added.
	 * @return The object itself.
	 */
	public Message withExtraDatum(byte[] extraData) {
		this.fExtraData.add(extraData);
		return this;
	}

	/**
	 * Getter for header of message.
	 * 
	 * @return Header of message.
	 */
	public Header getHeader() {
		return fHeader;
	}

	/**
	 * Getter for parent's header.
	 * 
	 * @return Parent's header.
	 */
	public Header getParentHeader() {
		return fParentHeader;
	}

	/**
	 * Getter for metadata of message.
	 * 
	 * @return Metadata of message.
	 */
	public Object getMetadata() {
		return fMetadata;
	}

	/**
	 * Getter for actual content of message.
	 * 
	 * @return Actual content of message.
	 */
	public Object getContent() {
		return fContent;
	}

	/**
	 * Getter for all ZMQ identities used.
	 * 
	 * @return All ZMQ identities used.
	 */
	public List<byte[]> getZmqIdentities() {
		return fZmqIdentities;
	}

	/**
	 * Getter for all extra byte blobs.
	 * 
	 * @return All extra byte blobs.
	 */
	public List<byte[]> getExtraData() {
		return fExtraData;
	}

	/**
	 * Getter for HMAC signature.
	 * 
	 * @return HMAC signature.
	 */
	public byte[] getHmacSignature() {
		return this.fHmacSignature;
	}

	/**
	 * Utility method creating a random message ID.
	 * 
	 * @return random message ID.
	 */
	public static String randomId() {
		return "EASE." + UUID.randomUUID();
	}

	public Message createReply() {
		final Message reply = new Message().withParentHeader(getHeader());
		reply.getHeader().withMsgId(randomId());
		return reply;
	}
}
