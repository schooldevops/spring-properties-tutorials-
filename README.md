# Spring 프로퍼치를 처리하는 방법

- Spring에서는 하드코딩을 피하고, 구현하고자 하는 시스템의 환경에 따라 환경변수를 변경할 수 있는 다양한 방법을 제공하고 있다. 
- 이런 처리를 수행하기 위해서 Properties 를 설정하고, 필요한 Properties를 로드하여 시스템에 맞는 환경에서 시스템을 기동 할 수 있다. 

## Overview

- 이번 아티클에서는 다음과 같은 방법들로 프로퍼티를 설정할 것이다. 
- 기본적으로 SpringBoot 를 이용하여 진행할 예정이다. 

1. 기본적인 SpringBoot properties 이용하기. 
2. 프로퍼티 객체를 생성하고 활용하기. 
3. @PropertySource 이용하기. 

## 1. 기본적인 SpringBoot properties 이용하기.

- 스프링부트 어플리케이션을 https://start.spring.io 에서 생성하게 되면 다음 경로에 프로퍼티 파일이 생성이 된다. 
- src/main/resources/application.properties

해당 프로퍼티 파일은 스프링 부트에서 바로 사용할 수 있는 내장 프로퍼티도 있으며, 사용자 지정 프로퍼티 설정도 수행할 수 있다. 

application.properties 파일 일부 

- 다음과 같이 프로퍼티 설정을 해주자. 

```yaml
schooldevops.prop-test.name=Schooldevops Test Sample

```

### 프로퍼티 사용하기. 

- 이제 생성한 프로퍼티를 사용하자. 

#### @Value

- 프로퍼티를 사용하는 방법에는 우선 @Value 를 이용하여 프로퍼티 값을 SpEL 을 이용하여 지정할 수 있다. 
- PropTestApplication.java 파일의 내용을 다음과 같이 수정해 보자. 

```java
@Slf4j
@SpringBootApplication
public class PropTestApplication implements CommandLineRunner {

    @Value("${schooldevops.prop-test.name}")
    private String projectName;

    @Value("${schooldevops.prop-test.defaultValue:Hello Program}")
    private String defaultValue;

    public static void main(String[] args) {
        SpringApplication.run(PropTestApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Project Name: {} ", projectName);
        log.info("Project Default Value: {} ", defaultValue);
    }
}


```

- 보는 바와 같이 @Value 어노테이션을 이용하여 프로퍼티 값이 설정되었다. 
- @Value 는 application.properties 에 지정된 키값을 프로그램이 실행할때 로드하고, 값을 할당해 두는 어노테이션이다. 
- @Value("${schooldevops.prop-test.name}") 은 우리가 지정한 값을 읽어와서 변수 projectName 에 'Schooldevops Test Sample' 을 할당한다. 
- @Value("${schooldevops.prop-test.defaultValue:Hello Program}") 은 프로퍼티 파일에 없는 키 값을 세팅했다. 이때에는 기본값이 사용된다. 
  - shcooldevops.prop-test.defaultValue 를 찾아보고 없다면 ':' 뒤에 지정한 기본값을 대입하게 된다. 

- 결과를 확인해보면 다음과 같은 로그를 확인할 수 있다. 

```go
Project Name: Schooldevops Test Sample 
Project Default Value: Hello Program 
```

#### 배열프로퍼티 로드하기

- application.properties 에 배열 형태로 값을 지정해두면, 프로그램 실행시 배열로 값을 로드할 수 있다. 
- application.properties 에 다음과 같이 값을 설정해 보자. 
- 다음은 친구목록을 ','분리하여 지정하고 있다. 

```go
schooldevops.prop-test.friends=Chulsu,Dongsoo,Konan,Moana
```

- 배열 변수로 저장하기. 

```java
// ... 생략
@Value("${schooldevops.prop-test.friends}")
private String[] friends;

//... 생략

    @Override
    public void run(String... args) throws Exception {
    
        Stream.of(friends).forEach(System.out::println);
    }

```

- 아래와 같이 배열로 정상적으로 저장된 값이 출력됨을 알 수 있다. 

```go
Chulsu
Dongsoo
Konan
Moana
```

#### @Value 에서 SePL 이용하기

- @Value는 SePL 을 이용하여 스프링내부의 변수에 접근할 수 있다. 

```java
//	... 생략 

	@Value("#{systemProperties['java.version']}")
	private String javaVersion;

	@Value("#{systemProperties['java.version.my'] ?: '11.0'}")
	private String javaVersionWithDefault;

	@Value("#{'${schooldevops.prop-test.friends}'.split(',')}")
	private List<String> friendList;
	
//	... 생략 

        log.info("System Prop: {}", javaVersion);
        log.info("Java Version from System Prop: {}", javaVersionWithDefault);

        friendList.forEach(System.out::println);
```

