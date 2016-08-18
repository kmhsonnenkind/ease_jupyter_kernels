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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * {@link Content} for status messages.
 * 
 * Automatically generated from JSON schema using jsonschema2pojo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "execution_state" })
public class Status extends Content {

	@JsonProperty("execution_state")
	private Status.ExecutionState executionState;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * 
	 * @return The executionState
	 */
	@JsonProperty("execution_state")
	public Status.ExecutionState getExecutionState() {
		return executionState;
	}

	/**
	 * 
	 * @param executionState
	 *            The execution_state
	 */
	@JsonProperty("execution_state")
	public void setExecutionState(Status.ExecutionState executionState) {
		this.executionState = executionState;
	}

	public Status withExecutionState(Status.ExecutionState executionState) {
		this.executionState = executionState;
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

	public Status withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode()).append(executionState).append(additionalProperties)
				.toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Status) == false) {
			return false;
		}
		Status rhs = ((Status) other);
		return new EqualsBuilder().appendSuper(super.equals(other)).append(executionState, rhs.executionState)
				.append(additionalProperties, rhs.additionalProperties).isEquals();
	}

	public static enum ExecutionState {

		BUSY("busy"), IDLE("idle"), STARTING("starting");
		private final String value;
		private static Map<String, Status.ExecutionState> constants = new HashMap<String, Status.ExecutionState>();

		static {
			for (Status.ExecutionState c : values()) {
				constants.put(c.value, c);
			}
		}

		private ExecutionState(String value) {
			this.value = value;
		}

		@JsonValue
		@Override
		public String toString() {
			return this.value;
		}

		@JsonCreator
		public static Status.ExecutionState fromValue(String value) {
			Status.ExecutionState constant = constants.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}

	}

}
