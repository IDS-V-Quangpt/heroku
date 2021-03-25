package jp.co.hyas.hpf.core.constraints;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.hyas.hpf.database.OwnerRepository;

@Component
public class HpfOwnerUniquenessValidator implements ConstraintValidator<HpfOwnerUniqueness, Object> {

	@Autowired
	private OwnerRepository repository;

	private String[] properties;

	@Override
	public void initialize(HpfOwnerUniqueness constraint) {
		this.properties = constraint.properties();
	}

	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(object);

		Map<String, Object> criteria = new HashMap<>();
		for (String property: this.properties) {
			criteria.put(property, beanWrapper.getPropertyValue(property));
		}

		Logger logger = LoggerFactory.getLogger(HpfOwnerUniquenessValidator.class);
        logger.info("repository:" + repository);

		return !repository.exists(criteria, (String) beanWrapper.getPropertyValue("owner_id__c"));
	}
}
