package online.wgear.test.spring_boot_lezhnin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class SpringBootLezhninApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootLezhninApplication.class, args);
    }

}
