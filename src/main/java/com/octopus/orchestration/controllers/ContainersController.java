package com.octopus.orchestration.controllers;

import com.octopus.orchestration.services.ContainersService;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/containers")
public class ContainersController {

    @Autowired
    private ContainersService containersService;

    @GetMapping
    public ResponseEntity<List<Container>> listAll()  {
        return ResponseEntity.ok(containersService.listAll());
    }

    @GetMapping("/inspect/{id}")
    public ResponseEntity<ContainerInfo> inspect(@PathVariable String id) {
        return ResponseEntity.ok(containersService.inspect(id));
    }

    @GetMapping("/logs/{id}")
    public ResponseEntity<String> getLogs(@PathVariable String id) {
        return ResponseEntity.ok(containersService.getLogs(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        return ResponseEntity.ok(containersService.delete(id));
    }
}
