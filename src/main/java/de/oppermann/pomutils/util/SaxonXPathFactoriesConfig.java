package de.oppermann.pomutils.util;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import net.sf.saxon.Configuration;
import net.sf.saxon.xpath.XPathFactoryImpl;

import org.w3c.dom.Document;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

public class SaxonXPathFactoriesConfig extends DefaultXMLFactoriesConfig {

	protected Configuration saxonConfig = new Configuration();

	@Override
	public XPathFactory createXPathFactory() {
		XPathFactoryImpl impl = new XPathFactoryImpl(saxonConfig);
		return impl;
	}

	@Override
	public XPath createXPath(Document... document) {
		XPath xpath = super.createXPath(document);

		xpath.setNamespaceContext(new MavenNamespaceContext());
		return xpath;

	}

	private static class MavenNamespaceContext implements NamespaceContext {

		public String getNamespaceURI(String prefix) {
			if (prefix == null) {
				throw new IllegalArgumentException("No prefix provided!");
			}
			if ("m".equals(prefix)) {
				return "http://maven.apache.org/POM/4.0.0";
			}
			return XMLConstants.NULL_NS_URI;
		}

		public String getPrefix(String namespaceURI) {
			// Not needed in this context.
			return null;
		}

		public Iterator getPrefixes(String namespaceURI) {
			// Not needed in this context.
			return null;
		}

	}

}
