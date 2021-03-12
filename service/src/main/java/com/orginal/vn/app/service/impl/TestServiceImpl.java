package com.orginal.vn.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orginal.vn.app.service.TestService;
import com.orginal.vn.app.domain.Test;
import com.orginal.vn.app.domain.repository.TestRepository;

@Service
public class TestServiceImpl implements TestService{

	@Autowired
	private TestRepository testRepository;

	public Test helloBitgrip(Test test) {
		return testRepository.save(test);

	}
}
