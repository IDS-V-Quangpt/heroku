package jp.co.hyas.hpf.database.base;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

public interface BaseRepository<T extends BaseEntity, TS extends BaseSearchEntityInterface> {

	public List<T> findAll();

	public long count(@Param("criteria") TS criteria);

	public List<T> search(
			@Param("criteria") TS criteria,
			@Param("pageable") Pageable pageable);

	public T findOne(String ident);

	public int insert(T t);

	public int update(String ident, T t);

	public int updatePartial(String ident, T t, Map<String, Object> hs);

	public int delete(String ident);
}
