package com.octopus.orchestration.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/health")
public class HealthController {
	@GetMapping
	@ResponseBody
	public String healthCheck() {
		return "healthy";
	}
}
