package org.eclipse.ease.jupyter.kernel.messages;

import java.util.ArrayList;
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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * {@link Content} for replies to execution requests.
 * 
 * Automatically generated from JSON schema using jsonschema2pojo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "status", "execution_count", "payload",
		"user_expressions", "ename", "evalue", "traceback" })
public class ExecuteReply extends Content {

	@JsonProperty("status")
	private ExecuteReply.Status status;
	@JsonProperty("execution_count")
	private Integer executionCount;
	@JsonProperty("payload")
	private List<Payload> payload = new ArrayList<Payload>();
	@JsonProperty("user_expressions")
	private UserExpressions userExpressions;
	@JsonProperty("ename")
	private String ename;
	@JsonProperty("evalue")
	private String evalue;
	@JsonProperty("traceback")
	private List<String> traceback = new ArrayList<String>();
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * 
	 * @return The status
	 */
	@JsonProperty("status")
	public ExecuteReply.Status getStatus() {
		return status;
	}

	/**
	 * 
	 * @param status
	 *            The status
	 */
	@JsonProperty("status")
	public void setStatus(ExecuteReply.Status status) {
		this.status = status;
	}

	public ExecuteReply withStatus(ExecuteReply.Status status) {
		this.status = status;
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

	public ExecuteReply withExecutionCount(Integer executionCount) {
		this.executionCount = executionCount;
		return this;
	}

	/**
	 * 
	 * @return The payload
	 */
	@JsonProperty("payload")
	public List<Payload> getPayload() {
		return payload;
	}

	/**
	 * 
	 * @param payload
	 *            The payload
	 */
	@JsonProperty("payload")
	public void setPayload(List<Payload> payload) {
		this.payload = payload;
	}

	public ExecuteReply withPayload(List<Payload> payload) {
		this.payload = payload;
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

	public ExecuteReply withUserExpressions(UserExpressions userExpressions) {
		this.userExpressions = userExpressions;
		return this;
	}

	/**
	 * 
	 * @return The ename
	 */
	@JsonProperty("ename")
	public String getEname() {
		return ename;
	}

	/**
	 * 
	 * @param ename
	 *            The ename
	 */
	@JsonProperty("ename")
	public void setEname(String ename) {
		this.ename = ename;
	}

	public ExecuteReply withEname(String ename) {
		this.ename = ename;
		return this;
	}

	/**
	 * 
	 * @return The evalue
	 */
	@JsonProperty("evalue")
	public String getEvalue() {
		return evalue;
	}

	/**
	 * 
	 * @param evalue
	 *            The evalue
	 */
	@JsonProperty("evalue")
	public void setEvalue(String evalue) {
		this.evalue = evalue;
	}

	public ExecuteReply withEvalue(String evalue) {
		this.evalue = evalue;
		return this;
	}

	/**
	 * 
	 * @return The traceback
	 */
	@JsonProperty("traceback")
	public List<String> getTraceback() {
		return traceback;
	}

	/**
	 * 
	 * @param traceback
	 *            The traceback
	 */
	@JsonProperty("traceback")
	public void setTraceback(List<String> traceback) {
		this.traceback = traceback;
	}

	public ExecuteReply withTraceback(List<String> traceback) {
		this.traceback = traceback;
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

	public ExecuteReply withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode())
				.append(status).append(executionCount).append(payload)
				.append(userExpressions).append(ename).append(evalue)
				.append(traceback).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof ExecuteReply) == false) {
			return false;
		}
		ExecuteReply rhs = ((ExecuteReply) other);
		return new EqualsBuilder().appendSuper(super.equals(other))
				.append(status, rhs.status)
				.append(executionCount, rhs.executionCount)
				.append(payload, rhs.payload)
				.append(userExpressions, rhs.userExpressions)
				.append(ename, rhs.ename).append(evalue, rhs.evalue)
				.append(traceback, rhs.traceback)
				.append(additionalProperties, rhs.additionalProperties)
				.isEquals();
	}

	public static enum Status {

		OK("ok"), ERROR("error"), ABORT("abort");
		private final String value;
		private static Map<String, ExecuteReply.Status> constants = new HashMap<String, ExecuteReply.Status>();

		static {
			for (ExecuteReply.Status c : values()) {
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
		public static ExecuteReply.Status fromValue(String value) {
			ExecuteReply.Status constant = constants.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}

	}

}
