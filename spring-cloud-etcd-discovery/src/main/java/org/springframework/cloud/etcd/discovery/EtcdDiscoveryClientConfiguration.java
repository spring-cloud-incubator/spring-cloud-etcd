/*
 * Copyright 2013-2017 the original author or authors.
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.etcd.serviceregistry.EtcdRegistration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Spencer Gibb
 * @author Venil Noronha
 */
@Configuration
@ConditionalOnProperty(value = "spring.cloud.etcd.discovery.enabled", matchIfMissing = true)
@EnableScheduling
@EnableConfigurationProperties
public class EtcdDiscoveryClientConfiguration {

	@Autowired
	private EtcdClient client;

	@Bean
	@ConditionalOnMissingBean
	public HeartbeatScheduler heartbeatScheduler(EtcdDiscoveryProperties properties) {
		return new HeartbeatScheduler(client, properties);
	}

	@Bean
	public EtcdDiscoveryProperties etcdDiscoveryProperties() {
		return new EtcdDiscoveryProperties();
	}

	@Bean
	@ConditionalOnMissingBean
	public EtcdDiscoveryClient etcdDiscoveryClient(EtcdDiscoveryProperties properties, EtcdRegistration registration) {
		return new EtcdDiscoveryClient(client, properties, registration);
	}

}
