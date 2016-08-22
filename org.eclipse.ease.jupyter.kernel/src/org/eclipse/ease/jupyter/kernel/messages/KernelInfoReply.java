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
import com.fasterxml.jackson.databind.JsonMappingException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * {@link Content} for kernel_info requests.
 * 
 * Automatically generated from JSON schema using jsonschema2pojo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "protocol_version", "implementation", "implementation_version", "language_info", "banner",
		"help_links" })
public class KernelInfoReply extends Content {

	@JsonProperty("protocol_version")
	private String protocolVersion;
	@JsonProperty("implementation")
	private String implementation;
	@JsonProperty("implementation_version")
	private String implementationVersion;
	@JsonProperty("language_info")
	private LanguageInfo languageInfo;
	@JsonProperty("banner")
	private String banner;

	@JsonProperty("help_links")
	private List<HelpLink> helpLinks = new ArrayList<HelpLink>();
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	public KernelInfoReply() {

	}

	@JsonCreator
	public KernelInfoReply(@JsonProperty(value = "protocol_version", required = true) final String protocolVersion,
			@JsonProperty(value = "implementation", required = true) final String implementation,
			@JsonProperty(value = "implementation_version", required = true) final String implementationVersion,
			@JsonProperty(value = "language_info", required = true) final LanguageInfo languageInfo,
			@JsonProperty(value = "banner", required = true) final String banner,
			@JsonProperty(value = "help_links", required = false) final List<HelpLink> helpLinks) {
		this.protocolVersion = protocolVersion;
		this.implementation = implementation;
		this.implementationVersion = implementationVersion;
		this.languageInfo = languageInfo;
		this.banner = banner;
		if (helpLinks != null) {
			this.helpLinks = helpLinks;
		}
	}

	/**
	 * 
	 * @return The protocolVersion
	 */
	@JsonProperty("protocol_version")
	public String getProtocolVersion() {
		return protocolVersion;
	}

	/**
	 * 
	 * @param protocolVersion
	 *            The protocol_version
	 */
	@JsonProperty("protocol_version")
	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public KernelInfoReply withProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
		return this;
	}

	/**
	 * 
	 * @return The implementation
	 */
	@JsonProperty("implementation")
	public String getImplementation() {
		return implementation;
	}

	/**
	 * 
	 * @param implementation
	 *            The implementation
	 */
	@JsonProperty("implementation")
	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}

	public KernelInfoReply withImplementation(String implementation) {
		this.implementation = implementation;
		return this;
	}

	/**
	 * 
	 * @return The implementationVersion
	 */
	@JsonProperty("implementation_version")
	public String getImplementationVersion() {
		return implementationVersion;
	}

	/**
	 * 
	 * @param implementationVersion
	 *            The implementation_version
	 */
	@JsonProperty("implementation_version")
	public void setImplementationVersion(String implementationVersion) {
		this.implementationVersion = implementationVersion;
	}

	public KernelInfoReply withImplementationVersion(String implementationVersion) {
		this.implementationVersion = implementationVersion;
		return this;
	}

	/**
	 * 
	 * @return The languageInfo
	 */
	@JsonProperty("language_info")
	public LanguageInfo getLanguageInfo() {
		return languageInfo;
	}

	/**
	 * 
	 * @param languageInfo
	 *            The language_info
	 */
	@JsonProperty("language_info")
	public void setLanguageInfo(LanguageInfo languageInfo) {
		this.languageInfo = languageInfo;
	}

	public KernelInfoReply withLanguageInfo(LanguageInfo languageInfo) {
		this.languageInfo = languageInfo;
		return this;
	}

	/**
	 * 
	 * @return The banner
	 */
	@JsonProperty("banner")
	public String getBanner() {
		return banner;
	}

	/**
	 * 
	 * @param banner
	 *            The banner
	 */
	@JsonProperty("banner")
	public void setBanner(String banner) {
		this.banner = banner;
	}

	public KernelInfoReply withBanner(String banner) {
		this.banner = banner;
		return this;
	}

	/**
	 * 
	 * @return The helpLinks
	 */
	@JsonProperty("help_links")
	public List<HelpLink> getHelpLinks() {
		return helpLinks;
	}

	/**
	 * 
	 * @param helpLinks
	 *            The help_links
	 */
	@JsonProperty("help_links")
	public void setHelpLinks(List<HelpLink> helpLinks) {
		this.helpLinks = helpLinks;
	}

	public KernelInfoReply withHelpLinks(List<HelpLink> helpLinks) {
		this.helpLinks = helpLinks;
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

	public KernelInfoReply withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode()).append(protocolVersion).append(implementation)
				.append(implementationVersion).append(languageInfo).append(banner).append(helpLinks)
				.append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof KernelInfoReply) == false) {
			return false;
		}
		KernelInfoReply rhs = ((KernelInfoReply) other);
		return new EqualsBuilder().appendSuper(super.equals(other)).append(protocolVersion, rhs.protocolVersion)
				.append(implementation, rhs.implementation).append(implementationVersion, rhs.implementationVersion)
				.append(languageInfo, rhs.languageInfo).append(banner, rhs.banner).append(helpLinks, rhs.helpLinks)
				.append(additionalProperties, rhs.additionalProperties).isEquals();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ease.jupyter.kernel.messages.Content#validate()
	 */
	@Override
	public void validate() throws JsonMappingException {
		if (this.protocolVersion == null || this.implementation == null || this.implementationVersion == null
				|| this.languageInfo == null || this.banner == null) {
			throw new JsonMappingException("Missing parameter.");
		}
	}

}
