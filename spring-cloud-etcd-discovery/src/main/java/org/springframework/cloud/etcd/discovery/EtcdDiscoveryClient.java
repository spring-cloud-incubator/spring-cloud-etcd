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

package org.springframework.cloud.etcd.discovery;

import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import org.springframework.beans.BeansException;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author Spencer Gibb
 * @author Venil Noronha
 */
public class EtcdDiscoveryClient implements DiscoveryClient, ApplicationContextAware {

	private final EtcdClient etcd;

	private final EtcdDiscoveryProperties properties;

	private final EtcdRegistration registration;

	private ApplicationContext context;

	public EtcdDiscoveryClient(EtcdClient etcd, EtcdRegistration registration, EtcdDiscoveryProperties properties) {
		this.etcd = etcd;
		this.registration = registration;
		this.properties = properties;
	}

	@Override
	public String description() {
		return "Spring Cloud etcd Discovery Client";
	}

	@Override
	public List<ServiceInstance> getInstances(final String serviceId) {
		List<ServiceInstance> instances = null;
		try {
			EtcdKeysResponse response = etcd.getDir(getAppKey(registration.getService().getAppName())).send().get();
			List<EtcdKeysResponse.EtcdNode> nodes = response.node.nodes;
			instances = new ArrayList<>();
			for (EtcdKeysResponse.EtcdNode node : nodes) {
				String[] parts = node.value.split(":");
				instances.add(new DefaultServiceInstance(serviceId, parts[0], Integer.parseInt(parts[1]), false));
			}
		} catch (IOException | TimeoutException | EtcdException | EtcdAuthenticationException e) {
			ReflectionUtils.rethrowRuntimeException(e);
		}
		return instances;
	}

	private String getAppKey(String appName) {
		return properties.getDiscoveryPrefix() + "/" + appName;
	}

	@Override
	public List<String> getServices() {
		List<String> services = null;
		try {
			EtcdKeysResponse response = etcd.getDir(properties.getDiscoveryPrefix()).send().get();
			List<EtcdKeysResponse.EtcdNode> nodes = response.node.nodes;
			services = new ArrayList<>();
			for (EtcdKeysResponse.EtcdNode node : nodes) {
				String serviceId = node.key.replace(properties.getDiscoveryPrefix(), "");
				serviceId = serviceId.substring(1);
				services.add(serviceId);
			}
		} catch (IOException | EtcdException | TimeoutException | EtcdAuthenticationException e) {
			ReflectionUtils.rethrowRuntimeException(e);
		}
		return services;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}

	public EtcdRegistration  getRegistration() {
		return registration;
	}

	public EtcdDiscoveryProperties getProperties() {
		return properties;
	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EtcdDiscoveryClient that = (EtcdDiscoveryClient) o;

		if (etcd != null ? !etcd.equals(that.etcd) : that.etcd != null) return false;
		if (registration != null ? !registration.equals(that.registration) : that.registration != null) return false;
		if (properties != null ? !properties.equals(that.properties) : that.properties != null) return false;
		return context != null ? context.equals(that.context) : that.context == null;
	}

	@Override
	public int hashCode() {
		int result = etcd != null ? etcd.hashCode() : 0;
		result = 31 * result + (registration != null ? registration.hashCode() : 0);
		result = 31 * result + (properties != null ? properties.hashCode() : 0);
		result = 31 * result + (context != null ? context.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return String.format("EtcdDiscoveryClient{etcd=%s, registration=%s, properties=%s, context=%s}", etcd, registration, properties, context);
	}
}
