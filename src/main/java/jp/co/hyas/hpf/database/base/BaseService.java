package jp.co.hyas.hpf.database.base;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import jp.co.hyas.hpf.core.exception.ValidateException;
import xyz.downgoon.snowflake.Snowflake;

@Service
abstract public class BaseService<T extends BaseEntity, TS extends BaseSearchEntityInterface, R extends BaseRepository<T, TS>> {
	//@Autowired
	protected R repository;

	/** アクセス元アプリケーションコード */
	protected String appCode = null;
	abstract public R getRepository();
	abstract public Class<T> getEntityClass();
	abstract public void presetEntityOnCrud(String crud, String ident, T t, Map<String, Object> hs);

	// DB登録後処理
	abstract public void postDbExecute(String crud, String ident, T t, Map<String, Object> hs);

	// datacenter: AppConst.IDG_MACHINEID; workerId: 0
	static Snowflake snowflake = new Snowflake(AppConst.IDG_MACHINEID, 0);

	// ID生成
	public String generateId() {
		long gid = snowflake.nextId();
		return "" + gid;
	}

	@PostConstruct
	public void init() {
		this.repository = this.getRepository();
		//System.out.println("this.repository:"+(this.repository == null ? "null!" : this.repository.getClass()));
	}

	public List<T> findAll() {
		return repository.findAll();
	}

	public Page<T> search(TS criteria, Pageable pageable) {
		long total = repository.count(criteria);
		List<T> list = Collections.emptyList();
		if (total > 0) {
			list = repository.search(criteria, pageable);
		}
		return new PageImpl<T>(list, pageable, total);
	}

	public T findOne(String ident) {
		return (T) repository.findOne(ident);
	}

	public void create(T t) {
		create(t, true);
	}

	public void create(T t, Boolean doValidate) {
		presetEntityOnCrud("insert", null, t, null);
		if (doValidate) {
			validate(t);
		}
		repository.insert(t);
		postDbExecute("insert", t.getNaturalKey(), t, null);
	}

	public void updatePartial(String ident, T t, Map<String, Object> hs) {
		updatePartial(ident, t, hs, true);
	}

	public void updatePartial(String ident, T t, Map<String, Object> hs, Boolean doValidate) {
		presetEntityOnCrud("update", ident, t, hs);
		if (doValidate) {
			validate(t);
		}
		repository.updatePartial(ident, t, hs);
		postDbExecute("update", ident, t, hs);
	}

	public void update(String ident, T t) {
		update(ident, t, true);
	}

	public void update(String ident, T t, Boolean doValidate) {
		presetEntityOnCrud("update", ident, t, null);
		if (doValidate) {
			validate(t);
		}
		repository.update(ident, t);
		postDbExecute("update", ident, t, null);
	}

	public void delete(String ident) {
		presetEntityOnCrud("delete", ident, null, null);
		repository.delete(ident);
		postDbExecute("delete", ident, null, null);
	}

	@Autowired
	Validator validator;
	//Validator validator = new LocalValidatorFactoryBean();

	private <T> void validate(T bean) {
		//ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		//Validator validator = factory.getValidator();

		Set<ConstraintViolation<T>> constraintViolations = validator.validate(bean);

		System.out.println("size = " + constraintViolations.size());
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		constraintViolations.forEach(constraintViolation -> {
			System.out.println("path = " + constraintViolation.getPropertyPath().toString());
			System.out.println("message = " + constraintViolation.getMessage());
			map.add(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
		});
		if (constraintViolations.size() > 0) {
			throw new ValidateException(map);
		}
	}
}
