package org.eclipse.ease.jupyter.kernel.messages;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Header information for Jupyter message.
 * 
 * Automatically generated from JSON schema using jsonschema2pojo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "msg_id", "username", "session", "msg_type", "version",
		"date" })
public class Header {

	@JsonProperty("msg_id")
	private String msgId;
	@JsonProperty("username")
	private String username;
	@JsonProperty("session")
	private String session;
	@JsonProperty("msg_type")
	private String msgType;
	@JsonProperty("version")
	private String version;
	@JsonProperty("date")
	private String date;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * 
	 * @return The msgId
	 */
	@JsonProperty("msg_id")
	public String getMsgId() {
		return msgId;
	}

	/**
	 * 
	 * @param msgId
	 *            The msg_id
	 */
	@JsonProperty("msg_id")
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public Header withMsgId(String msgId) {
		this.msgId = msgId;
		return this;
	}

	/**
	 * 
	 * @return The username
	 */
	@JsonProperty("username")
	public String getUsername() {
		return username;
	}

	/**
	 * 
	 * @param username
	 *            The username
	 */
	@JsonProperty("username")
	public void setUsername(String username) {
		this.username = username;
	}

	public Header withUsername(String username) {
		this.username = username;
		return this;
	}

	/**
	 * 
	 * @return The session
	 */
	@JsonProperty("session")
	public String getSession() {
		return session;
	}

	/**
	 * 
	 * @param session
	 *            The session
	 */
	@JsonProperty("session")
	public void setSession(String session) {
		this.session = session;
	}

	public Header withSession(String session) {
		this.session = session;
		return this;
	}

	/**
	 * 
	 * @return The msgType
	 */
	@JsonProperty("msg_type")
	public String getMsgType() {
		return msgType;
	}

	/**
	 * 
	 * @param msgType
	 *            The msg_type
	 */
	@JsonProperty("msg_type")
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public Header withMsgType(String msgType) {
		this.msgType = msgType;
		return this;
	}

	/**
	 * 
	 * @return The version
	 */
	@JsonProperty("version")
	public String getVersion() {
		return version;
	}

	/**
	 * 
	 * @param version
	 *            The version
	 */
	@JsonProperty("version")
	public void setVersion(String version) {
		this.version = version;
	}

	public Header withVersion(String version) {
		this.version = version;
		return this;
	}

	/**
	 * 
	 * @return The date
	 */
	@JsonProperty("date")
	public String getDate() {
		return date;
	}

	/**
	 * 
	 * @param date
	 *            The date
	 */
	@JsonProperty("date")
	public void setDate(String date) {
		this.date = date;
	}

	public Header withDate(String date) {
		this.date = date;
		return this;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	public Header withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(msgId).append(username)
				.append(session).append(msgType).append(version).append(date)
				.append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Header) == false) {
			return false;
		}
		Header rhs = ((Header) other);
		return new EqualsBuilder().append(msgId, rhs.msgId)
				.append(username, rhs.username).append(session, rhs.session)
				.append(msgType, rhs.msgType).append(version, rhs.version)
				.append(date, rhs.date)
				.append(additionalProperties, rhs.additionalProperties)
				.isEquals();
	}

}
