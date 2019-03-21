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

import java.net.URI;

import mousio.etcd4j.EtcdClient;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Spencer Gibb
 */
@Configuration
@EnableConfigurationProperties
public class EtcdAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public EtcdClient etcdClient() {
		// TODO: support ssl
		// TODO: support authentication
		return new EtcdClient(etcdProperties().getUris().toArray(new URI[] {}));
	}

	@Bean
	@ConditionalOnMissingBean
	public EtcdProperties etcdProperties() {
		return new EtcdProperties();
	}

	@Bean
	@ConditionalOnMissingBean
	public EtcdEndpoint etcdEndpoint() {
		return new EtcdEndpoint(etcdClient());
	}

	@Bean
	@ConditionalOnMissingBean
	public EtcdHealthIndicator etcdHealthIndicator(EtcdClient client) {
		return new EtcdHealthIndicator(client);
	}
}
