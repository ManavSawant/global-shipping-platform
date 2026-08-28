package com.MNS.global_shipping_platform.container.Dto;

import com.MNS.global_shipping_platform.container.ContainerType;

public record ContainerRequest(ContainerType containerType,
                               String containerNumber,
                               Integer size,
                               Integer weight
){}
