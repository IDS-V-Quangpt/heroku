package jp.co.hyas.hpf.core.formatters;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(java.lang.annotation.ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HpfCorporationKanaConvert {
}