- @Value("#{systemProperties['java.version']}") 을 이용하면 시스템 프로퍼티에 접근하여 프로퍼티 값을 조회할 수 있다.
- SystemProperties는 'System.getProperties()' 의 조회 결과를 시스템 프로퍼티를 확인할 수 있다.
- 결과는 다음과 같이 출력된다. 

```go
System Prop: 11.0.10
```
- @Value("#{systemProperties['java.version.my'] ?: '11.0'}") 에서 SePL 은 '?:' 을 이용하여 값이 없는경우 기본값을 확인할 수 있다.
- 결과와 같이 기본값을 확인할 수 있다. 

```go
Java Version from System Prop: 11.0
```

- 이전 예제에서는 String[] 으로 값을 받았다. 그러나 List로 받기 위해서는 @Value("#{'${schooldevops.prop-test.friends}'.split(',')}") 으로 delimety를 이용하여 리스트로 변환할 수 있다. 
- 결과는 다음과 같이 출력할 수 있다. friendList.forEach(System.out::println);

```go
Chulsu
Dongsoo
Konan
Moana
```

#### Map 으로 값 받기 

- 설정값을 Map으로 받기 위해서 다음과 같이 application.properties 에 설정하자. 

```go
schooldevops.prop-test.cutline={A: '90', B: '80', C: '70', D: '60', F: '59'}
```

- SePL 을 이용하기 위해서 @Value를 다음과 같이 코드를 입력하자. 

```go
// ...생략
	@Value("#{${schooldevops.prop-test.cutline}}")
	private Map<String, Integer> cutline;
// ...생략

log.info("Cutline Level: {}", cutline);
```

- 위 결과를 확인하면 다음과 같은 결과를 확인할 수 있다. 

```go
Cutline Level: {A=90, B=80, C=70, D=60, F=59}
```

#### Environment 이용하기. 

- 로드된 프로퍼티는 Environment 객체로 저장된다. 
- 이 값을 그대로 이용할 수 있으며, 다음과 같이 접근이 가능하다. 

```java
// ...생략
    @Autowired
	Environment env;

// .. 조회하기 
    log.info("Project Name from env: {}", env.getProperty("schooldevops.prop-test.name"));
```

- Environment 를 Autowired 하고, getProperty 메소드를 이용하여 조회할 수 있다. 
- env.getProperty("schooldevops.prop-test.name") 와 같이 설정된 프로퍼티 full 이름을 이용하여 접근하면 된다. 
- 결과는 다음과 같이 확인할 수 있다. 

```go
Project Name from env: Schooldevops Test Sample
```

## 2. 프로퍼티 객체를 생성하고 활용하기.

- 지금까지는 프로퍼티 값을 @Value나 Environment 를 이용하여 값을 조회할 수 있다. 
- 그러나 이들 설정의 단점은 값을 조회하기 위해서 스트링으로 해당 프로퍼티 키를 기술해야해서 오타로 인한 오동작이 있을 수 있다. 
- 우리는 프로퍼티를 객체로 받아 객체 조회를 통한 값에 접근할 수 있다. 
- 이를 위해 @ConfigurationProperties 를 이용하여 프로퍼티 객체를 생성할 수 있다. 

### 프로퍼티 설정하기 (properties)
- 우선 application.properties 에 다음가 같이 작성하자. 

```go
schooldevops.prop-test.name=Schooldevops Test Sample
schooldevops.prop-test.friends=Chulsu,Dongsoo,Konan,Moana
schooldevops.prop-test.cutline2.A=90
schooldevops.prop-test.cutline2.B=80
schooldevops.prop-test.cutline2.C=70
schooldevops.prop-test.cutline2.D=60
schooldevops.prop-test.cutline2.F=59
```

- 달라진 부분은 schooldevops.prop-test.cutline2 부분이다. 맵으로 변환하기 위해서는 키로 값들을 설정해 주어야한다.

### 프로퍼티 설정하기 (yml)
- 만약 yml 을 이용한다면 다음과 같이 작성될 것이다. 

```yaml
schooldevops:
  prop-test:
    name: Schooldevops Test Sample
    friends: Chulsu,Dongsoo,Konan,Moana
    cutline: {A: '90', B: '80', C: '70', D: '60', F: '59'}
    cutline2:
      A: 90
      B: 80
      C: 70
      D: 60
      F: 59
```

### PropertiesConfig 클래스 생성하기. 

- 이제는 이를 조회할 객체를 생성하자. PropValue.java 파일을 하나 만들고 다츰 코드를 작성하자. 

