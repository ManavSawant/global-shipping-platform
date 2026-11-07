package com.MNS.global_shipping_platform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:shipping-platform-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"jwt.secret=test-only-secret-with-at-least-32-characters",
		"jwt.expiration=3600000"
})
class GlobalShippingPlatformApplicationTests {

	@Test
	void contextLoads() {
	}

}
