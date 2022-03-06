package com.octopus.orchestration.utils;

import org.apache.log4j.Logger;

public class ConfigService {
	private static final Logger LOGGER = Logger.getLogger(ConfigService.class);

	public static String getValue(String key, String defaultValue) {
		String envValue = System.getenv(key);
		if (envValue != null && !envValue.isBlank()) {
			LOGGER.debug("Env var" + key + "not found");
			return envValue;
		}
		return defaultValue;
	}
}
