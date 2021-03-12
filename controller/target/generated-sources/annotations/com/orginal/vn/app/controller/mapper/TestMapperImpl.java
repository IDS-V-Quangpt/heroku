package com.orginal.vn.app.controller.mapper;

import com.orginal.vn.app.api.dto.request.HelloWorldRequestDTO;
import com.orginal.vn.app.api.dto.response.HelloWorldResponseDTO;
import com.orginal.vn.app.domain.Test;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-03-12T16:49:37+0700",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_232 (Oracle Corporation)"
)
@Component
public class TestMapperImpl implements TestMapper {

    @Override
    public HelloWorldResponseDTO toHelloWorldResponseDTO(Test test) {
        if ( test == null ) {
            return null;
        }

        HelloWorldResponseDTO helloWorldResponseDTO = new HelloWorldResponseDTO();

        helloWorldResponseDTO.setId( test.getId() );
        helloWorldResponseDTO.setMessage( test.getMessage() );

        return helloWorldResponseDTO;
    }

    @Override
    public Test toTest(HelloWorldRequestDTO request) {
        if ( request == null ) {
            return null;
        }

        Test test = new Test();

        test.setMessage( request.getMessage() );

        return test;
    }
}
