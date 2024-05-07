package com.product.ordering.system.kafka.message.serialization;

import java.io.Serializable;

public interface TypeProjection extends Serializable {

    String type();
}
