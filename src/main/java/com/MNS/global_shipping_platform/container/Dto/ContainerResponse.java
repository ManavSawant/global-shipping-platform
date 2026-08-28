package com.MNS.global_shipping_platform.container.Dto;

import com.MNS.global_shipping_platform.container.ContainerStatus;
import com.MNS.global_shipping_platform.container.ContainerType;

import java.util.UUID;

public record ContainerResponse(UUID containerId,
                                ContainerType containerType,
                                String containerNumber,
                                Integer size,
                                Integer weight ,
                                ContainerStatus containerStatus)
{ }
