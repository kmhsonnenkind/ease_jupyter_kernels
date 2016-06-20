package org.eclipse.ease.jupyter.kernel;

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
 * Stores information about a Jupyter configuration file.
 * 
 * Automatically generated from JSON schema using jsonschema2pojo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "ip", "transport", "key", "signature_scheme",
		"control_port", "hb_port", "shell_port", "iopub_port", "stdin_port" })
public class Config {

	@JsonProperty("ip")
	private String ip;
	@JsonProperty("transport")
	private String transport;
	@JsonProperty("key")
	private String key;
	@JsonProperty("signature_scheme")
	private String signatureScheme;
	@JsonProperty("control_port")
	private Integer controlPort;
	@JsonProperty("hb_port")
	private Integer hbPort;
	@JsonProperty("shell_port")
	private Integer shellPort;
	@JsonProperty("iopub_port")
	private Integer iopubPort;
	@JsonProperty("stdin_port")
	private Integer stdinPort;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * 
	 * (Required)
	 * 
	 * @return The ip
	 */
	@JsonProperty("ip")
	public String getIp() {
		return ip;
	}

	/**
	 * 
	 * (Required)
	 * 
	 * @param ip
	 *            The ip
	 */
	@JsonProperty("ip")
	public void setIp(String ip) {
		this.ip = ip;
	}

	public Config withIp(String ip) {
		this.ip = ip;
		return this;
	}

	/**
	 * 
	 * (Required)
	 * 
	 * @return The transport
	 */
	@JsonProperty("transport")
	public String getTransport() {
		return transport;
	}

	/**
	 * 
	 * (Required)
	 * 
	 * @param transport
	 *            The transport
	 */
	@JsonProperty("transport")
	public void setTransport(String transport) {
		this.transport = transport;
	}

	public Config withTransport(String transport) {
		this.transport = transport;
		return this;
	}

	/**
	 * 
	 * @return The key
	 */
	@JsonProperty("key")
	public String getKey() {
		return key;
	}

	/**
	 * 
	 * @param key
	 *            The key
	 */
	@JsonProperty("key")
	public void setKey(String key) {
		this.key = key;
	}

	public Config withKey(String key) {
		this.key = key;
		return this;
	}

	/**
	 * 
	 * @return The signatureScheme
	 */
	@JsonProperty("signature_scheme")
	public String getSignatureScheme() {
		return signatureScheme;
	}

	/**
	 * 
	 * @param signatureScheme
	 *            The signature_scheme
	 */
	@JsonProperty("signature_scheme")
	public void setSignatureScheme(String signatureScheme) {
		this.signatureScheme = signatureScheme;
	}

	public Config withSignatureScheme(String signatureScheme) {
		this.signatureScheme = signatureScheme;
		return this;
	}

	/**
	 * 
	 * @return The controlPort
	 */
	@JsonProperty("control_port")
	public Integer getControlPort() {
		return controlPort;
	}

	/**
	 * 
	 * @param controlPort
	 *            The control_port
	 */
	@JsonProperty("control_port")
	public void setControlPort(Integer controlPort) {
		this.controlPort = controlPort;
	}

	public Config withControlPort(Integer controlPort) {
		this.controlPort = controlPort;
		return this;
	}

	/**
	 * 
	 * @return The hbPort
	 */
	@JsonProperty("hb_port")
	public Integer getHbPort() {
		return hbPort;
	}

	/**
	 * 
	 * @param hbPort
	 *            The hb_port
	 */
	@JsonProperty("hb_port")
	public void setHbPort(Integer hbPort) {
		this.hbPort = hbPort;
	}

	public Config withHbPort(Integer hbPort) {
		this.hbPort = hbPort;
		return this;
	}

	/**
	 * 
	 * @return The shellPort
	 */
	@JsonProperty("shell_port")
	public Integer getShellPort() {
		return shellPort;
	}

	/**
	 * 
	 * @param shellPort
	 *            The shell_port
	 */
	@JsonProperty("shell_port")
	public void setShellPort(Integer shellPort) {
		this.shellPort = shellPort;
	}

	public Config withShellPort(Integer shellPort) {
		this.shellPort = shellPort;
		return this;
	}

	/**
	 * 
	 * @return The iopubPort
	 */
	@JsonProperty("iopub_port")
	public Integer getIopubPort() {
		return iopubPort;
	}

	/**
	 * 
	 * @param iopubPort
	 *            The iopub_port
	 */
	@JsonProperty("iopub_port")
	public void setIopubPort(Integer iopubPort) {
		this.iopubPort = iopubPort;
	}

	public Config withIopubPort(Integer iopubPort) {
		this.iopubPort = iopubPort;
		return this;
	}

	/**
	 * 
	 * @return The stdinPort
	 */
	@JsonProperty("stdin_port")
	public Integer getStdinPort() {
		return stdinPort;
	}

	/**
	 * 
	 * @param stdinPort
	 *            The stdin_port
	 */
	@JsonProperty("stdin_port")
	public void setStdinPort(Integer stdinPort) {
		this.stdinPort = stdinPort;
	}

	public Config withStdinPort(Integer stdinPort) {
		this.stdinPort = stdinPort;
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

	public Config withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(ip).append(transport).append(key)
				.append(signatureScheme).append(controlPort).append(hbPort)
				.append(shellPort).append(iopubPort).append(stdinPort)
				.append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Config) == false) {
			return false;
		}
		Config rhs = ((Config) other);
		return new EqualsBuilder().append(ip, rhs.ip)
				.append(transport, rhs.transport).append(key, rhs.key)
				.append(signatureScheme, rhs.signatureScheme)
				.append(controlPort, rhs.controlPort)
				.append(hbPort, rhs.hbPort).append(shellPort, rhs.shellPort)
				.append(iopubPort, rhs.iopubPort)
				.append(stdinPort, rhs.stdinPort)
				.append(additionalProperties, rhs.additionalProperties)
				.isEquals();
	}

}
