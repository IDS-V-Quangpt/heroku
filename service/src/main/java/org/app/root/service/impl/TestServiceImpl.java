package org.app.root.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.app.root.service.TestService;
import org.app.root.domain.Test;
import org.app.root.domain.repository.TestRepository;

@Service
public class TestServiceImpl implements TestService{

	@Autowired
	private TestRepository testRepository;

	public Test helloBitgrip(Test test) {
		return testRepository.save(test);

	}
}
