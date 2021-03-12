package org.app.root.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import org.app.root.controller.mapper.TestMapper;
import org.app.root.api.dto.request.HelloWorldRequestDTO;
import org.app.root.domain.Test;
import org.app.root.api.dto.response.HelloWorldResponseDTO;
import org.app.root.api.TestController;
import org.app.root.service.TestService;


@RestController
public class TestControllerImpl implements TestController{

	@Autowired
	private TestService testService;

	@Autowired
	private TestMapper testMapper;


	public  HelloWorldResponseDTO helloBitgrip(@RequestBody HelloWorldRequestDTO request) {
		Test result = testService.helloBitgrip(testMapper.toTest(request));
		return testMapper.toHelloWorldResponseDTO(result);
	}
}
