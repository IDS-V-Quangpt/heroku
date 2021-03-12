package org.app.root.controller.mapper;


import org.mapstruct.Mapper;

import org.app.root.domain.Test;
import org.app.root.api.dto.request.HelloWorldRequestDTO;
import org.app.root.api.dto.response.HelloWorldResponseDTO;

@Mapper(componentModel="spring")
public interface TestMapper {


	public HelloWorldResponseDTO toHelloWorldResponseDTO(Test test);

	public Test toTest(HelloWorldRequestDTO request);
}
