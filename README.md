# PHP unserializer for Scala

Library that unserializes PHP serialized strings (through PHPs [`serialize`](https://secure.php.net/manual/en/function.serialize.php) method.).

Supports basic values, array (also asociative) and PHP objects.

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