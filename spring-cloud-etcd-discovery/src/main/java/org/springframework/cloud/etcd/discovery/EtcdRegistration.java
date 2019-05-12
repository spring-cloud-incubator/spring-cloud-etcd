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

import javax.servlet.ServletContext;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.discovery.ManagementServerPortUtils;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Venil Noronha
 * @author qiyi
 */
public class EtcdRegistration implements Registration {

	public static final char SEPARATOR = ':';
	public static final String MANAGEMENT = "management";

	private final Service service;
	private final EtcdDiscoveryProperties properties;
	private final ApplicationContext context;
	private String instanceId;

	public EtcdRegistration(Service service, EtcdDiscoveryProperties properties, ApplicationContext context) {
		this.service = service;
		this.properties = properties;
		this.context = context;

		// cache instanceId, so on refresh this won't get recomputed
		// this is a problem if ${random.value} is used
		this.instanceId = EtcdRegistration.getServiceId(context);
	}

	public String getInstanceId() {
		return this.instanceId;
	}

	public void initializePort(int knownPort) {
		if (getService().getPort() == null) {
			// not set by properties
			getService().setPort(knownPort);
		}
	}

	public EtcdRegistration managementRegistration() {
		return managementRegistration(this.properties, this.context);
	}

	public static EtcdRegistration registration(EtcdDiscoveryProperties properties, ApplicationContext context,
			ServletContext servletContext) {
		Service service = new Service();
		service.setAppName(getAppName(properties, context.getEnvironment()));
		service.setId(getServiceId(context));

		return new EtcdRegistration(service, properties, context);
	}

	public static EtcdRegistration managementRegistration(EtcdDiscoveryProperties properties,
			ApplicationContext context) {
		Service management = new Service();
		management.setAppName(getManagementServiceName(properties, context.getEnvironment()));
		management.setId(getManagementServiceId(properties, context));
		management.setPort(getManagementPort(properties, context));

		return new EtcdRegistration(management, properties, context);
	}

	public String getServiceId() {
		return this.service.getId();
	}

	@Override
	public String getHost() {
		return this.properties.getHostname();
	}

	@Override
	public int getPort() {
		return this.service.getPort();
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public URI getUri() {
	    return DefaultServiceInstance.getUri(this);
	}

	@Override
	public Map<String, String> getMetadata() {
		return new HashMap<>();
	}

	public static String getServiceId(ApplicationContext context) {
		return context.getId();
	}

	/**
	 * @return the app name, currently the spring.application.name property
	 */
	public static String getAppName(EtcdDiscoveryProperties properties, Environment environment) {
		return environment.getProperty("spring.application.name", "application");
	}

	/**
	 * @return if the management service should be registered with the
	 *         {@link ServiceRegistry}
	 */
	public static boolean shouldRegisterManagement(EtcdDiscoveryProperties properties, ApplicationContext context) {
		return getManagementPort(properties, context) != null && ManagementServerPortUtils.isDifferent(context);
	}

	/**
	 * @return the serviceId of the Management Service
	 */
	public static String getManagementServiceId(EtcdDiscoveryProperties properties, ApplicationContext context) {
		return context.getId() + SEPARATOR + MANAGEMENT;
	}

	/**
	 * @return the service name of the Management Service
	 */
	public static String getManagementServiceName(EtcdDiscoveryProperties properties,
			Environment environment) {
		return getAppName(properties, environment) + SEPARATOR + MANAGEMENT;
	}

	/**
	 * @return the port of the Management Service
	 */
	public static Integer getManagementPort(EtcdDiscoveryProperties properties, ApplicationContext context) {
		return ManagementServerPortUtils.getPort(context);
	}

	public Service getService() {
		return service;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EtcdRegistration other = (EtcdRegistration) obj;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
			return false;
		return true;
	}

}
