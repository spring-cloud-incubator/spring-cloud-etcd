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

package org.springframework.cloud.etcd.serviceregistry;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.cloud.etcd.discovery.EtcdDiscoveryProperties;
import org.springframework.cloud.etcd.discovery.Service;
import org.springframework.cloud.etcd.discovery.HeartbeatScheduler;
import org.springframework.util.ReflectionUtils;

import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdException;

/**
 * @author Venil Noronha
 */
public class EtcdServiceRegistry implements ServiceRegistry<EtcdRegistration> {

	private static Log log = LogFactory.getLog(EtcdServiceRegistry.class);

	private final EtcdClient client;
	private final EtcdDiscoveryProperties properties;
	private final HeartbeatScheduler heartbeatScheduler;

	public EtcdServiceRegistry(EtcdClient client, EtcdDiscoveryProperties properties, HeartbeatScheduler heartbeatScheduler) {
		this.client = client;
		this.properties = properties;
		this.heartbeatScheduler = heartbeatScheduler;
	}

	@Override
	public void register(EtcdRegistration reg) {
		Service service = reg.getService();
		log.info("Registering service with etcd: " + service);
		try {
			heartbeatScheduler.register(service);
		}
		catch (IOException | EtcdException | TimeoutException e) {
			ReflectionUtils.rethrowRuntimeException(e);
		}
		heartbeatScheduler.add(service);
	}

	private String getServiceKey(String appName, String serviceId) {
		return getAppKey(appName) + "/" + serviceId;
	}

	public String getAppKey(String appName) {
		return properties.getDiscoveryPrefix() + "/" + appName;
	}

	@Override
	public void deregister(EtcdRegistration reg) {
		Service service = reg.getService();
		heartbeatScheduler.remove(service.getId());
		if (log.isInfoEnabled()) {
			log.info("Deregistering service with etcd: " + service);
		}
		try {
			client.delete(getServiceKey(service.getAppName(), service.getId())).send();
		}
		catch (IOException e) {
			ReflectionUtils.rethrowRuntimeException(e);
		}
	}

	@Override
	public void close() {

	}

	@Override
	public void setStatus(EtcdRegistration registration, String status) {

	}

	@Override
	public Object getStatus(EtcdRegistration registration) {
		return null;
	}

}
