package com.octopus.orchestration.models;

import java.util.List;

import lombok.Data;

@Data
public class ContainerRequest {
    List<String> containersIds;
}
