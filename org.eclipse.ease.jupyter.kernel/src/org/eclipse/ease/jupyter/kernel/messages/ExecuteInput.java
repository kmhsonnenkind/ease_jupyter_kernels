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
 * {@link Content} for broadcasting code currently executed.
 * 
 * Automatically generated from JSON schema using jsonschema2pojo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "code", "execution_count" })
public class ExecuteInput extends Content {

	@JsonProperty("code")
	private String code;
	@JsonProperty("execution_count")
	private Integer executionCount;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * 
	 * @return The code
	 */
	@JsonProperty("code")
	public String getCode() {
		return code;
	}

	/**
	 * 
	 * @param code
	 *            The code
	 */
	@JsonProperty("code")
	public void setCode(String code) {
		this.code = code;
	}

	public ExecuteInput withCode(String code) {
		this.code = code;
		return this;
	}

	/**
	 * 
	 * @return The executionCount
	 */
	@JsonProperty("execution_count")
	public Integer getExecutionCount() {
		return executionCount;
	}

	/**
	 * 
	 * @param executionCount
	 *            The execution_count
	 */
	@JsonProperty("execution_count")
	public void setExecutionCount(Integer executionCount) {
		this.executionCount = executionCount;
	}

	public ExecuteInput withExecutionCount(Integer executionCount) {
		this.executionCount = executionCount;
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

	public ExecuteInput withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode()).append(code).append(executionCount)
				.append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof ExecuteInput) == false) {
			return false;
		}
		ExecuteInput rhs = ((ExecuteInput) other);
		return new EqualsBuilder().appendSuper(super.equals(other)).append(code, rhs.code)
				.append(executionCount, rhs.executionCount).append(additionalProperties, rhs.additionalProperties)
				.isEquals();
	}

}
