package sqldiff.spring.boot.simple.sample;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("testcontainers")
public class SimpleApplicationTests {

    @Test
    public void contextLoads() {
    }

}
