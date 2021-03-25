package jp.co.hyas.hpf.core.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;

@Target(java.lang.annotation.ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Pattern(regexp="\\d{3}-\\d{4}|")
public @interface HpfZipCode {
	String message() default "invalid zip code";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target(java.lang.annotation.ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public static @interface List {
		HpfZipCode[] value();
	}
}
