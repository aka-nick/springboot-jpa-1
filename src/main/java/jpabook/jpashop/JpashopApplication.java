package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	@Bean
	Hibernate5Module hibernate5Module() {
		Hibernate5Module hibernate5Module = new Hibernate5Module(); // 지연 로딩으로 인한 프록시객체가 없어서 ByteBuddy(JPA 프록시 객체 생성하는 라이브러리) 관련 오류가 날 때 하이버네이트5모듈을 사용하면 해결될 수 있다.
//		hibernate5Module.configure(Feature.FORCE_LAZY_LOADING, true); // 지연로딩 강제로 땡기기
		return hibernate5Module;
	}

}
