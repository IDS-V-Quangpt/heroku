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
@Pattern(regexp="^[ぁ-ん一-龠々﨑 ァ-ヷーa-zA-Z0-9!-~･｢｣]+$|")
public @interface HpfCorporationName {
	String message() default "invalid corporation name";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target(java.lang.annotation.ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public static @interface List {
		HpfCorporationName[] value();
	}
}
