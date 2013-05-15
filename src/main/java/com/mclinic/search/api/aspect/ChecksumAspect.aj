package com.mclinic.search.api.aspect;

import com.mclinic.search.api.model.object.Searchable;
import com.mclinic.search.api.resource.Resource;
import com.mclinic.search.api.util.DigestUtil;

/**
 * TODO: The aspect class will assign the checksum value into the searchable object.
 * The point-cut will be the execution of Algorithm.deserialize(String). The advice will be executed after the method.
 * We need to get the serialized value, generate the checksum and then assign the checksum value into the searchable.
 */
public aspect ChecksumAspect {

    pointcut generateChecksum(Resource resource, String serialized):
            execution(Searchable Resource.deserialize(String)) && target(resource) && args(serialized);

    after(Resource resource, String serialized) returning (Searchable searchable):
            generateChecksum(resource, serialized) {
    }
}
