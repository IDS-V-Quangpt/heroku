package jp.co.hyas.hpf;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

public class FQCNGenerator extends AnnotationBeanNameGenerator {

	@Override
	protected String buildDefaultBeanName(BeanDefinition definition) {
		System.out.println("buildDefaultBeanName: "+definition.getBeanClassName());
		return definition.getBeanClassName();
	}
}
