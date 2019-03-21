/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.etcd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * @author Luca Burgazzoli
 * @author Spencer Gibb
 */
@ConfigurationProperties("spring.cloud.etcd.config")
public class EtcdConfigProperties {
	private boolean enabled = true;
	private String prefix = "config";
	private String defaultContext = "application";
	private String profileSeparator = "-";
	private int timeout = 1;
	private TimeUnit timeoutUnit = TimeUnit.SECONDS;

	public EtcdConfigProperties() {
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getDefaultContext() {
		return defaultContext;
	}

	public void setDefaultContext(String defaultContext) {
		this.defaultContext = defaultContext;
	}

	public String getProfileSeparator() {
		return profileSeparator;
	}

	public void setProfileSeparator(String profileSeparator) {
		this.profileSeparator = profileSeparator;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public TimeUnit getTimeoutUnit() {
		return timeoutUnit;
	}

	public void setTimeoutUnit(TimeUnit timeoutUnit) {
		this.timeoutUnit = timeoutUnit;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EtcdConfigProperties that = (EtcdConfigProperties) o;

		if (enabled != that.enabled) return false;
		if (timeout != that.timeout) return false;
		if (prefix != null ? !prefix.equals(that.prefix) : that.prefix != null) return false;
		if (defaultContext != null ? !defaultContext.equals(that.defaultContext) : that.defaultContext != null)
			return false;
		if (profileSeparator != null ? !profileSeparator.equals(that.profileSeparator) : that.profileSeparator != null)
			return false;
		return timeoutUnit == that.timeoutUnit;
	}

	@Override
	public int hashCode() {
		int result = (enabled ? 1 : 0);
		result = 31 * result + (prefix != null ? prefix.hashCode() : 0);
		result = 31 * result + (defaultContext != null ? defaultContext.hashCode() : 0);
		result = 31 * result + (profileSeparator != null ? profileSeparator.hashCode() : 0);
		result = 31 * result + timeout;
		result = 31 * result + (timeoutUnit != null ? timeoutUnit.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return String.format("EtcdConfigProperties{enabled=%s, prefix='%s', defaultContext='%s', profileSeparator='%s', timeout=%d, timeoutUnit=%s}", enabled, prefix, defaultContext, profileSeparator, timeout, timeoutUnit);
	}
}
