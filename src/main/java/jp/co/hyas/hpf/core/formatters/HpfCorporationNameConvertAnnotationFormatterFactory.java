package jp.co.hyas.hpf.core.formatters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class HpfCorporationNameConvertAnnotationFormatterFactory implements AnnotationFormatterFactory<HpfCorporationNameConvert> {

	@Override
	public Set<Class<?>> getFieldTypes(){
		return new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { String.class }));
	}

	@Override
	public Parser<?> getParser(HpfCorporationNameConvert annotation, Class<?> fieldtype) {
		return new HpfCorporationNameFormatter();
	}

	@Override
	public Printer<?> getPrinter(HpfCorporationNameConvert annotation, Class<?> fieldtype) {
		return new HpfCorporationNameFormatter();
	}
}
