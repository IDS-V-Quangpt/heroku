package jp.co.hyas.hpf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
@SpringBootApplication(scanBasePackages=
	{
		"jp.co.hyas.hpf.database",
		"jp.co.hyas.hpf.database.property"
	}
)
*/
//@ComponentScan(nameGenerator = FQCNGenerator.class)
@SpringBootApplication
public class HyasHpfSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(HyasHpfSpringApplication.class, args);
	}
}
