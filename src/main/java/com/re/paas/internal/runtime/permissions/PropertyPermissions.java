package com.re.paas.internal.runtime.permissions;

import java.util.PropertyPermission;

import com.re.paas.api.runtime.RuntimeIdentity;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.runtime.Permissions;

public class PropertyPermissions implements BasePermission {

	@Override
	public String permissionClass() {
		return ClassUtils.getName(PropertyPermission.class);
	}

	@Override
	public Integer getBaseIndex() {
		return 1;
	}

	@Override
	public Short getIndex(String name, String actions, String context) {

		// Use indexes

//		if (actions.equals("write")) {
//			return Permissions.DENY;
//		}
//
//		if (name.startsWith("org.apache.logging.log4j")) {
//			return Permissions.ALLOW;
//		}
//
//		switch (name) {
//		case "sun.reflect.debugModuleAccessChecks":
//			return Permissions.ALLOW;
//		case "jdk.proxy.debug":
//			return Permissions.ALLOW;
//		case "jdk.proxy.ProxyGenerator.saveGeneratedFiles":
//			return Permissions.ALLOW;
//		case "java.io.tmpdir":
//			return Permissions.ALLOW;
//		case "java.net.preferIPv6Addresses":
//			return Permissions.ALLOW;
//		case "impl.prefix":
//			return Permissions.ALLOW;
//		case "jdk.net.hosts.file":
//			return Permissions.ALLOW;
//		case "*":
//			return Permissions.ALLOW;
//		case "line.separator":
//			return Permissions.ALLOW;
//		case "tika.config":
//			return Permissions.ALLOW;
//		case "org.apache.tika.service.error.warn":
//			return Permissions.ALLOW;
//		case "jaxp.debug":
//			return Permissions.ALLOW;
//		case "javax.xml.parsers.SAXParserFactory":
//			return Permissions.ALLOW;
//		case "infinispan.arrays.debug":
//			return Permissions.ALLOW;
//		case "infinispan.collections.limit":
//			return Permissions.ALLOW;
//		case "org.jboss.logging.locale":
//			return Permissions.ALLOW;
//		case "org.jboss.logging.provider":
//			return Permissions.ALLOW;
//		case "disableThreadContext":
//			return Permissions.ALLOW;
//		case "log4j.ignoreTCL":
//			return Permissions.ALLOW;
//		case "disableThreadContextStack":
//			return Permissions.ALLOW;
//		case "log4j2.is.webapp":
//			return Permissions.ALLOW;
//		case "disableThreadContextMap":
//			return Permissions.ALLOW;
//		case "java.version":
//			return Permissions.ALLOW;
//		case "log4j2.threadContextMap":
//			return Permissions.ALLOW;
//		case "log4j.maxReusableMsgSize":
//			return Permissions.ALLOW;
//		case "log4j2.messageFactory":
//			return Permissions.ALLOW;
//		case "isThreadContextMapInheritable":
//			return Permissions.ALLOW;
//		case "log4j2.flowMessageFactory":
//			return Permissions.ALLOW;
//		case "java.util.concurrent.ForkJoinPool.common.maximumSpares":
//			return Permissions.ALLOW;
//		case "cchm.initial_capacity":
//			return Permissions.ALLOW;
//		case "log4j2.status.entries":
//			return Permissions.ALLOW;
//		case "log4j2.garbagefree.threadContextMap":
//			return Permissions.ALLOW;
//		case "infinispan.activeprocessorcount":
//			return Permissions.ALLOW;
//		case "os.name":
//			return Permissions.ALLOW;
//		case "log4j2.StatusLogger.level":
//			return Permissions.ALLOW;
//		case "org.apache.logging.log4j.simplelog.StatusLogger.level":
//			return Permissions.ALLOW;
//		case "log4j2.debug":
//			return Permissions.ALLOW;
//		case "log4j2.loggerContextFactory":
//			return Permissions.ALLOW;
//		case "java.util.concurrent.ForkJoinPool.common.parallelism":
//			return Permissions.ALLOW;
//		case "cchm.load_factor":
//			return Permissions.ALLOW;
//		case "org.apache.logging.log4j.simplelog.showContextMap":
//			return Permissions.ALLOW;
//		case "org.apache.logging.log4j.simplelog.showlogname":
//			return Permissions.ALLOW;
//		case "java.home":
//			return Permissions.ALLOW;
//		case "org.openjdk.java.util.stream.tripwire":
//			return Permissions.ALLOW;
//		case "org.apache.logging.log4j.simplelog.showShortLogname":
//			return Permissions.ALLOW;
//		case "org.apache.logging.log4j.simplelog.showdatetime":
//			return Permissions.ALLOW;
//		case "java.net.useSystemProxies":
//			return Permissions.ALLOW;
//		case "org.apache.logging.log4j.simplelog.level":
//			return Permissions.ALLOW;
//		case "socksProxyHost":
//			return Permissions.ALLOW;
//		case "org.apache.logging.log4j.simplelog.logFile":
//			return Permissions.ALLOW;
//		case "org.apache.logging.log4j.simplelog.org.jboss.logging.level":
//			return Permissions.ALLOW;
//		case "java.vendor":
//			return Permissions.ALLOW;
//		case "user.dir":
//			return Permissions.ALLOW;
//		case "java.util.concurrent.ForkJoinPool.common.threadFactory":
//			return Permissions.ALLOW;
//		case "java.util.concurrent.ForkJoinPool.common.exceptionHandler":
//			return Permissions.ALLOW;
//		case "java.net.preferIPv4Stack":
//			return Permissions.ALLOW;
//		case "cchm.concurrency_level":
//			return Permissions.ALLOW;
//		case "max.list.print_size":
//			return Permissions.ALLOW;
//		case "jgroups.msg.default_headers":
//			return Permissions.ALLOW;
//		case "jgroups.conf.magic_number_file":
//			return Permissions.ALLOW;
//		case "org.jgroups.conf.magicNumberFile":
//			return Permissions.ALLOW;
//		case "jgroups.conf.protocol_id_file":
//			return Permissions.ALLOW;
//		case "org.jgroups.conf.protocolIDFile":
//			return Permissions.ALLOW;
//		case "javax.xml.parsers.DocumentBuilderFactory":
//			return Permissions.ALLOW;
//		case "jdk.xml.overrideDefaultParser":
//			return Permissions.ALLOW;
//		case "javax.xml.useCatalog":
//			return Permissions.ALLOW;
//		case "jdk.xml.resetSymbolTable":
//			return Permissions.ALLOW;
//		case "jdk.xml.cdataChunkSize":
//			return Permissions.ALLOW;
//		case "jdk.xml.entityExpansionLimit":
//			return Permissions.ALLOW;
//		case "entityExpansionLimit":
//			return Permissions.ALLOW;
//		case "jdk.xml.maxOccurLimit":
//			return Permissions.ALLOW;
//		case "maxOccurLimit":
//			return Permissions.ALLOW;
//		case "jdk.xml.elementAttributeLimit":
//			return Permissions.ALLOW;
//		case "elementAttributeLimit":
//			return Permissions.ALLOW;
//		case "jdk.xml.totalEntitySizeLimit":
//			return Permissions.ALLOW;
//		case "jdk.xml.maxGeneralEntitySizeLimit":
//			return Permissions.ALLOW;
//		case "jdk.xml.maxParameterEntitySizeLimit":
//			return Permissions.ALLOW;
//		case "jdk.xml.maxElementDepth":
//			return Permissions.ALLOW;
//		case "jdk.xml.maxXMLNameLimit":
//			return Permissions.ALLOW;
//		case "jdk.xml.entityReplacementLimit":
//			return Permissions.ALLOW;
//		case "javax.xml.accessExternalDTD":
//			return Permissions.ALLOW;
//		case "javax.xml.accessExternalSchema":
//			return Permissions.ALLOW;
//		case "http://java.sun.com/xml/dom/properties/ancestor-check":
//			return Permissions.ALLOW;
//		case "suppress.view_size":
//			return Permissions.ALLOW;
//		case "jgroups.use.jdk_logger":
//			return Permissions.ALLOW;
//		case "jgroups.log_class":
//			return Permissions.ALLOW;
//		case "slf4j.detectLoggerNameMismatch":
//			return Permissions.ALLOW;
//		case "org.infinispan.feature.zero-capacity-node":
//			return Permissions.ALLOW;
//		case "infinispan.deserialization.whitelist.regexps":
//			return Permissions.ALLOW;
//		case "infinispan.deserialization.whitelist.classes":
//			return Permissions.ALLOW;
//		case "jgroups.mcast_port":
//			return Permissions.ALLOW;
//		case "jgroups.bind.port":
//			return Permissions.ALLOW;
//		case "jgroups.udp.port":
//			return Permissions.ALLOW;
//		case "jgroups.ip_ttl":
//			return Permissions.ALLOW;
//		case "jgroups.mcast_addr":
//			return Permissions.ALLOW;
//		case "jgroups.thread_pool.max_threads":
//			return Permissions.ALLOW;
//		case "jgroups.bind.address":
//			return Permissions.ALLOW;
//		case "jgroups.udp.address":
//			return Permissions.ALLOW;
//		case "jgroups.thread_pool.min_threads":
//			return Permissions.ALLOW;
//		case "jgroups.join_timeout":
//			return Permissions.ALLOW;
//		case "java.vm.vendor":
//			return Permissions.ALLOW;
//		case "jgroups.bind_addr":
//			return Permissions.ALLOW;
//		case "jgroups.external_addr":
//			return Permissions.ALLOW;
//		case "jgroups.external_port":
//			return Permissions.ALLOW;
//		case "sun.net.inetaddr.ttl":
//			return Permissions.ALLOW;
//		case "jgroups.name_cache.max_elements":
//			return Permissions.ALLOW;
//		case "jgroups.name_cache.max_age":
//			return Permissions.ALLOW;
//		case "sun.net.maxDatagramSockets":
//			return Permissions.ALLOW;
//		case "rx2.scheduler.drift-tolerance":
//			return Permissions.ALLOW;
//		case "rx2.single-priority":
//			return Permissions.ALLOW;
//		case "rx2.computation-threads":
//			return Permissions.ALLOW;
//		case "rx2.computation-priority":
//			return Permissions.ALLOW;
//		case "rx2.io-keep-alive-time":
//			return Permissions.ALLOW;
//		case "rx2.io-priority":
//			return Permissions.ALLOW;
//		case "rx2.newthread-priority":
//			return Permissions.ALLOW;
//		case "rx2.buffer-size":
//			return Permissions.ALLOW;
//		case "jctools.spsc.max.lookahead.step":
//			return Permissions.ALLOW;
//		}

		return Permissions.ALLOW;
	}

	@Override
	public void addDefaults(Boolean[] destination) {
	}

	/**
	 * Applications that wish to store system properties should prepend this prefix
	 * to the keys
	 * 
	 * @return
	 */
	private static String prefix() {
		String appId = RuntimeIdentity.getAppId();
		return appId != null ? "app." + appId + ".props." : "";
	}
}
