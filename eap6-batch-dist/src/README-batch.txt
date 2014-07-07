Batch API for EAP6
==================


=== 1. Installation

Unzip under the jboss-eap-6.2 directory.

When upgrading, make sure to overwrite all existing files.


=== 2. Configuration

standalone/configuration/standalone-full.xml:
<extensions>
	...
	<extension module="org.wildfly.extension.batch"/>
</extensions>

<profile>
	...
	<subsystem xmlns="urn:jboss:domain:batch:1.0">
		<job-repository>
			<jdbc jndi-name="java:jboss/datasources/ExampleDS"/>
		</job-repository>
		<thread-pool>
			<max-threads count="10"/>
			<keepalive-time time="30" unit="seconds"/>
		</thread-pool>
	</subsystem>
</profile>

modules/system/layers/base/javaee/api/main/module.xml:
<dependencies>
	...
	<module name="javax.batch.api" export="true"/>
</dependencies>