package jpabook.solo_jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;



@SpringBootApplication
public class SoloJpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoloJpashopApplication.class, args);
	}

	@Bean
	Hibernate5Module hibernate5Module(){

		Hibernate5Module hibernate5Module = new Hibernate5Module();
		//강제 지연 로딩 설정 //대신 있는 놈들만 나오고 없는 놈들은 null
		//hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
		return new Hibernate5Module();
	}
}
