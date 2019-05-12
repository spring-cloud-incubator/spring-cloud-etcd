/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.etcd;

import mousio.etcd4j.EtcdClient;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Spencer Gibb
 */
@ConfigurationProperties(prefix = "endpoints.etcd", ignoreUnknownFields = false)
@Endpoint(id="etcd")
public class EtcdEndpoint {

	private EtcdClient etcd;

	public EtcdEndpoint(EtcdClient etcd) {
		this.etcd = etcd;
	}

	@ReadOperation
	public Data invoke() {
		Data data = new Data();
		data.setVersion(etcd.getVersion());
		return data;
	}

	public static class Data {
		private String version;

		public Data() {
		}

		public Data(String version) {
			this.version = version;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Data data = (Data) o;

			return version != null ? version.equals(data.version) : data.version == null;
		}

		@Override
		public int hashCode() {
			return version != null ? version.hashCode() : 0;
		}

		@Override
		public String toString() {
			return String.format("Data{version='%s'}", version);
		}
	}
}
