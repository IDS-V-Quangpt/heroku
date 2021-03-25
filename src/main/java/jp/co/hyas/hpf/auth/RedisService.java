package jp.co.hyas.hpf.auth;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
/*
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
*/
import org.springframework.stereotype.Service;

@Service
public class RedisService {
	@Autowired
	private StringRedisTemplate redisTmpl;
	//private StringRedisTemplate redisTmpl = null;

	public RedisService(StringRedisTemplate redisTemplate) {
		redisTmpl = redisTemplate;
	}
	public void saveSamlAttrs(String token, Map<String, List<String>> attr) {
		redisTmpl.expire("my_key", 36000, TimeUnit.SECONDS);
		System.err.println("saveAttrs: " + token);
		ListOperations<String, String> opl = redisTmpl.opsForList();
		String rkey = "";

		// uid: suer,
		// eduPersonAffiliation: member,employee,
		// https://developer.salesforce.com/docs/atlas.en-us.sso.meta/sso/sso_saml_assertion_examples.htm
		Collection<String> keys = attr.keySet();
		//List<String> list = new ArrayList<String>(keys);
		for(String name : keys){
			System.err.print("" + name + ": ");
			List<String> values = attr.get(name);
			for (String value : values) {
				System.err.print("" + value + ",");
			}
			System.err.println("");
		}

		rkey = "token:"+token+":saml-keys";
		redisTmpl.delete(rkey);
		if (!keys.isEmpty())
			opl.rightPushAll(rkey, keys);

		for(String name : keys){
			rkey = "token:"+token+":saml:" + name;
			List<String> values = attr.get(name);
			redisTmpl.delete(rkey);
			opl.rightPushAll(rkey, values);
			
		}
	}

	public void removeSamlAttrs(String token) {
		System.err.println("removeAttrs: " + token);
		ListOperations<String, String> opl = redisTmpl.opsForList();
		String rkey = "";
		rkey = "token:"+token+":saml-keys";
		List<String> keys = opl.range(rkey, 0, -1);
		redisTmpl.delete(rkey);
		for(String name : keys){
			rkey = "token:"+token+":saml:" + name;
			redisTmpl.delete(rkey);
		}
	}

	public Map<String, List<String>> loadSamlAttrs(String token) {
		return loadSamlAttrs(token, false);
	}

	public Map<String, List<String>> loadSamlAttrs(String token, boolean delete) {
		System.err.println("loadAttrs: " + token);
		ListOperations<String, String> opl = redisTmpl.opsForList();
		String rkey = "";

		Map<String, List<String>> attr = new HashMap<String, List<String>>();
		rkey = "token:"+token+":saml-keys";
		List<String> keys = opl.range(rkey, 0, -1);
		if (delete) redisTmpl.delete(rkey);
		for (String name : keys){
			rkey = "token:"+token+":saml:" + name;
			List<String> values = opl.range(rkey, 0, -1);
			attr.put(name, values);
			if (delete) redisTmpl.delete(rkey);
		}

		Collection<String> keys_ = attr.keySet();
		for(String name : keys_){
			System.err.print("" + name + ": ");
			List<String> values = attr.get(name);
			for (String value : values) {
				System.err.print("" + value + ",");
			}
			System.err.println("");
		}

		return attr;
	}
}
