package com.octopus.orchestration.controllers;

import com.octopus.orchestration.services.ContainersService;
import com.spotify.docker.client.messages.Container;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContainersController {

    @Autowired
    private ContainersService containersService;

    @GetMapping
    public List<Container> listAllContainers()  {
        return containersService.listAll();
    }
}
