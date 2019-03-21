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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author Spencer Gibb
 */
@ConfigurationProperties("spring.cloud.etcd.discovery")
public class EtcdDiscoveryProperties {

	private static final Log log = LogFactory.getLog(EtcdDiscoveryProperties.class);

	private boolean enabled = true;

	private String discoveryPrefix = "/spring/cloud/discovery";

	private HostInfo hostInfo = initHostInfo();

	private String ipAddress = this.hostInfo.getIpAddress();

	private String hostname = this.hostInfo.getHostname();

	private boolean preferIpAddress = false;

	private int ttl = 30;

	private int heartbeatInterval = 25000;

	public String getHostname() {
		return this.preferIpAddress ? this.ipAddress : this.hostname;
	}

	private HostInfo initHostInfo() {
		InetAddress ipAddress = getIpAddress();
		return new HostInfo(ipAddress.getHostAddress(), ipAddress.getHostName());
	}

	public static InetAddress getIpAddress() {
		try {
			for (Enumeration<NetworkInterface> enumNic = NetworkInterface
					.getNetworkInterfaces(); enumNic.hasMoreElements();) {
				NetworkInterface ifc = enumNic.nextElement();
				if (ifc.isUp()) {
					for (Enumeration<InetAddress> enumAddr = ifc
							.getInetAddresses(); enumAddr.hasMoreElements();) {
						InetAddress address = enumAddr.nextElement();
						if (address instanceof Inet4Address
								&& !address.isLoopbackAddress()) {
							return address;
						}
					}
				}
			}
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e){
			ReflectionUtils.rethrowRuntimeException(e);
			return null;
		} catch (IOException e) {
			log.warn("Unable to find non-loopback address", e);
			return null;
		}
	}

	private class HostInfo {
		private final String ipAddress;
		private final String hostname;

		public HostInfo(String ipAddress, String hostname) {
			this.ipAddress = ipAddress;
			this.hostname = hostname;
		}

		public String getIpAddress() {
			return ipAddress;
		}

		public String getHostname() {
			return hostname;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			HostInfo hostInfo = (HostInfo) o;

			if (!ipAddress.equals(hostInfo.ipAddress)) return false;
			return hostname.equals(hostInfo.hostname);
		}

		@Override
		public int hashCode() {
			int result = ipAddress.hashCode();
			result = 31 * result + hostname.hashCode();
			return result;
		}

		@Override
		public String toString() {
			return String.format("HostInfo{ipAddress='%s', hostname='%s'}", ipAddress, hostname);
		}
	}

	public static Log getLog() {
		return log;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getDiscoveryPrefix() {
		return discoveryPrefix;
	}

	public void setDiscoveryPrefix(String discoveryPrefix) {
		this.discoveryPrefix = discoveryPrefix;
	}

	private HostInfo getHostInfo() {
		return hostInfo;
	}

	private void setHostInfo(HostInfo hostInfo) {
		this.hostInfo = hostInfo;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public boolean isPreferIpAddress() {
		return preferIpAddress;
	}

	public void setPreferIpAddress(boolean preferIpAddress) {
		this.preferIpAddress = preferIpAddress;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public int getHeartbeatInterval() {
		return heartbeatInterval;
	}

	public void setHeartbeatInterval(int heartbeatInterval) {
		this.heartbeatInterval = heartbeatInterval;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EtcdDiscoveryProperties that = (EtcdDiscoveryProperties) o;

		if (enabled != that.enabled) return false;
		if (preferIpAddress != that.preferIpAddress) return false;
		if (ttl != that.ttl) return false;
		if (heartbeatInterval != that.heartbeatInterval) return false;
		if (discoveryPrefix != null ? !discoveryPrefix.equals(that.discoveryPrefix) : that.discoveryPrefix != null)
			return false;
		if (hostInfo != null ? !hostInfo.equals(that.hostInfo) : that.hostInfo != null) return false;
		if (ipAddress != null ? !ipAddress.equals(that.ipAddress) : that.ipAddress != null) return false;
		return hostname != null ? hostname.equals(that.hostname) : that.hostname == null;
	}

	@Override
	public int hashCode() {
		int result = (enabled ? 1 : 0);
		result = 31 * result + (discoveryPrefix != null ? discoveryPrefix.hashCode() : 0);
		result = 31 * result + (hostInfo != null ? hostInfo.hashCode() : 0);
		result = 31 * result + (ipAddress != null ? ipAddress.hashCode() : 0);
		result = 31 * result + (hostname != null ? hostname.hashCode() : 0);
		result = 31 * result + (preferIpAddress ? 1 : 0);
		result = 31 * result + ttl;
		result = 31 * result + heartbeatInterval;
		return result;
	}

	@Override
	public String toString() {
		return String.format(
				"EtcdDiscoveryProperties{enabled=%s, discoveryPrefix='%s', hostInfo=%s, ipAddress='%s', hostname='%s', preferIpAddress=%s, ttl=%d, heartbeatInterval=%d}",
				enabled, discoveryPrefix, hostInfo, ipAddress, hostname, preferIpAddress, ttl, heartbeatInterval);
	}
}
