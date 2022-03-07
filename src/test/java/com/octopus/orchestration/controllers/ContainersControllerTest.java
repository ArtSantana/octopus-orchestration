package com.octopus.orchestration.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.octopus.orchestration.exceptions.ContainersException;
import com.octopus.orchestration.services.ContainersService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class ContainersControllerTest {
	@MockBean
	ContainersService containersService;

	@Autowired
	private MockMvc mockMvc;

	private static URI uri;

	@BeforeAll
	public static void setup() throws URISyntaxException {
		uri = new URI("/containers");
	}

	@Test
	void testListAll() throws Exception {
		mockMvc.perform(get(uri))
				.andDo(print())
				.andExpect(status().isOk());

		Mockito.verify(containersService, times(1)).listAll();
	}

	@Test
	void testListAllShouldThrowException() throws Exception {
		when(containersService.listAll()).thenThrow(new ContainersException("some exception message", HttpStatus.BAD_REQUEST));
		mockMvc.perform(get(uri))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void testInspect() throws Exception {
		mockMvc.perform(get(uri + "/inspect/some-container-id"))
				.andDo(print())
				.andExpect(status().isOk());

		Mockito.verify(containersService, times(1)).inspect("some-container-id");
	}

	@Test
	void testInspectShouldThrowException() throws Exception {
		when(containersService.inspect(anyString())).thenThrow(new ContainersException("some exception message", HttpStatus.BAD_REQUEST));
		mockMvc.perform(get(uri + "/inspect/some-container-id"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void testGetLogs() throws Exception {
		mockMvc.perform(get(uri + "/logs/some-container-id"))
				.andDo(print())
				.andExpect(status().isOk());

		Mockito.verify(containersService, times(1)).getLogs("some-container-id");
	}

	@Test
	void testGetLogsShouldThrowException() throws Exception {
		when(containersService.getLogs(anyString())).thenThrow(new ContainersException("some exception message", HttpStatus.BAD_REQUEST));
		mockMvc.perform(get(uri + "/logs/some-container-id"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void testDelete() throws Exception {
		when(containersService.delete(anyString())).thenReturn("some-container-id");

		MvcResult result =
				mockMvc.perform(delete(uri + "/some-container-id"))
						.andDo(print())
						.andExpect(status().isOk())
						.andReturn();

		String response = result.getResponse().getContentAsString();

		Mockito.verify(containersService, times(1)).delete("some-container-id");
		assertEquals("some-container-id", response);
	}

	@Test
	void testDeleteShouldThrowException() throws Exception {
		when(containersService.delete(anyString())).thenThrow(new ContainersException("some exception message", HttpStatus.BAD_REQUEST));
		mockMvc.perform(delete(uri + "/some-container-id"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}
}
