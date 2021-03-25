package jp.co.hyas.hpf.core.formatters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

public class HpfCorporationKanaConvertAnnotationFormatterFactory implements AnnotationFormatterFactory<HpfCorporationKanaConvert> {

	@Override
	public Set<Class<?>> getFieldTypes(){
		return new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { String.class }));
	}

	@Override
	public Parser<?> getParser(HpfCorporationKanaConvert annotation, Class<?> fieldtype) {
		return new HpfCorporationKanaFormatter();
	}

	@Override
	public Printer<?> getPrinter(HpfCorporationKanaConvert annotation, Class<?> fieldtype) {
		return new HpfCorporationKanaFormatter();
	}
}
