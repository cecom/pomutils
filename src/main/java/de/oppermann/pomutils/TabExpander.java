package de.oppermann.pomutils;

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

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.codehaus.plexus.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts indentation-tabs to configurable amount of spaces.
 * No support for Unicode-Spaces (other then standard 0x20 character).<br>
 * <br>
 * Example:
 * <code><blockquote>
 * \t\tString text = "\t";
 * </blockquote></code>
 *
 * replacing tabs with 4 spaces get converted into
 *
 * <code><blockquote>
 * ........String text = "\t";
 * </blockquote></code>
 * (`<code>.</code>´ represents a space character).<br>
 * 
 * @author Boris Brodski <brodsky_boris@yahoo.com>
 *
 */
public class TabExpander {
	private final Logger logger = LoggerFactory.getLogger(TabExpander.class);

	private String filename;
	private int spaceCount;

	public TabExpander(String filename, int spaceCount) {
		this.filename = filename;
		this.spaceCount = spaceCount;
	}

	public void expand() {
		logger.debug("Expanding indenting tabs into {} spaces [file={}]", spaceCount, filename);

		byte[] fileInMemory = readFileIntoMemory();

		boolean ok = false;
		OutputStream outputStream = null;
		try {
			outputStream = new BufferedOutputStream(new FileOutputStream(filename));
			copyExpandingTabs(fileInMemory, outputStream);

			ok = true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					if (ok) {
						throw new RuntimeException(e);
					} else {
						logger.error("Error closing output file " + filename, e);
					}
				}
			}
		}
	}

	private void copyExpandingTabs(byte[] fileInMemory, OutputStream outputStream) throws IOException {
		byte[] indentSpaces = new byte[spaceCount];
		Arrays.fill(indentSpaces, (byte)' ');
		boolean inIndent = true;
		for (int i = 0; i < fileInMemory.length; i++) {
			switch (fileInMemory[i]) {
			case '\n':
			case '\r':
				inIndent = true;
				break;

			case ' ':
				break;

			case '\t':
				if (inIndent) {
					outputStream.write(indentSpaces);
					continue;
				}
				break;

			default:
				inIndent = false;
			}

			outputStream.write(fileInMemory[i]);
		}
	}

	private byte[] readFileIntoMemory() {
		InputStream inputStream = null;
		byte[] fileInMemory;
		boolean ok = false;
		try {
			inputStream = new FileInputStream(filename);
			fileInMemory = IOUtil.toByteArray(inputStream);
			ok = true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					if (ok) {
						throw new RuntimeException(e);
					} else {
						logger.error("Error closing input file " + filename, e);
					}
				}
			}
		}
		return fileInMemory;
	}

}
