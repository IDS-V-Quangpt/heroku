package jp.co.hyas.hpf.core.formatters;

import java.util.Locale;

import org.springframework.expression.ParseException;
import org.springframework.format.Formatter;

import jp.co.hyas.hpf.core.HpfConvert;

public class HpfCorporationKanaFormatter implements Formatter<String> {

	@Override
	public String print(String str, Locale locate) {
		return str;
	}

	@Override
	public String parse(String str, Locale locate) throws ParseException {
		return HpfConvert.CorporationKana(str);
	}
}