```java

package com.schooldevops.proptest.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "schooldevops.prop-test")
public class PropValue {
    private String name;
    private String[] friends;
    private Map<String, Integer> cutline2;

    // getter/setter 생략 
}

```

- @ConfigurationProperties 는 프로퍼티 내에서 존재하는 프로퍼티 계층에서 prefix 를 지정할 수 있도록 한다. 
- prefix는 필수 값이며 'schooldevops.prop-test' 가 우리가 받을 값이다. 여기서 '.' 값들은 Object레벨로 하위 객체를 가지는 구조임을 명심하자. 
- 그리고 객체 필드로 최종 프로퍼티 이름을 지정했다. 

### 프로퍼티 사용하기. 

- 이제 프로퍼티를 사용하자. 
- 프로퍼티 객체를 사용하기 위해서는 @EnableConfigurationProperties(value = {PropValue.class}) 와 같이 사용할 프로퍼티를 기술해 주어야한다. 

```java

@Slf4j
@EnableConfigurationProperties(value = {PropValue.class})
@SpringBootApplication
public class PropTestApplication implements CommandLineRunner {

	// ... 생략 

	@Autowired
	PropValue propValue;

	public static void main(String[] args) {
		SpringApplication.run(PropTestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
        // ... 생략 
		log.info("PropValue: {}, {}, {}", propValue.getName(), propValue.getFriends(), propValue.getCutline2());
	}


}
```

- 이제 PropValue를 @Autowired 하고 바로 접근할 수 있게 되었다. 
- 결과는 다음과 같이 출력됨을 알 수 있다. 

```go
PropValue: Schooldevops Test Sample, [Chulsu, Dongsoo, Konan, Moana], {A=90, B=80, C=70, D=60, F=59}
```

### 계층을 가진 프로퍼티 로드하기. 

- properties 에서는 '.' 을 기준으로 계층 구조로 프로퍼티가 설정이 된다. 
- @ConfigurationProperties 을 이용하여 prefix를 어느 레벨로 정하느냐에 따라 프로퍼티는 하위 객체를 가지는 구조로 구성할 수 있다. 
- 이번샘플에서는 schooldevops.student 라는 프로퍼티를 만들고, 하위에 user, address 정보를 가지는 속성을 설정해 보자.

#### application.properties 작성 

- application.properties 를 다음과 같이 작성하자. 

```go
schooldevops.student.user.name=KIDO
schooldevops.student.user.age=10
schooldevops.student.user.subject=Computer Science

schooldevops.student.address.post-num=123-456
schooldevops.student.address.main-address=을지로2가
schooldevops.student.address.detail-address=Tower
```

- user.name, user.age, user.subject 를 설정하여 user 정보를 설정했다. 
- address.post-num, address.main-address, address.detail-address 를 설정하여 address 정보를 설정했다. 

#### ConfigurationProperty 구성하기 

- 이제 @ConfigurationProperties 를 이용하여 프로퍼티를 객체로 담아 보자. 
- StudentPropValue.java 를 생성하고 다음과 같이 작성한다. 

```java
package com.schooldevops.proptest.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "schooldevops.student")
public class StudentPropValue {

    private User user;
    private Address address;

    // getter/setter 생략 

    public static class User {
        private String name;
        private Integer age;
        private String subject;

      // getter/setter 생략 
    }

    public static class Address {
        private String postNum;
        private String mainAddress;
        private String detailAddress;

        // getter/setter 생략 
    }

}


```

- @ConfigurationProperties(prefix = "schooldevops.student") 를 이용하여 프로퍼티를 위한 prefix 를 설정했다. 
- public static class Use 클래스를 생성하고, 필드를 지정했다. 
- public static class Address 클래스를 생성하고, 필드를 지정했다. 

- 위와 같은 방법으로 프로퍼티 객체 (StudentPropValue) 가 User, Address 객체를 담을 수 있도록 계층을 가지도록 구성했다. 

#### 프로퍼티 사용하기. 

- 이제 프로퍼티를 사용하기 위해서 다음과 같이 테스트해보자. 

```java
package com.schooldevops.proptest;

import com.schooldevops.proptest.props.PropValue;
import com.schooldevops.proptest.props.StudentPropValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@EnableConfigurationProperties(value = {PropValue.class, StudentPropValue.class})
@SpringBootApplication
public class PropTestApplication implements CommandLineRunner {
    // ... 생략 

	@Autowired
	StudentPropValue studentPropValue;

	public static void main(String[] args) {
		SpringApplication.run(PropTestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// ... 생략 
		log.info("Student Info : {} {} {}", studentPropValue.getUser().getName(), studentPropValue.getUser().getAge(), studentPropValue.getUser().getSubject());
		log.info("Address Info : {} {} {}", studentPropValue.getAddress().getPostNum(), studentPropValue.getAddress().getMainAddress(), studentPropValue.getAddress().getDetailAddress());
	}
}

```

