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

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.etcd.discovery.EtcdDiscoveryProperties;
import org.springframework.cloud.etcd.discovery.HeartbeatScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mousio.etcd4j.EtcdClient;

/**
 * @author Venil Noronha
 */
@Configuration
@ConditionalOnProperty(value = "spring.cloud.service-registry.enabled", matchIfMissing = true)
public class EtcdServiceRegistryAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public EtcdServiceRegistry etcdServiceRegistry(EtcdClient etcdClient, EtcdDiscoveryProperties properties,
			HeartbeatScheduler heartbeatScheduler) {
		return new EtcdServiceRegistry(etcdClient, properties, heartbeatScheduler);
	}

	@Bean
	@ConditionalOnMissingBean
	public HeartbeatScheduler heartbeatScheduler(EtcdClient etcdClient, EtcdDiscoveryProperties properties) {
		return new HeartbeatScheduler(etcdClient, properties);
	}

	@Bean
	@ConditionalOnMissingBean
	public EtcdDiscoveryProperties etcdDiscoveryProperties() {
		return new EtcdDiscoveryProperties();
	}

}
