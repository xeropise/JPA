## 엔티티 관련

**1. @Entity**

- JPA는 Entity가 설정된 클래스로부터 생성된 객체만 엔티티로 인지하고 사용할 수 있다. name 속성으로 엔티티 이름을 지정할 수 있다.
- name 속성을 생략하면 클래스 이름이 엔티티 이름이 된다. 엔티티 이름이 중요한 이유는 JPQL 을 사용 시, 엔티티 이름을 사용하기 때문이다.

<br>

**2. @Table**

- 엔티티와 매핑할 테이블을 지정한다.
- 기본적으로 엔티티 이름과 동일한 이름의 테이블이 매핑되지만 이름이 다른 경우, name 속성을 사용하여 매핑할 테이블을 지정해야 한다.

| 속성              | 설명                                                                                                |
| ----------------- | --------------------------------------------------------------------------------------------------- |
| name              | 매핑될 테이블 이름을 지정한다                                                                       |
| catalog           | 데이터베이스 카탈로그를 지정한다 (MySQL)                                                            |
| schema            | 데이터베이스 스키마를 지정한다 (schema)                                                             |
| uniqueConstraints | 결합 unique 제약조건을 지정하며, 여러 개의 칼럼이 결합되어 유일성을 보장해야 하는 경우 사용(복합키) |

<br>

**3. @Id**

- 테이블의 기본 키와 매핑되는 식별자 변수를 매핑
- 식별자 변수는 테이블의 기본 키(Primary Key)와 매핑되는 변수를 의미한다.

<br>

**4. @Column**

- 엔티티 클래스의 멤버 변수와 테이블의 컬럼을 매핑할 때 사용한다. 일반적으로 엔티티의 멤버 변수 이름과 칼럼 이름이 다를 때 사용하며, 생략하는 경우 기본적으로 변수 이름과 동일한 칼럼이 매핑된다.

- 지원하는 속성이 매우 다양하지만, 칼럼 이름 지정에 사용하는 name 과 NULL 입력을 방지하는 nullable 정도를 주로 사용한다.

| 속성             | 설명                                                               | 기본 값 |
| ---------------- | ------------------------------------------------------------------ | ------- |
| name             | 매핑될 칼럼 이름을 지정한다 (생략 시 변수 이름과 동일한 칼럼 매핑) |         |
| unique           | unique 제약조건 설정                                               | false   |
| nullable         | null 허용 여부 설정                                                | false   |
| insertable       | INSERT SQL을 생성할 때 이 칼럼을 포함할지 설정                     | true    |
| updatable        | UPDATE SQL을 생성할 때 이 칼럼을 포함할지 설정                     | true    |
| columnDefinition | 이 칼럼에 대한 DDL문을 직접 기술                                   |         |
| length           | 문자열 타입의 칼럼 길이 지정                                       | 255     |
| precision        | 숫자 타입의 전체 자릿수 지정                                       | 0       |
| scale            | 숫자 타입의 소수점 자릿수 지정                                     | 0       |

<br>

**5. @Temporal**

- java.util.Date 타입의 변수에 사용 가능하며, 날짜 데이터를 매핑할 때 사용한다.
- TemporalType.DATE(날짜), TemporalType.TIME(시간), TemporalType.TIMESTAMP(날짜와 시간) 를 사용 가능하다.

<br>

**6. @Transient**

- 몇몇 멤버변수는 매핑되는 칼럼이 없거나 또는 임시로 사용되는 변수들을 아예 매핑해서 제외해야할때 사용

- 엔티티 클래스 내의 특정 변수를 여속 필드에서 제외할 때 사용

<br>

**7. @Access**

- JPA가 엔티티의 멤버 변수에 접근하는 방식을 지정

  - AccessType.FIELD : 멤버 변수에 직접 접근
  - AccessType.PROPERTY : Getter/Setter 메소드를 통해 접근

- 명시적으로 접근 방법을 지정하지 않으면 @Id 나 @EmbeddedId 가 어디에 위치했느냐에 따라 접근 방식을 정한다.

<br>

**8. @EntityGraph**

- 쿼리 메소드 마다 연관 관계의 Fetch 모드를 유연하게 설정 가능하다.
- 기본적으로 @ManyToOne 은 Fetch 모드 기본값이 Eager, @OneToMany 는 (Many 로 끝나는 경우) 기본값이 LAZY 이다.
- EAGER 의 경우, comment, post 엔티티가 있다면 comment 정보를 가져올 떄 post 정보도 미리 가져오지만 LAZY 의 경우 post 데이터가 필요한 시점에 쿼리를 날린다.