- 위와 같이 @EnableConfigurationProperties(value = {PropValue.class, StudentPropValue.class}) 을 통해 프로퍼티를 등록한다.
- StudentPropValue studentPropValue; 을 @Autowired 를 수행했다.
- 마지막으로 설정한 객체를 조회하기 위해서 log.info("Student Info : {} {} {}", studentPropValue.getUser().getName(), studentPropValue.getUser().getAge(), studentPropValue.getUser().getSubject()); 를 사용했다.

#### 결과보기

```go
Student Info : KIDO 10 Computer Science
Address Info : 123-456 을지로2가 Tower
```

- 원하는 결과를 확인할 수 있다. 
- 위 과정을 통해서 프로퍼티를 계층적으로 구성할 수 있음을 알 수 있다. 

## 3. @PropertySource 이용하기.

- 이제 프로퍼티 파일이 여러개인경우 어떻게 프로퍼티를 로드할 수 있는지 알아보자. 
- application.properties 만으로도 충분히 프로퍼티를 설정할 수 있지만, 필요에 따라 프로퍼티를 분리하고, 분리 관리하고 싶을 수 있다. 
- 이럴때 @PropertySource 를 활용하여 프로퍼티 파일을 구분해서 로드할수 있다. 

### db 설정 확인하기. 

- maria db에 접속한다고 해보자. 이를 위해서 db.properties 파일을 하나 생성하고 다음과 같이 작성한다.

```go
db.maria.url=localhost:3306
db.maria.db-name=testdb
db.maria.user-name=testuser
db.maria.password=test123
```

### 프로퍼티 객체 생성하기.

- 동일하게 DBPropValue.java 파일을 다음과 같이 작성하자. 

```java
package com.schooldevops.proptest.props;

import org.springframework.context.annotation.PropertySource;

@ConfigurationProperties(prefix = "db.maria")
public class DBPropValue {

    private String url;
    private String dbName;
    private String userName;
    private String password;

    // getter/setter 생략 
}

```

- 프로퍼티 @ConfigurationProperties(prefix = "db.maria") 으로 프로퍼티 prefix를 작성했다. 

### 프로퍼티 로드 하기. 

- 이제 프로퍼티 로드를 위해서 @PropertySource 를 작성하자. 
- PropTestApplication.java 파일을 다음과 같이 작성하자. 

```java
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
@EnableConfigurationProperties(value = {PropValue.class, StudentPropValue.class, DBPropValue.class})
@SpringBootApplication
public class PropTestApplication implements CommandLineRunner {
    // ... 생략하기 
	@Autowired
	DBPropValue dbPropValue;

	@Value("${db.maria.url}")
	private String dbUrl;

	public static void main(String[] args) {
		SpringApplication.run(PropTestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		log.info("DB Prop: {}, {}, {}, {}", dbPropValue.getDbName(), dbPropValue.getUrl(), dbPropValue.getUserName(), dbPropValue.getPassword());
		log.info("DB Prop: {}", dbUrl);
	}


}

```

- 외부 프로퍼티 파일은 @PropertySource("classpath:db.properties") 으로 프로퍼티를 지정해 주어야한다. 
- 그리고 프로퍼티 파일을 이용하기 위해서 @EnableConfigurationProperties(value = {PropValue.class, StudentPropValue.class, DBPropValue.class}) 으로 프로퍼티 객체를 활성화 했다.
- 위 예제와 같이 DBPropValue 를 @Autowired 해서 객체를 사용할 수 있다. 
- 또한 @Value 를 이용하여 직접 DB 프로퍼티에 접근할 수도 있다. 

### 결과 보기

- 이제 결과를 출력해보자. 

```go
DB Prop: testdb, localhost:3306, testuser, test123
DB Prop: localhost:3306
```

- 원하는 결과를 확인할 수 있다.

### 복수개의 프로퍼티 파일 로드하기

- 프로퍼티 파일이 여러개인 경우 다음 2가지 방법으로 프로퍼티를 로드할 수 있다. 
- 복수개의 @PropertySource 설정하기. 

```java

@PropertySource("classpath:config.properties")
@PropertySource("classpath:db.properties")

```

- 혹은 @PropertySources 를 이용하여 포함관계로 프로퍼티를 로드할 수 있다. 

```java
@PropertySources({
    @PropertySource("classpath:config.properties"),
    @PropertySource("classpath:db.properties")
})
```


