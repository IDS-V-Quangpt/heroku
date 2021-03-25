package jp.co.hyas.hpf.core.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy={HpfOwnerUniquenessValidator.class})
@Repeatable(HpfOwnerUniqueness.List.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HpfOwnerUniqueness {

	String message() default "redundant";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String[] properties();

	@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public @interface List {
		HpfOwnerUniqueness[] value();
	}
}
