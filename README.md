# PHP unserializer for Scala

[![Build Status](https://travis-ci.org/Ceratech/php-unserializer.svg?branch=master)](https://travis-ci.org/Ceratech/php-unserializer)
[![Download](https://api.bintray.com/packages/ceratech/maven/php-unserializer/images/download.svg)](https://bintray.com/ceratech/maven/php-unserializer/_latestVersion)
[![Coverage Status](https://coveralls.io/repos/github/Ceratech/php-unserializer/badge.svg?branch=master)](https://coveralls.io/github/Ceratech/php-unserializer?branch=master)

Library that unserializes PHP serialized strings (through PHPs [`serialize`](https://secure.php.net/manual/en/function.serialize.php) method.).

Supports basic values, array (also asociative) and PHP objects.

# Installation

Add the following resolver to your `build.sbt`:

```scala
resolvers += Resolver.bintrayRepo("ceratech", "maven")
```

Add the following dependency to your dependencies:

```scala
libraryDependencies += "io.ceratech" %% "php-unserializer" % "0.1"
```

# Usage

Call the `unserialize` method of the `PHPSParser` object. Like so:

```scala
PHPSParser.unserialize(inputString)
```

The resulting value is an `Any`.

## Types

Parse results, given a certain input:

* A non-asociative array in the serialized data will be parsed into a Scala list
* A asociative array in the serialized data will be parsed into a Scala map
* PHPs `null` will be parsed into a Scala `None`

# Acknowledgements

This project is a fork from [Scala PHPS](https://github.com/mcfunley/scala-phps) cleaning up some things and adding deployment for simplified usage.