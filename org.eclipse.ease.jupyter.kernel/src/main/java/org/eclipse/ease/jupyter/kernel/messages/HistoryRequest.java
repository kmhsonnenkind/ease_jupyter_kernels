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
 * {@link Content} for replies to history requests.
 * 
 * Automatically generated from JSON schema using jsonschema2pojo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "output", "raw", "hist_access_type", "session", "start",
		"stop", "n", "pattern", "unique" })
public class HistoryRequest extends Content {

	@JsonProperty("output")
	private Boolean output = false;
	@JsonProperty("raw")
	private Boolean raw = false;
	/**
	 * 
	 * (Required)
	 * 
	 */
	@JsonProperty("hist_access_type")
	private HistoryRequest.HistAccessType histAccessType;
	@JsonProperty("session")
	private Integer session;
	@JsonProperty("start")
	private Integer start;
	@JsonProperty("stop")
	private Integer stop;
	@JsonProperty("n")
	private Integer n;
	@JsonProperty("pattern")
	private String pattern;
	@JsonProperty("unique")
	private Boolean unique;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * 
	 * @return The output
	 */
	@JsonProperty("output")
	public Boolean getOutput() {
		return output;
	}

	/**
	 * 
	 * @param output
	 *            The output
	 */
	@JsonProperty("output")
	public void setOutput(Boolean output) {
		this.output = output;
	}

	public HistoryRequest withOutput(Boolean output) {
		this.output = output;
		return this;
	}

	/**
	 * 
	 * @return The raw
	 */
	@JsonProperty("raw")
	public Boolean getRaw() {
		return raw;
	}

	/**
	 * 
	 * @param raw
	 *            The raw
	 */
	@JsonProperty("raw")
	public void setRaw(Boolean raw) {
		this.raw = raw;
	}

	public HistoryRequest withRaw(Boolean raw) {
		this.raw = raw;
		return this;
	}

	/**
	 * 
	 * (Required)
	 * 
	 * @return The histAccessType
	 */
	@JsonProperty("hist_access_type")
	public HistoryRequest.HistAccessType getHistAccessType() {
		return histAccessType;
	}

	/**
	 * 
	 * (Required)
	 * 
	 * @param histAccessType
	 *            The hist_access_type
	 */
	@JsonProperty("hist_access_type")
	public void setHistAccessType(HistoryRequest.HistAccessType histAccessType) {
		this.histAccessType = histAccessType;
	}

	public HistoryRequest withHistAccessType(
			HistoryRequest.HistAccessType histAccessType) {
		this.histAccessType = histAccessType;
		return this;
	}

	/**
	 * 
	 * @return The session
	 */
	@JsonProperty("session")
	public Integer getSession() {
		return session;
	}

	/**
	 * 
	 * @param session
	 *            The session
	 */
	@JsonProperty("session")
	public void setSession(Integer session) {
		this.session = session;
	}

	public HistoryRequest withSession(Integer session) {
		this.session = session;
		return this;
	}

	/**
	 * 
	 * @return The start
	 */
	@JsonProperty("start")
	public Integer getStart() {
		return start;
	}

	/**
	 * 
	 * @param start
	 *            The start
	 */
	@JsonProperty("start")
	public void setStart(Integer start) {
		this.start = start;
	}

	public HistoryRequest withStart(Integer start) {
		this.start = start;
		return this;
	}

	/**
	 * 
	 * @return The stop
	 */
	@JsonProperty("stop")
	public Integer getStop() {
		return stop;
	}

	/**
	 * 
	 * @param stop
	 *            The stop
	 */
	@JsonProperty("stop")
	public void setStop(Integer stop) {
		this.stop = stop;
	}

	public HistoryRequest withStop(Integer stop) {
		this.stop = stop;
		return this;
	}

	/**
	 * 
	 * @return The n
	 */
	@JsonProperty("n")
	public Integer getN() {
		return n;
	}

	/**
	 * 
	 * @param n
	 *            The n
	 */
	@JsonProperty("n")
	public void setN(Integer n) {
		this.n = n;
	}

	public HistoryRequest withN(Integer n) {
		this.n = n;
		return this;
	}

	/**
	 * 
	 * @return The pattern
	 */
	@JsonProperty("pattern")
	public String getPattern() {
		return pattern;
	}

	/**
	 * 
	 * @param pattern
	 *            The pattern
	 */
	@JsonProperty("pattern")
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public HistoryRequest withPattern(String pattern) {
		this.pattern = pattern;
		return this;
	}

	/**
	 * 
	 * @return The unique
	 */
	@JsonProperty("unique")
	public Boolean getUnique() {
		return unique;
	}

	/**
	 * 
	 * @param unique
	 *            The unique
	 */
	@JsonProperty("unique")
	public void setUnique(Boolean unique) {
		this.unique = unique;
	}

	public HistoryRequest withUnique(Boolean unique) {
		this.unique = unique;
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

	public HistoryRequest withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode())
				.append(output).append(raw).append(histAccessType)
				.append(session).append(start).append(stop).append(n)
				.append(pattern).append(unique).append(additionalProperties)
				.toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof HistoryRequest) == false) {
			return false;
		}
		HistoryRequest rhs = ((HistoryRequest) other);
		return new EqualsBuilder().appendSuper(super.equals(other))
				.append(output, rhs.output).append(raw, rhs.raw)
				.append(histAccessType, rhs.histAccessType)
				.append(session, rhs.session).append(start, rhs.start)
				.append(stop, rhs.stop).append(n, rhs.n)
				.append(pattern, rhs.pattern).append(unique, rhs.unique)
				.append(additionalProperties, rhs.additionalProperties)
				.isEquals();
	}

	public static enum HistAccessType {

		RANGE("range"), TAIL("tail"), SEARCH("search");
		private final String value;
		private static Map<String, HistoryRequest.HistAccessType> constants = new HashMap<String, HistoryRequest.HistAccessType>();

		static {
			for (HistoryRequest.HistAccessType c : values()) {
				constants.put(c.value, c);
			}
		}

		private HistAccessType(String value) {
			this.value = value;
		}

		@JsonValue
		@Override
		public String toString() {
			return this.value;
		}

		@JsonCreator
		public static HistoryRequest.HistAccessType fromValue(String value) {
			HistoryRequest.HistAccessType constant = constants.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}

	}

}
