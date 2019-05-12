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

package org.springframework.cloud.etcd.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Spencer Gibb
 */
@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class SampleEtcdApplication {

	public static final String CLIENT_NAME = "testEtcdApp";

	@Autowired
	private LoadBalancerClient loadBalancer;

	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired
	private Registration registration;

	@Autowired
	private Environment env;

	@RequestMapping("/me")
	public ServiceInstance me() {
		return this.registration;
	}

	@RequestMapping("/")
	public ServiceInstance lb() {
		return loadBalancer.choose(CLIENT_NAME);
	}

	@RequestMapping("/myenv")
	public String env(@RequestParam("prop") String prop) {
		return env.getProperty(prop, "Not Found");
	}

	@RequestMapping("/all")
	public List<ServiceInstance> all() {
		return discoveryClient.getInstances(CLIENT_NAME);
	}

	public static void main(String[] args) {
		SpringApplication.run(SampleEtcdApplication.class, args);
	}
}
