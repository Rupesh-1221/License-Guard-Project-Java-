package com.licenseguard;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Disabled in CI because it requires a live database connection")
class LicenseguardApplicationTests {

	@Test
	void contextLoads() {
	}

}
