package com.schooldevops.proptest;

import com.schooldevops.proptest.props.DBPropValue;
import com.schooldevops.proptest.props.PropValue;
import com.schooldevops.proptest.props.StudentPropValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@PropertySource("classpath:db.properties")
@PropertySource("classpath:config.properties")
@EnableConfigurationProperties(value = {PropValue.class, StudentPropValue.class, DBPropValue.class})
@SpringBootApplication
public class PropTestApplication implements CommandLineRunner {

	@Value("${schooldevops.prop-test.name}")
	private String projectName;

	@Value("${schooldevops.prop-test.defaultValue:Hello Program}")
	private String defaultValue;

	@Value("${schooldevops.prop-test.friends}")
	private String[] friends;

	@Value("#{systemProperties['java.version']}")
	private String javaVersion;

	@Value("#{systemProperties['java.version.my'] ?: '11.0'}")
	private String javaVersionWithDefault;

	@Value("#{'${schooldevops.prop-test.friends}'.split(',')}")
	private List<String> friendList;

	@Value("#{${schooldevops.prop-test.cutline}}")
	private Map<String, Integer> cutline;

	@Autowired
	Environment env;

	@Autowired
	PropValue propValue;

	@Autowired
	StudentPropValue studentPropValue;

	@Autowired
	DBPropValue dbPropValue;

	@Value("${db.maria.url}")
	private String dbUrl;

	@Value("${api.user.url}")
	private String userApiUrl;

	public static void main(String[] args) {
		SpringApplication.run(PropTestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Project Name: {} ", projectName);
		log.info("Project Default Value: {} ", defaultValue);

		Stream.of(friends).forEach(System.out::println);

//		log.info("System Prop: {}", System.getProperties());
		log.info("System Prop: {}", javaVersion);
		log.info("Java Version from System Prop: {}", javaVersionWithDefault);

		friendList.forEach(System.out::println);
		
		log.info("Cutline Level: {}", cutline);

		log.info("Project Name from env: {}", env.getProperty("schooldevops.prop-test.name"));

		log.info("PropValue: {}, {}, {}", propValue.getName(), propValue.getFriends(), propValue.getCutline2());

		log.info("Student Info : {} {} {}", studentPropValue.getUser().getName(), studentPropValue.getUser().getAge(), studentPropValue.getUser().getSubject());
		log.info("Address Info : {} {} {}", studentPropValue.getAddress().getPostNum(), studentPropValue.getAddress().getMainAddress(), studentPropValue.getAddress().getDetailAddress());

		log.info("DB Prop: {}, {}, {}, {}", dbPropValue.getDbName(), dbPropValue.getUrl(), dbPropValue.getUserName(), dbPropValue.getPassword());
		log.info("DB Prop: {}", dbUrl);

		log.info("User api url: {}", userApiUrl);
	}


}
