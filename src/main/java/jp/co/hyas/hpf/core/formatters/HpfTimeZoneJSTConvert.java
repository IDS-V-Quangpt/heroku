package jp.co.hyas.hpf.core.formatters;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Target(java.lang.annotation.ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HpfTimeZoneJSTConvert {
}
