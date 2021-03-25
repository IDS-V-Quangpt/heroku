package jp.co.hyas.hpf.core.formatters;

import java.util.Locale;
import java.util.Date;
import org.springframework.expression.ParseException;
import org.springframework.format.Formatter;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import jp.co.hyas.hpf.core.HpfConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class HpfTimeZoneJSTFormatter implements Formatter<String> {

	@Override
	public String print(String str, Locale locate) {
		return str;
	}

	@Override
	public String parse(String str, Locale locate) throws ParseException {
		String timeStr = "";
		try { 
			timeStr = HpfConvert.TimeZoneJST(str);
		} catch (Exception e) {
		}
		return timeStr;
	}
}
