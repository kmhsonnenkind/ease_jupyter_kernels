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
 * {@link Content} for execution requests.
 * 
 * Automatically generated from JSON schema using jsonschema2pojo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "code", "silent", "store_history", "user_expressions",
		"allow_stdin", "stop_on_error" })
public class ExecuteRequest extends Content {

	/**
	 * 
	 * (Required)
	 * 
	 */
	@JsonProperty("code")
	private String code;
	@JsonProperty("silent")
	private Boolean silent = false;
	@JsonProperty("store_history")
	private Boolean storeHistory;
	@JsonProperty("user_expressions")
	private UserExpressions userExpressions;
	@JsonProperty("allow_stdin")
	private Boolean allowStdin;
	@JsonProperty("stop_on_error")
	private Boolean stopOnError;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

	public ExecuteRequest withCode(String code) {
		this.code = code;
		return this;
	}

	/**
	 * 
	 * @return The silent
	 */
	@JsonProperty("silent")
	public Boolean getSilent() {
		return silent;
	}

	/**
	 * 
	 * @param silent
	 *            The silent
	 */
	@JsonProperty("silent")
	public void setSilent(Boolean silent) {
		this.silent = silent;
	}

	public ExecuteRequest withSilent(Boolean silent) {
		this.silent = silent;
		return this;
	}

	/**
	 * 
	 * @return The storeHistory
	 */
	@JsonProperty("store_history")
	public Boolean getStoreHistory() {
		return storeHistory;
	}

	/**
	 * 
	 * @param storeHistory
	 *            The store_history
	 */
	@JsonProperty("store_history")
	public void setStoreHistory(Boolean storeHistory) {
		this.storeHistory = storeHistory;
	}

	public ExecuteRequest withStoreHistory(Boolean storeHistory) {
		this.storeHistory = storeHistory;
		return this;
	}

	/**
	 * 
	 * @return The userExpressions
	 */
	@JsonProperty("user_expressions")
	public UserExpressions getUserExpressions() {
		return userExpressions;
	}

	/**
	 * 
	 * @param userExpressions
	 *            The user_expressions
	 */
	@JsonProperty("user_expressions")
	public void setUserExpressions(UserExpressions userExpressions) {
		this.userExpressions = userExpressions;
	}

	public ExecuteRequest withUserExpressions(UserExpressions userExpressions) {
		this.userExpressions = userExpressions;
		return this;
	}

	/**
	 * 
	 * @return The allowStdin
	 */
	@JsonProperty("allow_stdin")
	public Boolean getAllowStdin() {
		return allowStdin;
	}

	/**
	 * 
	 * @param allowStdin
	 *            The allow_stdin
	 */
	@JsonProperty("allow_stdin")
	public void setAllowStdin(Boolean allowStdin) {
		this.allowStdin = allowStdin;
	}

	public ExecuteRequest withAllowStdin(Boolean allowStdin) {
		this.allowStdin = allowStdin;
		return this;
	}

	/**
	 * 
	 * @return The stopOnError
	 */
	@JsonProperty("stop_on_error")
	public Boolean getStopOnError() {
		return stopOnError;
	}

	/**
	 * 
	 * @param stopOnError
	 *            The stop_on_error
	 */
	@JsonProperty("stop_on_error")
	public void setStopOnError(Boolean stopOnError) {
		this.stopOnError = stopOnError;
	}

	public ExecuteRequest withStopOnError(Boolean stopOnError) {
		this.stopOnError = stopOnError;
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

	public ExecuteRequest withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode()).append(code)
				.append(silent).append(storeHistory).append(userExpressions)
				.append(allowStdin).append(stopOnError)
				.append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof ExecuteRequest) == false) {
			return false;
		}
		ExecuteRequest rhs = ((ExecuteRequest) other);
		return new EqualsBuilder().appendSuper(super.equals(other))
				.append(code, rhs.code).append(silent, rhs.silent)
				.append(storeHistory, rhs.storeHistory)
				.append(userExpressions, rhs.userExpressions)
				.append(allowStdin, rhs.allowStdin)
				.append(stopOnError, rhs.stopOnError)
				.append(additionalProperties, rhs.additionalProperties)
				.isEquals();
	}

}
