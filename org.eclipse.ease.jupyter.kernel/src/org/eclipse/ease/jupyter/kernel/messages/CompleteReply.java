/*******************************************************************************
 * Copyright (c) 2016 Martin Kloesch and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tobias Verbeke - original implementation in Japyter project
 *     Martin Kloesch - move to kernel project and minor reworks
 *******************************************************************************/
package org.eclipse.ease.jupyter.kernel.messages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * {@link Content} for replies to code completion requests.
 * 
 * Automatically generated from JSON schema using jsonschema2pojo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "matches", "cursor_start", "cursor_end", "metadata", "status" })
public class CompleteReply extends Content {
	@JsonProperty("matches")
	private List<String> matches;
	@JsonProperty("cursor_start")
	private Integer cursorStart;
	@JsonProperty("cursor_end")
	private Integer cursorEnd;
	@JsonProperty("metadata")
	private Map<String, Object> metadata;
	@JsonProperty("status")
	private CompleteReply.Status status;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * 
	 * @return The matches
	 */
	@JsonProperty("matches")
	public List<String> getMatches() {
		return matches;
	}

	/**
	 * 
	 * @param matches
	 *            The matches
	 */
	@JsonProperty("matches")
	public void setMatches(List<String> matches) {
		this.matches = matches;
	}

	public CompleteReply withMatches(List<String> matches) {
		this.matches = matches;
		return this;
	}

	/**
	 * 
	 * @return The cursorStart
	 */
	@JsonProperty("cursor_start")
	public Integer getCursorStart() {
		return cursorStart;
	}

	/**
	 * 
	 * @param cursorStart
	 *            The cursorStart
	 */
	@JsonProperty("cursor_start")
	public void setCursorStart(Integer cursorStart) {
		this.cursorStart = cursorStart;
	}

	public CompleteReply withCursorStart(Integer cursorStart) {
		this.cursorStart = cursorStart;
		return this;
	}

	/**
	 * 
	 * @return The cursorEnd
	 */
	@JsonProperty("cursor_end")
	public Integer getCursorEnd() {
		return cursorEnd;
	}

	/**
	 * 
	 * @param cursorEnd
	 *            The cursorEnd
	 */
	@JsonProperty("cursor_end")
	public void setCursorEnd(Integer cursorEnd) {
		this.cursorEnd = cursorEnd;
	}

	public CompleteReply withCursorEnd(Integer cursorEnd) {
		this.cursorEnd = cursorEnd;
		return this;
	}

	/**
	 * 
	 * @return The metadata
	 */
	@JsonProperty("metadata")
	public Map<String, Object> getMetadata() {
		return metadata;
	}

	/**
	 * 
	 * @param metadata
	 *            The metadata
	 */
	@JsonProperty("metadata")
	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	public CompleteReply withMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
		return this;
	}

	/**
	 * 
	 * @return The status
	 */
	@JsonProperty("status")
	public CompleteReply.Status getStatus() {
		return status;
	}

	/**
	 * 
	 * @param status
	 *            The status
	 */
	@JsonProperty("status")
	public void setStatus(CompleteReply.Status status) {
		this.status = status;
	}

	public CompleteReply withStatus(CompleteReply.Status status) {
		this.status = status;
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

	public CompleteReply withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode()).append(matches).append(cursorStart).append(cursorEnd)
				.append(metadata).append(status).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof CompleteReply) == false) {
			return false;
		}
		CompleteReply rhs = ((CompleteReply) other);
		return new EqualsBuilder().appendSuper(super.equals(other)).append(matches, rhs.matches)
				.append(cursorStart, rhs.cursorStart).append(cursorEnd, rhs.cursorEnd).append(metadata, rhs.metadata)
				.append(status, rhs.status).append(additionalProperties, rhs.additionalProperties).isEquals();
	}

	public static enum Status {

		OK("ok"), ERROR("error");
		private final String value;
		private static Map<String, CompleteReply.Status> constants = new HashMap<String, CompleteReply.Status>();

		static {
			for (CompleteReply.Status c : values()) {
				constants.put(c.value, c);
			}
		}

		private Status(String value) {
			this.value = value;
		}

		@JsonValue
		@Override
		public String toString() {
			return this.value;
		}

		@JsonCreator
		public static CompleteReply.Status fromValue(String value) {
			CompleteReply.Status constant = constants.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ease.jupyter.kernel.messages.Content#validate()
	 */
	@Override
	public void validate() throws JsonMappingException {

	}

}
