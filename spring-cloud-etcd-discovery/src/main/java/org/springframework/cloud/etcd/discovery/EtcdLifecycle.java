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

package org.springframework.cloud.etcd.discovery;

import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.client.discovery.AbstractDiscoveryLifecycle;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Spencer Gibb
 */
public class EtcdLifecycle extends AbstractDiscoveryLifecycle {

	private static final Log log = LogFactory.getLog(EtcdLifecycle.class);

	private final EtcdClient etcd;
	private final EtcdDiscoveryProperties props;
	private final Service service = new Service();

	public EtcdLifecycle(EtcdClient etcd, EtcdDiscoveryProperties props) {
		this.etcd = etcd;
		this.props = props;
	}

	@Override
	protected void register() {
		Assert.notNull(service.getPort(), "service.port has not been set");

		service.setAppName(getAppName());
		service.setId(getContext().getId());

		register(service);
	}

	@Scheduled(initialDelayString = "${spring.cloud.etcd.discovery.heartbeatInterval:25000}", fixedRateString = "${spring.cloud.etcd.discovery.heartbeatInterval:25000}")
	protected void sendHeartbeat() {
		register();
	}

	@Override
	protected void registerManagement() {
		Service management = new Service();
		management.setId(getManagementServiceId());
		management.setAppName(getManagementServiceName());
		management.setPort(getManagementPort());

		register(management);
	}

	protected void register(Service service) {
		try {
			log.info("Registering service with etcd: " + service);
			String key = getServiceKey(service.appName, service.getId());
			//TODO: what should be serialized about the service?
			String value = props.getHostname() + ":" + service.getPort();
			etcd.put(key, value).ttl(props.getTtl()).send().get();
		} catch (IOException | TimeoutException | EtcdException e) {
			ReflectionUtils.rethrowRuntimeException(e);
		}
	}

	private String getServiceKey(String appName, String serviceId) {
		return getAppKey(appName) + "/" + serviceId;
	}

	public String getAppKey(String appName) {
		return props.getDiscoveryPrefix() + "/" + appName;
	}

	class Service {
		String appName;
		String id;
		Integer port;

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
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Service service = (Service) o;

			if (appName != null ? !appName.equals(service.appName) : service.appName != null) return false;
			if (id != null ? !id.equals(service.id) : service.id != null) return false;
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

	@Override
	protected int getConfiguredPort() {
		return service.getPort() == null? 0 : service.getPort();
	}

	@Override
	protected void setConfiguredPort(int port) {
		service.setPort(port);
	}

	@Override
	protected EtcdDiscoveryProperties getConfiguration() {
		return props;
	}

	@Override
	protected void deregister() {
		deregister(getAppName(), getContext().getId());
	}

	@Override
	protected void deregisterManagement() {
		deregister(getAppName(), getManagementServiceName());
	}

	private void deregister(String appName, String serviceId) {
		try {
			etcd.delete(getServiceKey(appName, serviceId)).send();
		} catch (IOException e) {
			ReflectionUtils.rethrowRuntimeException(e);
		}
	}

	@Override
	protected boolean isEnabled() {
		return props.isEnabled();
	}

	public EtcdClient getEtcd() {
		return etcd;
	}

	public EtcdDiscoveryProperties getProps() {
		return props;
	}

	public Service getService() {
		return service;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EtcdLifecycle that = (EtcdLifecycle) o;

		if (etcd != null ? !etcd.equals(that.etcd) : that.etcd != null) return false;
		if (props != null ? !props.equals(that.props) : that.props != null) return false;
		if (service != null ? !service.equals(that.service) : that.service != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = etcd != null ? etcd.hashCode() : 0;
		result = 31 * result + (props != null ? props.hashCode() : 0);
		result = 31 * result + (service != null ? service.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return String.format("EtcdLifecycle{etcd=%s, props=%s, service=%s}", etcd, props, service);
	}
}
