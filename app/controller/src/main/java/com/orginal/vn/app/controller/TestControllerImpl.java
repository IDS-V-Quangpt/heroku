package com.orginal.vn.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.orginal.vn.app.controller.mapper.TestMapper;
import com.orginal.vn.app.api.dto.request.HelloWorldRequestDTO;
import com.orginal.vn.app.domain.Test;
import com.orginal.vn.app.api.dto.response.HelloWorldResponseDTO;
import com.orginal.vn.app.api.TestController;
import com.orginal.vn.app.service.TestService;


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
