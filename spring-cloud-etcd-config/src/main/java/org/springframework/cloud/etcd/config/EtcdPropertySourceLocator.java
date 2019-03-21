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

package org.springframework.cloud.etcd.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mousio.etcd4j.EtcdClient;

import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

/**
 * @author Luca Burgazzoli
 * @author Spencer Gibb
 */
public class EtcdPropertySourceLocator implements PropertySourceLocator {
	private final EtcdClient etcd;
	private final EtcdConfigProperties properties;

	public EtcdPropertySourceLocator(EtcdClient etcd, EtcdConfigProperties properties) {
		this.etcd = etcd;
		this.properties = properties;
	}

	@Override
	public PropertySource<?> locate(Environment environment) {
		if (environment instanceof ConfigurableEnvironment) {
			final ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
			final String[] profiles = env.getActiveProfiles();
			final List<String> contexts = new ArrayList<>();

			setupContext(contexts, profiles, this.properties.getPrefix(),
					this.properties.getDefaultContext());

			setupContext(contexts, profiles, this.properties.getPrefix(),
					env.getProperty(EtcdConstants.PROPERTY_SPRING_APPLICATION_NAME));

			CompositePropertySource composite = new CompositePropertySource(
					EtcdConstants.NAME);
			Collections.reverse(contexts);

			for (String context : contexts) {
				EtcdPropertySource propertySource = new EtcdPropertySource(context, etcd, properties);
				propertySource.init();

				composite.addPropertySource(propertySource);
			}

			return composite;
		}

		return null;
	}

	private void setupContext(List<String> contexts, String[] profiles, String prefix,
			String item) {
		String ctx = prefix + EtcdConstants.PATH_SEPARATOR + item;
		if (ctx.startsWith(EtcdConstants.PATH_SEPARATOR)) {
			ctx = ctx.substring(1);
		}

		contexts.add(ctx);

		for (String profile : profiles) {
			contexts.add(ctx + this.properties.getProfileSeparator() + profile);
		}
	}
}