- 영속성 때문에 LAZY LOADING 으로 발생하는 N+1 쿼리 문제 해결을 위해 사용할 수 있다.

```java
@EntityGraph(attributePaths = "subjects")
@Query("select a from post a")
List<post> findAllPost();
```

- EntityGraph 의 attributePaths에 쿼리 수행할 때 같이 가져올 필드명을 지정하면 Lazy 가 아닌 Eager로 조회해서 가져온다.

- 속성 값 중 type을 이용해 attributePaths 에 정의한 필드값만 EAGER 로 불러오고 나머지 필드 값 LAZY 혹은 각자 FetchType 으로 가져오게 할 수 있다.

  - FETCH: entity graph에 명시한 attribute는 EAGER로 패치하고, 나머지 attribute는 LAZY로 패치

  - LOAD: entity graph에 명시한 attribute는 EAGER로 패치하고, 나머지 attribute는 entity에 명시한 fetch type이나 디폴트 FetchType으로 패치 (e.g. @OneToMany는 LAZY, @ManyToOne은 EAGER 등이 디폴트이다.)

```java
@EntityGraph(attributePaths = "country", type = EntityGraphType.FETCH)
@EntityGraph(attributePaths = "country", type = EntityGraphType.LOAD)
```

  <br>

**9. @ElementCollection, @CollectionTable**

- RDB 에서는 내부적으로 컬렉션을 담을 수 있는 구조가 없다. 값만 넣을 수 있다. 이런 관계를 DB 테이블에 저장하려면 별도의 테이블이 필요하다.

```java
@ElementCollection
@CollectionTable(name = "FAVORITE_FOOD", joinColumns = @JoinColumn(name = "MEMBER_ID"))
@Column(name = "FOOD_NAME")
private Set<String> favoriteFoods = new HashSet<>();

@ElementCollection
@CollectionTable(name = "ADDRESS", joinColumns = @JoinColumn(name = "MEMBER_ID"))
private List<Address> addressHistory = new ArrayList<>();
```

- 위와 같이 선언한 값 타입 컬렉션을 가지고 있으면, RDB 에서 FAVORITE_FOOD, ADDRESS 라는 별도의 테이블을 만들어서 관리하게 된다.

- @CollectionTable 의 속성으로 테이블의 이름과, 외래키를 지정해 줄 수 있다.

- 값타입 컬렉션도 값 타입이기 때문에 생명주기를 가지지 않고, 엔티티와 같은 생명주기를 따라간다. 주의사항은 값 타입 분류를 참조하자.

  <br>

**10. @Embeddable, @Embedded, @AttributeOverrides**

- 한 클래스를 엔티티의 컬럼 객체로 사용하기 위해 사용한다.

- 주소를 세부 주소1, 세부 주소2 등으로 정의할 수 있다. 이를 한 엔티티에 펼처 놓는것이 아닌 밸류 타입으로 쓰는 것

```java 
@Embeddable
public class Address {

	private address1
	private address2 
	private zipCode; 
}

```

```java
@Entity
@Table(name = "user")
public class UserEntity {

	...
	
	@Embedded
	@AttributeOverrides(
		@AttributeOverride(name = "address1", column = @Column(name = user_address1))
	)
	private Address address;
}
```

  <br>

**11. @Enumerated**

- 자바 엔티티 필드의 Enum 타입을 데이터베이스에 매핑할 때 어떻게 매핑할 것인지 정해주는 어노테이션이다.

|속성 명|설명|
|------|---|
|EnumType.ORDINAL| enum 순서 값을 DB에 저장|
|EnumType.STRING| enum 이름을 DB에 저장|

```java
enum Gender {
   MALE,
   FEMALE
}

@Enumerated(EnumType.ORDINAL)
private Gender gender    // MALE로 세팅하면 1, FEMALE 은 2

@Enumerated(EnumType.STRING)
private Gender gender;   // "MALE", "FEMALE" 문자열 자체가 저장
```

---

### 식별자 값 자동 생성

- JPA 는 테이블의 기본 키와 엔티티의 식별자 변수를 매핑하여 유일한 엔티티 객체를 식별하고 관리한다.

- 식별자 변수에는 사용자가 직접 값을 할당할 수도 있으나, 일반적으로 애플리케이션에서 자동으로 증가하도록 하는데, JPA 에서는 식별자 값을 자동으로 생성하고 할당하는 다양한 전략이 있다.

  **1. @GeneratedValue**

- 식별자 변수에 자동으로 증가된 값을 할당할 때는 @Id 가 적용된 식별자 변수 위에 이 어노테이션을 추가하면 된다.

  | 속성      | 설명                                |
  | --------- | ----------------------------------- |
  | strategy  | 식별자 값 자동 생성 전략을 선택한다 |
  | generator | 생성된 키 생성기를 참조한다.        |

