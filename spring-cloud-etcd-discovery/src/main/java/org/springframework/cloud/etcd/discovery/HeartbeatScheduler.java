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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeoutException;

import mousio.etcd4j.responses.EtcdAuthenticationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdException;

/**
 * @author Venil Noronha
 */
public class HeartbeatScheduler {

	private static Log log = LogFactory.getLog(HeartbeatScheduler.class);

	private final Map<String, ScheduledFuture> serviceHeartbeats = new ConcurrentHashMap<>();

	private final TaskScheduler scheduler = new ConcurrentTaskScheduler(Executors.newSingleThreadScheduledExecutor());

	private EtcdClient client;

	private EtcdDiscoveryProperties properties;

	public HeartbeatScheduler(EtcdClient client, EtcdDiscoveryProperties properties) {
		this.client = client;
		this.properties = properties;
	}

	public void add(Service service) {
		ScheduledFuture task = scheduler.scheduleAtFixedRate(new EtcdHeartbeatTask(service),
				properties.getHeartbeatInterval());
		ScheduledFuture previousTask = serviceHeartbeats.put(service.getId(), task);
		if (previousTask != null) {
			previousTask.cancel(true);
		}
	}

	public void remove(String serviceId) {
		ScheduledFuture task = serviceHeartbeats.get(serviceId);
		if (task != null) {
			task.cancel(true);
		}
		serviceHeartbeats.remove(serviceId);
	}

	public void register(Service service) throws IOException, EtcdException, TimeoutException, EtcdAuthenticationException {
		String key = getServiceKey(service.getAppName(), service.getId());
		// TODO: what should be serialized about the service?
		String value = properties.getHostname() + ":" + service.getPort();
		client.put(key, value).ttl(properties.getTtl()).send().get();
	}

	private String getServiceKey(String appName, String serviceId) {
		return getAppKey(appName) + "/" + serviceId;
	}

	public String getAppKey(String appName) {
		return properties.getDiscoveryPrefix() + "/" + appName;
	}

	private class EtcdHeartbeatTask implements Runnable {

		private Service service;

		EtcdHeartbeatTask(Service service) {
			this.service = service;
		}

		@Override
		public void run() {
			log.debug("Sending etcd heartbeat for: " + service.getId());
			try {
				register(service);
			}
			catch (IOException | EtcdException | TimeoutException | EtcdAuthenticationException e) {
				log.error("Failed to send etcd heartbeat for: " + service.getId(), e);
			}
		}

	}

}
