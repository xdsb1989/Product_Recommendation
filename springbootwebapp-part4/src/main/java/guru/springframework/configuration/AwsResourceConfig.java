package guru.springframework.configuration;

import org.springframework.cloud.aws.jdbc.config.annotation.EnableRdsInstance;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:/aws-config.xml")
//@EnableRdsInstance(databaseName = "food", 
//                   dbInstanceIdentifier = "rds-test1-db", 
//				   password = "xdsb1989")
public class AwsResourceConfig {
	
}
