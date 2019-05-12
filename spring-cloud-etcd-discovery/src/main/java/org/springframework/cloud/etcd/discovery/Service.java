/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.cloud.etcd.discovery;

/**
 * @author Venil Noronha
 */
public class Service {

	private String appName;
	private String id;
	private Integer port;

	public Service() {
	}

	public Service(String appName, String id, Integer port) {
		this.appName = appName;
		this.id = id;
		this.port = port;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Service service = (Service) o;

		if (appName != null ? !appName.equals(service.appName) : service.appName != null)
			return false;
		if (id != null ? !id.equals(service.id) : service.id != null)
			return false;
		return port != null ? port.equals(service.port) : service.port == null;
	}

	@Override
	public int hashCode() {
		int result = appName != null ? appName.hashCode() : 0;
		result = 31 * result + (id != null ? id.hashCode() : 0);
		result = 31 * result + (port != null ? port.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return String.format("Service{appName='%s', id='%s', port=%d}", appName, id, port);
	}

}
