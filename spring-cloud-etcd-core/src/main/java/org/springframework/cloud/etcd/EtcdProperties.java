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

package org.springframework.cloud.etcd;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * @author Spencer Gibb
 */
@ConfigurationProperties("spring.cloud.etcd")
public class EtcdProperties {
	@NotNull
	private List<URI> uris = Arrays.asList(URI.create("http://localhost:4001"));

	private boolean enabled = true;

	public EtcdProperties() {
	}

	public EtcdProperties(List<URI> uris, boolean enabled) {
		this.uris = uris;
		this.enabled = enabled;
	}

	public List<URI> getUris() {
		return uris;
	}

	public void setUris(List<URI> uris) {
		this.uris = uris;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EtcdProperties that = (EtcdProperties) o;

		if (enabled != that.enabled) return false;
		return uris != null ? uris.equals(that.uris) : that.uris == null;
	}

	@Override
	public int hashCode() {
		int result = uris != null ? uris.hashCode() : 0;
		result = 31 * result + (enabled ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return String.format("EtcdProperties{uris=%s, enabled=%s}", uris, enabled);
	}
}
