package eu.sparfeld.gbtec.challenge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ChallengeApplicationTests {

    @Test
    void contextLoads() {
        // Just tests whether the context loads
    }

}
