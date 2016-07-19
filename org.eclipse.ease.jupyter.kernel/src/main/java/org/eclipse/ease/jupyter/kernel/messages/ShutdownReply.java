
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
 * {@link Content} for replies to shutdown requests.
 * 
 * Automatically generated from JSON schema using jsonschema2pojo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "restart", "status" })
public class ShutdownReply extends Content {

	@JsonProperty("restart")
	private Boolean restart;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * 
	 * @return The restart
	 */
	@JsonProperty("restart")
	public Boolean getRestart() {
		return restart;
	}

	/**
	 * 
	 * @param restart
	 *            The restart
	 */
	@JsonProperty("restart")
	public void setRestart(Boolean restart) {
		this.restart = restart;
	}

	public ShutdownReply withRestart(Boolean restart) {
		this.restart = restart;
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

	public ShutdownReply withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode()).append(restart).append(additionalProperties)
				.toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof ShutdownReply) == false) {
			return false;
		}
		ShutdownReply rhs = ((ShutdownReply) other);
		return new EqualsBuilder().appendSuper(super.equals(other)).append(restart, rhs.restart)
				.append(additionalProperties, rhs.additionalProperties).isEquals();
	}

}
