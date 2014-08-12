# JBeret Batch API for EAP6

Backport of Batch API subsystem to EAP6

Tested on JBoss EAP 6.2.4 and 6.3.


## Downloads

* [eap6-batch-dist-1.0.2.zip](http://www.e-contract.be/maven2/org/jberet/eap6/eap6-batch-dist/1.0.2/eap6-batch-dist-1.0.2.zip)
* [eap6-batch-dist-1.0.1.zip](http://www.e-contract.be/maven2/org/jberet/eap6/eap6-batch-dist/1.0.1/eap6-batch-dist-1.0.1.zip)
* [eap6-batch-dist-1.0.0.zip](http://www.e-contract.be/maven2/org/jberet/eap6/eap6-batch-dist/1.0.0/eap6-batch-dist-1.0.0.zip)


## Dependencies

We're using a patched version of JBeret:
https://github.com/fcorneli/jsr352


## Modules

### eap6-jberet

This is a backport of
https://github.com/wildfly/wildfly/tree/master/batch/jberet
to JBoss AS 7.2/EAP6.

### eap6-batch

This is a backport of
https://github.com/wildfly/wildfly/tree/master/batch/extension
to JBoss AS 7.2/EAP6.

### eap6-batch-dist

This is a module that generates a ZIP artifact to ease installation of the Batch API on EAP6.

### eap6-batch-tests

This module contains [Arquillian](http://arquillian.org/) based integration tests and examples.


## Build

We're using [Maven](http://maven.apache.org/) as build system.
Build the project via:
```
mvn clean install
```

## Release

We're using the [Maven Release Plugin](http://maven.apache.org/maven-release/maven-release-plugin/) for the release management.
Release the project via:
```
mvn release:prepare
mvn release:perform
```
The artifact are available within the [e-Contract.be Maven Repository](https://www.e-contract.be/maven2/).
There is also a [Maven Project Site](https://www.e-contract.be/sites/jberet-eap6/) being deployed.


## References

* [JSR 352: Batch Applications for the Java Platform](https://jcp.org/en/jsr/detail?id=352)
* [JBeret](https://github.com/jberet/jsr352)
