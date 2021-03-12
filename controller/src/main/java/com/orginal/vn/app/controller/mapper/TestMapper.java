package com.orginal.vn.app.controller.mapper;


import org.mapstruct.Mapper;

import com.orginal.vn.app.domain.Test;
import com.orginal.vn.app.api.dto.request.HelloWorldRequestDTO;
import com.orginal.vn.app.api.dto.response.HelloWorldResponseDTO;

@Mapper(componentModel="spring")
public interface TestMapper {


	public HelloWorldResponseDTO toHelloWorldResponseDTO(Test test);

	public Test toTest(HelloWorldRequestDTO request);
}
