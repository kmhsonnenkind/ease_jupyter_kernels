package org.eclipse.ease.jupyter.kernel.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
 * {@link Content} for replies to history requests.
 * 
 * Automatically generated from JSON schema using jsonschema2pojo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "history" })
public class HistoryReply extends Content {

	@JsonProperty("history")
	private List<List<Object>> history = new ArrayList<List<Object>>();
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * 
	 * @return The history
	 */
	@JsonProperty("history")
	public List<List<Object>> getHistory() {
		return history;
	}

	/**
	 * 
	 * @param history
	 *            The history
	 */
	@JsonProperty("history")
	public void setHistory(List<List<Object>> history) {
		this.history = history;
	}

	public HistoryReply withHistory(List<List<Object>> history) {
		this.history = history;
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

	public HistoryReply withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode())
				.append(history).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof HistoryReply) == false) {
			return false;
		}
		HistoryReply rhs = ((HistoryReply) other);
		return new EqualsBuilder().appendSuper(super.equals(other))
				.append(history, rhs.history)
				.append(additionalProperties, rhs.additionalProperties)
				.isEquals();
	}

}
