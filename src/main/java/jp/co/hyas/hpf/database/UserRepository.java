package jp.co.hyas.hpf.database;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import jp.co.hyas.hpf.database.base.BaseRepository;
import jp.co.hyas.hpf.database.base.BaseSearchEntity;

@Mapper
public interface UserRepository extends BaseRepository<User, BaseSearchEntity> {
	final static String TABLE = "salesforce.user";
	final static String KEYCOL = "sfid";

	@Select("SELECT * FROM " + TABLE + " WHERE sfid = #{sfid} ORDER BY id")
	public Map<String, Object> findOneBySfId(@Param("sfid") String sfid);

	@Select("SELECT * FROM " + TABLE + " WHERE " + KEYCOL + " = #{ident} ORDER BY id")
	public User findOne(@Param("ident") String ident);
}
