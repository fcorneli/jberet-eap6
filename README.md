# JBeret Batch API for EAP6

Backport of Batch API subsystem to EAP6


## Downloads

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


## References

* [JSR 352: Batch Applications for the Java Platform](https://jcp.org/en/jsr/detail?id=352)
* [JBeret](https://github.com/jberet/jsr352)
