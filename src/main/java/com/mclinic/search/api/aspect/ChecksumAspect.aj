package com.mclinic.search.api.aspect;

/**
 * TODO: The aspect class will assign the checksum value into the searchable object.
 * The point-cut will be the execution of Algorithm.deserialize(String). The advice will be executed after the method.
 * We need to get the serialized value, generate the checksum and then assign the checksum value into the searchable.
 */
public aspect ChecksumAspect {
}
