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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Language information abstraction for kernel_info requests.
 * 
 * Automatically generated from JSON schema using jsonschema2pojo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "version", "mimetype", "file_extension", "pygments_lexer", "nbconvert_exporter" })
public class LanguageInfo {

	@JsonProperty("name")
	private String name;
	@JsonProperty("version")
	private String version;
	@JsonProperty("mimetype")
	private String mimetype;
	@JsonProperty("file_extension")
	private String fileExtension;
	@JsonProperty("pygments_lexer")
	private String pygmentsLexer;
	@JsonProperty("nbconvert_exporter")
	private String nbconvertExporter;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * 
	 * @return The name
	 */
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 *            The name
	 */
	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	public LanguageInfo withName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * 
	 * @return The version
	 */
	@JsonProperty("version")
	public String getVersion() {
		return version;
	}

	/**
	 * 
	 * @param version
	 *            The version
	 */
	@JsonProperty("version")
	public void setVersion(String version) {
		this.version = version;
	}

	public LanguageInfo withVersion(String version) {
		this.version = version;
		return this;
	}

	/**
	 * 
	 * @return The mimetype
	 */
	@JsonProperty("mimetype")
	public String getMimetype() {
		return mimetype;
	}

	/**
	 * 
	 * @param mimetype
	 *            The mimetype
	 */
	@JsonProperty("mimetype")
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public LanguageInfo withMimetype(String mimetype) {
		this.mimetype = mimetype;
		return this;
	}

	/**
	 * 
	 * @return The fileExtension
	 */
	@JsonProperty("file_extension")
	public String getFileExtension() {
		return fileExtension;
	}

	/**
	 * 
	 * @param fileExtension
	 *            The file_extension
	 */
	@JsonProperty("file_extension")
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public LanguageInfo withFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
		return this;
	}

	/**
	 * 
	 * @return The pygmentsLexer
	 */
	@JsonProperty("pygments_lexer")
	public String getPygmentsLexer() {
		return pygmentsLexer;
	}

	/**
	 * 
	 * @param pygmentsLexer
	 *            The pygments_lexer
	 */
	@JsonProperty("pygments_lexer")
	public void setPygmentsLexer(String pygmentsLexer) {
		this.pygmentsLexer = pygmentsLexer;
	}

	public LanguageInfo withPygmentsLexer(String pygmentsLexer) {
		this.pygmentsLexer = pygmentsLexer;
		return this;
	}

	/**
	 * 
	 * @return The nbconvertExporter
	 */
	@JsonProperty("nbconvert_exporter")
	public String getNbconvertExporter() {
		return nbconvertExporter;
	}

	/**
	 * 
	 * @param nbconvertExporter
	 *            The nbconvert_exporter
	 */
	@JsonProperty("nbconvert_exporter")
	public void setNbconvertExporter(String nbconvertExporter) {
		this.nbconvertExporter = nbconvertExporter;
	}

	public LanguageInfo withNbconvertExporter(String nbconvertExporter) {
		this.nbconvertExporter = nbconvertExporter;
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

	public LanguageInfo withAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
		return this;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name).append(version).append(mimetype).append(fileExtension)
				.append(pygmentsLexer).append(nbconvertExporter).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof LanguageInfo) == false) {
			return false;
		}
		LanguageInfo rhs = ((LanguageInfo) other);
		return new EqualsBuilder().append(name, rhs.name).append(version, rhs.version).append(mimetype, rhs.mimetype)
				.append(fileExtension, rhs.fileExtension).append(pygmentsLexer, rhs.pygmentsLexer)
				.append(nbconvertExporter, rhs.nbconvertExporter).append(additionalProperties, rhs.additionalProperties)
				.isEquals();
	}

}
