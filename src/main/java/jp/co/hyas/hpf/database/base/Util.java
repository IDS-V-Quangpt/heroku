package jp.co.hyas.hpf.database.base;

import java.beans.FeatureDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {
	static public Object toString(String v) {
		return (v != null) ? v : "";
	}
	static public Object ifNull(Object v, Object def) {
		return (v == null) ? def : v;
	}
	static public Object ifMapNull(Map<?, Object> v, Object k, Object def) {
		Object r = (Object)v.getOrDefault(k, def);
		return Util.ifNull(r, def);
	}
	static public String insertSql(String table, String cols[]) {
		String sql = (new BaseSqlProvider()).insertSql(table, cols);
		Logger logger = LoggerFactory.getLogger(BaseRepository.class);
		logger.info("SQL:" + sql);
		return sql;
	}
	static public String updateSql(String table, String keycol, String cols[], HashMap<String,Object> param) {
		//String sql = (new BaseSqlProvider()).updateSql(table, keycol, cols);
		String sql = (new BaseSqlProvider()).updatePartialSql(table, keycol, cols, param);
		Logger logger = LoggerFactory.getLogger(BaseRepository.class);
		logger.info("SQL:" + sql);
		return sql;
	}
	static public Timestamp currentTimestamp() {
		//return new Timestamp(System.currentTimeMillis());
		return new Timestamp(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis());
	}

	static public String object2Json(Object obj) throws JsonProcessingException  {
		ObjectMapper mapper = new ObjectMapper();
		return  mapper.writeValueAsString(obj);
	}
	static public Object json2Object(String json, Class cls) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json,cls);
	}
	static public Object map2Object(Map<String, Object> map, Class cls) {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.convertValue(map,cls);
	}
	static public String generatePasswd() {
		//return RandomStringUtils.randomAscii(12);
		//return RandomStringUtils.randomAlphabetic(12);
		return RandomStringUtils.random(12,"#@+?123456789abcdefghijklmnopqrstuvwxyz");
	}

	static public String sfId15to18(String original15charId){

		if (original15charId == null || original15charId.isEmpty()) return null;
		original15charId = original15charId.trim();
		if (original15charId.length() == 18) return original15charId;
		if (original15charId.length() != 15) return null;

		final String BASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456";
		StringBuilder result = new StringBuilder();

		try {
			for (int i = 0; i < 3; i++) {
				StringBuilder tempString = new StringBuilder(original15charId.substring(i*5, i*5+5));
				tempString.reverse();
				String binary = "";
				for (char ch: tempString.toString().toCharArray()) {
					binary += Character.isUpperCase(ch) ? '1' : '0';
				}
				result.append(BASE.charAt(Integer.parseInt(binary,2)));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		if(result.length() == 0) return null;

		return original15charId + result.toString();

	}

	static public String getRequestBody(HttpServletRequest req) {
		try {
			return req.getReader().lines().collect(Collectors.joining("\r\n"));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return null;
		}
	}
	static public String stackTraceMsg(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	public static void copyProperties(Object src, Object target) {
		BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
	}

	public static void copyPropertiesNullSetByHash(Object src, Object target, Map<String,Object> param) {
		// getNullPropertyNamesの結果からnullでも　param　のキーにふくまれるものはコピーする
		// (getNullPropertyNamesの結果から除外することでentityのコピー対象とする)
		String[] ignoreProperties = getNullPropertyNames(src);
		List<String> ignores = new LinkedList<String>(Arrays.asList(ignoreProperties));
		param.forEach((k,v) -> {
			int idx = ignores.indexOf(k);
			if (idx >= 0) ignores.remove(idx);
			//System.err.printf("ignores[%d] = %s\n",idx, k);
		});
		BeanUtils.copyProperties(src, target, (String[])ignores.toArray(new String[]{}));
	}

	public static String[] getNullPropertyNames(Object source) {
		final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
		return Stream.of(wrappedSource.getPropertyDescriptors())
				.filter(descriptor -> descriptor.getReadMethod() != null)
				.map(FeatureDescriptor::getName)
				.filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
				.toArray(String[]::new);

		// .filter(descriptor -> descriptor.getReadMethod() != null) が必要な理由
		// Lombokの影響であるかは不明だが、Beanにsetterがある場合にsetterに対応したプロパティ名が返却される
		// ex. BaseEntity内のsetNaturalKey、setLogicalDeleteFlagが存在しており、
		//     desriptorとしてnaturalKeyとlogicalDeleteFlagが返される
		// このプロパティはdescriptorにReadMethodを持たないため、getPropertyValueを実行すると例外となる
		// そのため、事前にgetReadMethodにてメソッドを持っているかをチェックし、ReadMethodを持たない場合は、
		// 本処理対象から除外している。
	}
}

class BaseSqlProvider extends SQL {
	public String insertSql(String table, String cols[]) {
		INSERT_INTO(table);
		Arrays.stream(cols).forEach(col -> {
			if (col == null) return;
			VALUES("\"" + col + "\"", "#{record." + col + "}");
		});
		return toString();
	}
	public String updateSql(String table, String keycol, String cols[]) {
		UPDATE(table);
		Arrays.stream(cols).forEach(col -> {
			if (col == null) return;
			SET(String.format("\"%s\" = #{record.%s}", col, col));
		});
		WHERE(keycol + " = #{ident}");
		return toString();
	}
	public String updatePartialSql(String table, String keycol, String cols[], HashMap<String,Object> param) {
		UPDATE(table);
		Arrays.stream(cols).forEach(col -> {
			if (col == null) return;
			if (param != null && !param.containsKey(col)) return;
			SET(String.format("\"%s\" = #{record.%s}", col, col));
		});
		WHERE(keycol + " = #{ident}");
		return toString();
	}

}

