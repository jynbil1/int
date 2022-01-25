package hanpoom.internal_cron;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
@MapperScan(value = {"hanpoom.internal_cron.*"})
public class RolandApplication {

	public static void main(String[] args) {
		SpringApplication.run(RolandApplication.class, args);
	} 
}
