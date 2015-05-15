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
package co.pishfa.accelerate.cdi;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

/**
 * A simple mock HttpSession
 */
@Veto
public class MockHttpSession implements HttpSession {
	private final String sessionId;

	public MockHttpSession(final String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public long getCreationTime() {
		return 0;
	}

	@Override
	public String getId() {
		return sessionId;
	}

	@Override
	public long getLastAccessedTime() {
		return 0;
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	public void setMaxInactiveInterval(final int interval) {
	}

	@Override
	public int getMaxInactiveInterval() {
		return 0;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return null;
	}

	@Override
	public Object getAttribute(final String name) {
		return null;
	}

	@Override
	public Object getValue(final String name) {
		return null;
	}

	@Override
	public Enumeration getAttributeNames() {
		return null;
	}

	@Override
	public String[] getValueNames() {
		return new String[0];
	}

	@Override
	public void setAttribute(final String name, final Object value) {
	}

	@Override
	public void putValue(final String name, final Object value) {
	}

	@Override
	public void removeAttribute(final String name) {
	}

	@Override
	public void removeValue(final String name) {
	}

	@Override
	public void invalidate() {
	}

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		MockHttpSession that = (MockHttpSession) o;

		if (sessionId != null ? !sessionId.equals(that.sessionId) : that.sessionId != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return sessionId != null ? sessionId.hashCode() : 0;
	}
}
