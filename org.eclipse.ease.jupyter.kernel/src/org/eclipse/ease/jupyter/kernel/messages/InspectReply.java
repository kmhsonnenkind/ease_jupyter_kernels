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
@JsonPropertyOrder({ "status", "found", "data", "metadata" })
public class InspectReply extends Content {
	@JsonProperty("status")
	private InspectReply.Status status;
	@JsonProperty("found")
	private Boolean found;
	@JsonProperty("data")
	private Map<String, Object> data;
	@JsonProperty("metadata")
	private Map<String, Object> metadata;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * 
	 * @return The status
	 */
	@JsonProperty("status")
	public InspectReply.Status getStatus() {
		return status;
	}

	/**
	 * 
	 * @param status
	 *            The status
	 */
	@JsonProperty("status")
	public void setStatus(InspectReply.Status status) {
		this.status = status;
	}

	public InspectReply withStatus(InspectReply.Status status) {
		this.status = status;
		return this;
	}

	/**
	 * 
	 * @return The found
	 */
	@JsonProperty("found")
	public Boolean getFound() {
		return found;
	}

	/**
	 * 
	 * @param found
	 *            The found
	 */
	@JsonProperty("found")
	public void setFound(Boolean found) {
		this.found = found;
	}

	public InspectReply withFound(Boolean found) {
		this.found = found;
		return this;
	}

	/**
	 * 
	 * @return The data
	 */
	@JsonProperty("data")
	public Map<String, Object> getData() {
		return data;
	}

	/**
	 * 
	 * @param data
	 *            The data
	 */
	@JsonProperty("data")
	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public InspectReply withData(Map<String, Object> data) {
		this.data = data;
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

	public InspectReply withMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
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

	public InspectReply withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode()).append(status).append(found).append(data)
				.append(metadata).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof InspectReply) == false) {
			return false;
		}
		InspectReply rhs = ((InspectReply) other);
		return new EqualsBuilder().appendSuper(super.equals(other)).append(status, rhs.status).append(found, rhs.found)
				.append(data, rhs.data).append(metadata, rhs.metadata)
				.append(additionalProperties, rhs.additionalProperties).isEquals();
	}

	public static enum Status {

		OK("ok"), ERROR("error");
		private final String value;
		private static Map<String, InspectReply.Status> constants = new HashMap<String, InspectReply.Status>();

		static {
			for (InspectReply.Status c : values()) {
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
		public static InspectReply.Status fromValue(String value) {
			InspectReply.Status constant = constants.get(value);
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