<br>

- GenerationType.IDENTITY
  : auto_increment나 IDENTITY를 이용하여 PK값을 생성, MYSQL 같은 데이터베이스 이용할 때 사용

  <br>

- GenerationType.SEQUENCE
  : 시퀀스를 이용하여 PK 값을 생성, 오라클 같은 시퀀스 지원 데이터베이스에서만 사용 가능

  <br>

- GenerationType.TABLE
  : 키 관리를 위한 전용 테이블을 사용하여 PK 값을 생성

  <br>

- GenerationType.AUTO
  : 하이버네이트가 데이터베이스에 맞는 PK 값 생성 전략을 선택한다. (기본값)

<br>

### 공통 Entity 관련

1. @MappedSuperclass

   - 테이블과 매핑하지 않고 부모 클래스를 상속 받는 자식 클래스에게 매핑 정보만 제공을 위해 사용하는 어노테이션

   - @Entity는 실제 테이블과 매핑되지만, @MappedSuperclass 는 실제 테이블과 매핑되지 않는다.

   - 공통 값인 등록일, 수정일 등 단순히 매핑 정보를 상속한 목적으로만 사용하는 어노테이션

1. @EntityListeners

   - Entity를 통해 자동으로 값을 넣어주기 위해 사용하는 어노테이션

   - 엔티티를 DB에 적용하기 전에 (FLUSH 전) 커스텀 콜백을 요청할 수 있는 어노테이션이다.

   - @CreatedDate, @LastModifiedDate 을 통해 엔티티가 생성되거나 저장될 때, 값을 변경할 때 시간이 자동 저장된다.

---

## Config 관련 설정

1. @EnableTransactionManagement

   - XML의 \<tx:annotation-driven> 과 동일한 스프링에서 Java Config 파일에서 트랜잭션을 활성화 할 떄 사용하는 어노테이션

   - 스프링부트의 경우 AutoConfiguration 으로 설정되어있다.

   - @Transactional 을 사용할 수 있다.

2. @EnableJpaRepositories

   - 스프링 컨테이너가 리포지터리 인터페이스들을 인지하여 리포지터리 인터페이스에 대한 구현 객체를 생성하도록 한다.

   - basePackage 속성을 통해 인포지터리 인터페이스들이 들어있는 패키지를 지정할 수 있다.

   - @Bean 사용하는 경우 txManager 메소드로 생성된 JpaTransactionManager 객체는 이름이 txManager 가 되는데, 다른 이름을 사용하고 싶은 경우 @Bean 의 name 속성으로 지정해야 한다.

   - LocalContainerEntityManagerFactoryBean 과 JpaTransactionManager 는 다른 객체와 달리 고정된 이름을 사용해야 하는데 스프링 컨테이너가 정해진 이름으로 등록된 객체만 사용할 수 있도록 프로그램되어 있따.

   - @Bean 을 사용하되, name 속성을 사용하고 싶지 않다면 @EnableJpaRepositories 어노테이션이 가진 entityManagerFactory 속성과 transactionManagerRef 속성에 해당 객체의 메소듸 이름을 등록해야 한다.

```java
@Configuration
@PropertySource("classpath:/META-INF/database.properties")
@EnableTransactionManagement
@EnableJpaRepositories(
		basePackages = "org.xeropise.portfolio.mvc.repository",
		entityManagerFactoryRef = "factoryBean",
		transactionManagerRef = "txManager"
)
public class DatabaseConfig {

	@Value("${database.driverClassName}")
	private String driver;

	@Value("${database.url}")
	private String url;

	@Value("${database.username}")
	private String username;

	@Value("${database.password}")
	private String password;

	@Bean
	public HibernateJpaVendorAdapter vendorAdaptor() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();

		return adapter;
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driver);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);

		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean factoryBean() {

		// Hibernate에서 SessionFactoryBean과 동일한 역활을 담당하는 FactoryBean이다.
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setJpaVendorAdapter(vendorAdaptor());
		factoryBean.setDataSource(dataSource());

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect");
		properties.put("hibernate.show_sql", "true");
		properties.put("hibernate.format_sql", "true");
		properties.put("hibernate.id.new_generator_mappings", "true");
		properties.put("hibernate.hbm2ddl.auto", "create");

		factoryBean.setJpaPropertyMap(properties);

		return factoryBean;
	}

	@Bean
	public JpaTransactionManager txManager(EntityManagerFactory factory) {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(factory);
		return txManager;
	}
}

```
