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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * {@link Content} for code inspection requests.
 * 
 * Automatically generated from JSON schema using jsonschema2pojo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "code", "cursor_pos", "detail_level" })
public class InspectRequest extends Content {

	@JsonProperty("code")
	private String code;
	@JsonProperty("cursor_pos")
	private Integer cursorPos;
	@JsonProperty("detail_level")
	private Integer detailLevel;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	public InspectRequest(@JsonProperty(value = "code", required = true) final String code,
			@JsonProperty(value = "cursor_pos", required = true) final Integer cursorPos,
			@JsonProperty(value = "detail_level") final Integer detailLevel) {
		this.code = code;
		this.cursorPos = cursorPos;
		this.detailLevel = detailLevel;
	}

	/**
	 * 
	 * (Required)
	 * 
	 * @return The code
	 */
	@JsonProperty("code")
	public String getCode() {
		return code;
	}

	/**
	 * 
	 * (Required)
	 * 
	 * @param code
	 *            The code
	 */
	@JsonProperty("code")
	public void setCode(String code) {
		this.code = code;
	}

	public InspectRequest withCode(String code) {
		this.code = code;
		return this;
	}

	/**
	 * 
	 * @return The cursorPos
	 */
	@JsonProperty("cursor_pos")
	public Integer getCursorPos() {
		return cursorPos;
	}

	/**
	 * 
	 * @param cursorPos
	 *            The cursorPos
	 */
	@JsonProperty("cursor_pos")
	public void setCursorPos(Integer cursorPos) {
		this.cursorPos = cursorPos;
	}

	public InspectRequest withCursosPos(Integer cursorPos) {
		this.cursorPos = cursorPos;
		return this;
	}

	/**
	 * 
	 * @return The detailLevel
	 */
	@JsonProperty("detail_level")
	public Integer getDetailLevel() {
		return detailLevel;
	}

	/**
	 * 
	 * @param detailLevel
	 *            The detailLevel
	 */
	@JsonProperty("detail_level")
	public void setDetailLevel(Integer detailLevel) {
		this.detailLevel = detailLevel;
	}

	public InspectRequest withDetailLevel(Integer detailLevel) {
		this.detailLevel = detailLevel;
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

	public InspectRequest withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode()).append(code).append(cursorPos).append(detailLevel)
				.append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof InspectRequest) == false) {
			return false;
		}
		InspectRequest rhs = ((InspectRequest) other);
		return new EqualsBuilder().appendSuper(super.equals(other)).append(code, rhs.code)
				.append(cursorPos, rhs.cursorPos).append(detailLevel, rhs.detailLevel)
				.append(additionalProperties, rhs.additionalProperties).isEquals();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ease.jupyter.kernel.messages.Content#validate()
	 */
	@Override
	public void validate() throws JsonMappingException {
		if (this.code == null) {
			throw new JsonMappingException("Missing parameter.");
		}
		if (this.cursorPos == null) {
			throw new JsonMappingException("Missing parameter.");
		}
		if (this.detailLevel == null) {
			throw new JsonMappingException("Missing parameter.");
		}
	}

}
