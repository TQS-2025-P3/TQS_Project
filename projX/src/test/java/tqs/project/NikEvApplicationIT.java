package tqs.project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import app.getxray.xray.junit.customjunitxml.annotations.XrayTest;

@XrayTest(key = "TQSPROJECT-469")
@SpringBootTest
class NikEvApplicationIT {

	@Test
	void contextLoads() {
	}

}
