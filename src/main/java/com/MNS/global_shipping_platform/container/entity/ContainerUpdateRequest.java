package com.MNS.global_shipping_platform.container.entity;

import com.MNS.global_shipping_platform.container.ContainerType;

public record ContainerUpdateRequest(ContainerType containerType,
                                     Integer size,
                                     Integer weight) {
}
