package com.octopus.orchestration.controllers;

import com.octopus.orchestration.services.ContainersService;
import com.spotify.docker.client.messages.Container;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController public class ContainersController {
	Logger logger = Logger.getLogger(ContainersController.class);

	@Autowired private ContainersService containersService;

	@GetMapping public List<Container> listAllContainers() {
		logger.info("[GET] - listAllContainers");
		return containersService.listAll();
	}
}
