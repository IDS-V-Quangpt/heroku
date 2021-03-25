package jp.co.hyas.hpf;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.zaxxer.hikari.HikariDataSource;

import jp.co.hyas.hpf.core.formatters.HpfCorporationKanaConvertAnnotationFormatterFactory;
import jp.co.hyas.hpf.core.formatters.HpfCorporationNameConvertAnnotationFormatterFactory;
import jp.co.hyas.hpf.core.formatters.HpfTimeZoneJSTConvertAnnotationFormatterFactory;

@Configuration
@EnableRedisHttpSession
public class CustomMvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(
        List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver resolver =
            new PageableHandlerMethodArgumentResolver();
        resolver.setOneIndexedParameters(true);
        argumentResolvers.add(resolver);
    }

    @Bean
    public javax.validation.Validator localValidatorFactoryBean() {
       return new LocalValidatorFactoryBean();
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
/*
    @Bean
	public JedisConnectionFactory jedisConnFactory() {

		try {
			String redistogoUrl = System.getenv("REDISTOGO_URL");
			URI redistogoUri = new URI(redistogoUrl);

			JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory();

			jedisConnFactory.setUsePool(true);
			jedisConnFactory.setHostName(redistogoUri.getHost());
			jedisConnFactory.setPort(redistogoUri.getPort());
			jedisConnFactory.setTimeout(Protocol.DEFAULT_TIMEOUT);
			jedisConnFactory.setPassword(redistogoUri.getUserInfo().split(":", 2)[1]);

			return jedisConnFactory;

		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
*/
	@Autowired
	private Environment env;

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		HikariDataSource ds = new HikariDataSource();
		try {
			String username = "username";
			String password = "password";
			String url = "jdbc:postgresql://localhost/dbname";
			String driverClassName = env.getProperty("spring.datasource.driver-class-name");
			String dbProperty = env.getProperty("datasource.url");
			if (dbProperty != null) {
				URI dbUri = new URI(dbProperty);
				username = dbUri.getUserInfo().split(":")[0];
				password = dbUri.getUserInfo().split(":")[1];
				url = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
				System.err.println("datasource.url: " + dbProperty);
				System.err.println("jdbc:postgresq: " + url);
				System.err.println("CURRENT_DATABASE_URL: " +  System.getenv("CURRENT_DATABASE_URL"));
			}
			ds.setDriverClassName(driverClassName);
			ds.setJdbcUrl(url);
			ds.setUsername(username);
			ds.setPassword(password);
			return ds;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

    // 	[Heroku] REDIS_URL: redis://h:passwd@ec2-xxx-xxx-xxx-xxx.compute-x.amazonaws.com:xxxx
    // 	[local]  REDIS_URL: redis://h:passwd@localhost:6379
	//static final String ENV_REDIS_CONNECT = "REDISTOGO_URL";
	static final String ENV_REDIS_CONNECT = "REDIS_URL";
	static final String REDIS_LOCAL_DEFAULT = "redis://h:@localhost:6379";
	@Bean
    public JedisConnectionFactory jedisConnectionFactory() {

		URI redistInfo = null;
		String redistogoUrl = null;
		try {
			redistogoUrl = System.getenv(ENV_REDIS_CONNECT);
	    	System.err.println("REDIS_URL: " + redistogoUrl);
			if (null == redistogoUrl || "".equals(redistogoUrl)) {
				redistogoUrl = REDIS_LOCAL_DEFAULT;
			}
			//redistogoUrl = System.getProperty(ENV_REDIS_CONNECT, REDIS_LOCAL_DEFAULT);
			redistInfo = new URI(redistogoUrl);
		} catch (URISyntaxException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return null;
		}
		System.err.println("REDIS_URL: " + redistogoUrl);
		System.err.println("redistInfo.getHost(): " + redistInfo.getHost());
		System.err.println("edistInfo.getPort(): " + String.valueOf(redistInfo.getPort()) );
		System.err.println("RedisPassword.of(redistInfo.getUserInfo().split(:, 2)[1]): " + redistInfo.getUserInfo().split(":", 2)[1]);
		System.err.println("REDIS_URL: " + redistogoUrl);
		// https://stackoverflow.com/questions/49021994/jedisconnectionfactory-sethostname-is-deprecated
		RedisStandaloneConfiguration redisStdConf = new RedisStandaloneConfiguration();
		redisStdConf.setHostName(redistInfo.getHost());
		redisStdConf.setPort(redistInfo.getPort());
		//redisStdConf.setDatabase(0);
		redisStdConf.setPassword(RedisPassword.of(redistInfo.getUserInfo().split(":", 2)[1]));

		//JedisClientConfigurationBuilder clConf = JedisClientConfiguration.builder();
		//clConf.connectTimeout(Duration.ofSeconds(60));// 60s connection timeout

		//JedisConnectionFactory jedisConFactory = new JedisConnectionFactory(redisStdConf,clConf.build());
		JedisConnectionFactory jedisConFactory = new JedisConnectionFactory(redisStdConf);

		return jedisConFactory;
	}

	@Bean
	public ConfigureRedisAction configureRedisAction() {
	    return ConfigureRedisAction.NO_OP;
	}

	public void addFormatters(FormatterRegistry registry) {
		registry.addFormatterForFieldAnnotation(new HpfCorporationNameConvertAnnotationFormatterFactory());
		registry.addFormatterForFieldAnnotation(new HpfCorporationKanaConvertAnnotationFormatterFactory());
		registry.addFormatterForFieldAnnotation(new HpfTimeZoneJSTConvertAnnotationFormatterFactory());
	}
}
