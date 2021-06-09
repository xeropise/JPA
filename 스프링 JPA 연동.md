# 스프링 JPA 연동

- 스프링 프레임워크와 JPA를 연동해 보자.

- 메이븐 설정 파일에 스프링 프레임워크 관련 라이브러리 의존성을 추가하자.

  ```xml
          <!-- 스프링 프레임워크 라이브러리 -->

          <!-- 스프링 컨테이너 관련 핵심 모듈 -->
          <dependency>
              <groupId>org.springframework</groupId>
              <artifactId>spring-context</artifactId>
              <version>${springframework.version}</version>
          </dependency>

          <!-- 스프링 AOP 관련 모듈 -->
          <dependency>
              <groupId>org.springframework</groupId>
              <artifactId>spring-aop</artifactId>
              <version>${springframework.version}</version>
          </dependency>

          <!-- JPA 연동 관련 모듈 -->
          <dependency>
              <groupId>org.springframework</groupId>
              <artifactId>spring-orm</artifactId>
              <version>${springframework.version}</version>
          </dependency>

          <!-- AspectJ 라이브러리 -->
          <!-- 트랜잭션을 공통으로 처리하기 위함 -->
          <dependency>
              <groupId>org.aspectj</groupId>
              <artifactId>aspectjrt</artifactId>
              <version>1.9.5</version>
          </dependency>

          <dependency>
              <groupId>org.aspectj</groupId>
              <artifactId>aspectjweaver</artifactId>
              <version>1.9.5</version>
          </dependency>
  ```

- 스프링과 JPA 연동에 필요한 설정 파일을 작성하자.

  ```xml
  <?xml version="1.0" encoding="UTF-8">
  <beans ~ 생략 ~ >

      <!-- 컴포넌트 스캔 설정 -->
      <!-- @Component @Service @Controller @Repository 클래스를 스프링 컨테이너에 등록 -->
      <context:component-scan base-package="com.rubypaper.biz" />

      <!-- JPA 구현체 -->
      <bean id="jpaVendorAdapter"
      class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter">

      <!-- EntityManagerFactory -->
      <!-- 자동으로 /META-INF/persistence.xml 파일을 로딩 -->
      <bean id="entityManagerFactory"
      class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
          <property name="jpaVendorAdapter" ref="jpaVendorAdapter">
      </bean>


  ```

<br>

- JPA 연동에 필요한 DAO 클래스와 비슷한 리포지터리 클래스를 생성해 보자.

  ```java
  @Repository
  public class EmployeeRepository {

      @PersistenceContext
      // 스프링 컨테이너가 @PersistenceContext 설정 변수에 EntityManager 객체 자동 할당
      private EntityManager em;

      public void insertEmployee(Employee employee) {
          em.persist(employee);
      }

      public void updateEmployee(Employee employee) {
          em.merge(employee);
      }

      public void deleteEmployee(Employee employee) {
          em.remove(em.find(Employee.class, employee.getId()));
      }

      public Employee getEmployee(Employee employee) {
          return (Employee) em.find(Employee.class, employee.getId());
      }

      public List<Employee> getEmployeeList(Employee employee) {
          return em.createQuery("FROM Employee emp ORDER BY emp.id DESC").getResultList();
      }
  }
  ```

- 비즈니스 로직을 처리하는 서비스 클래스를 작성하자.

  ```java
  @Service("empService")
  public class EmployeeService {

      @Autowired
      private EmployeeRepository empRepostiroy;

      public voind insertEmployee(Employee employee) {
          empRepository.insertEmployee(employee);
      }

      public void updateEmployee(Employee employee) {
          empRepository.updateEmployee(employee);
      }

      public void deleteEmployee(Employee employee) {
          empRepository.deleteEmployee(employee);
      }

      public Employee getEmployee(Employee employee) {
          return empRepository.getEmployee(employee);
      }

      public List<Employee> getEmployeeList(Employee employee) {
          return empRepository.getEmployeeList(employee);
      }
  }

  ```

<br>

    ```
    Exception in thread "main" javax.persistece.TransactionRequireException
    ```

<br>

- 트랜잭션에서 동작할 수 있도록 설정하지 않아 에러가 난다. 트랜잭션 관련 설정을 추가하자.

<br>

    ```xml
        <!--=== Transaction 설정 ===-->
        <!--  TransactionManager 등록 -->
        <!-- 트랜잭션 관리자는 commit, rollback 메소드만 구현하고 있다.-->
        <bean id="txManager" class="org.springframework.orm.jpa.JpaTransactionManager">
            <property name="entityManagerFactory" ref="entityManagerFactory" />
        </bean>

        <!--  Transaction 어드바이스 설정  -->
        <!-- 실질적인 트랜잭션을 처리해 준다.-->
        <!-- 특정 메소드에 readonly true 처리를 해주는 것이 좋다 -->
        <tx:advice id="txAdvice" transaction-manager="txManager">
            <tx:attributes>
                <tx:method name="*" rollback-for="Exception" />
            </tx:attributes>
        </tx:advice>

        <!--  Transaction AOP 설정 -->
        <aop:config>
            <aop:pointcut id="txPointcut" expression="execution(* com.rubypaper.biz.service..*Service.*(..))" />

            <aop:advisor advice-ref="txAdvice" pointcut-ref="txPointcut" />
        </aop:config>
    ```

---

## 영속성 유닛 설정 통합하기

- 스프링 컨테이너와 영속 컨테이너는 생성되는 시점이 다르고 컨테이너를 생성하는 주체도 다르다.

- 스프링 컨테이너는 클라이언트가 직접 생성하지만 영속 컨테이너는 클라이언트가 아닌 스프링 컨테이너가 생성한다.

- JPA 설정 파일을 읽어 생성된 스프링 컨테이너는 설정 파일에 LocalContainerEntityManagerFacotryBean 객체를 이용하여 영속 컨테이너를 생성한다.

- EntityManager 를 생성하기 위해서 영속성 유닛(Persistence Unit) 정보가 필요한데 이런 이유로 LocalContainerEntityManagerFactoryBean 이 자동으로 META-INF/persistence.xml 파일을 로딩한다.

- 영속 컨테이너와 관련된 설정은 LocalContainerEntityManagerFactoryBean 에서 모두 처리도록하면 JPA 설정 파일인 persistence.xml 에는 관련 작성을 하지 않아도 된다.

  ```xml
      <!-- EntityManager 공장 -->
      <bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
          <property name="jpaVendorAdapter" ref="jpaVendorAdapter" />
          <property name="dataSource" ref="dataSource"></property>
          <property name="jpaProperties">
              <props>
                  <prop key="hibernate.dialect">org.hibernate.dialect.H2Dialect</prop>
                  <prop key="hibernate.show_sql">true</prop>
                  <prop key="hibernate.foratm_sql">true</prop>
                  <prop key="hibernate.id.new_generator_mappings">true</prop>
                  <prop key="hibernate.hbm2ddl.auto">create</prop>
              </props>
          </property>
      </bean>
  ```

---

## 스프링 설정을 어노테이션 기반으로 변경하기

- XML 설정을 어노테이션 기반의 스프링 설정 클래스로 대체할 수 있다.

```java
@Configuration
@ComponentScan(basePackages = "com.rubypaper.biz")
@EnableTransactionManagement // @Transaction 선언을 클래스에 직접 해야 한다.

public class SpringConfiguration {

	@Bean
	public HibernateJpaVendorAdapter vendorAdaptor() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();

		return adapter;
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:tcp://localhost/~/test");
		dataSource.setUsername("sa");
		dataSource.setPassword("");

		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean factoryBean() {
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setJpaVendorAdapter(vendorAdaptor());
		factoryBean.setDataSource(dataSource());

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
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

<br>

- Service 클래스에 @Transactional 선언하여 transaction 처리를 해주자.

```java
@Service("deptService")
@Transactional
public class DepartmentService {
```

- @Transactional 의 동작 방법은 추가로 정리하도록 하겠다.

---

# 스프링 데이터 JPA

- JPA 를 사용해도, 반복적으로 리포지터리 클래스들을 작성하다보면 중복되는 코드가 등장, CRUD 기능의 메소드가 메소드 이름만 다를 뿐 실제 EntityManager의 메소드를 호출하는 코드는 거의 동일

- 스프링 데이터 JPA 는 이러한 기본적인 코드까지도 제거해 준다.

- pom.xml 에 라이브러리를 추가하자.

```xml
		<!-- Spring Data JPA -->
		<dependency>
			<groupId>org.springframework.data</groupId>
			<artifactId>spring-data-jpa</artifactId>
			<version>${spring.data.jpa.version}</version>
		</dependency>
```

<br>

---

## 리포지터리 인터페이스 작성

- 스프링 데이터 JPA 에서 제공하는 인터페이스들의 계층 구조는 다음과 같다.

<br>

![spring-data-jpa-diagram](https://user-images.githubusercontent.com/50399804/121336751-ca19f000-c956-11eb-95f4-9d3dcf462869.png)

- 보통 CrudRepository, 페이징처리와 정렬 기능을 추가하고 싶은 경우 PagingAndSortingRepository 를 사용한다.

- 스프링 데이터 JPA에서 추가 기능을 사용하고 싶으면, JpaRepository 를 사용한다.

- 모든 인터페이스들은 공통적으로 두 개의 제네릭 타입을 지정하도록 되어있다.

```
CrudRepository<T, ID>

T : 엔티티 클래스 타입
ID : 식별자 타입(@ID로 매핑한 엔티티 클래스의 식별자 변수타입)
```

<br>

```java
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

}

-----------------------------------------------------------

public interface DepartmentRepository extends CrudRepository<Department, Long> {

}
```

- 스프링에서 제공하는 JPA DATA Repository 는 Repository 를 상속하여 새로운 인터페이스를 정의하기만 하면, 인터페이스에 대한 구현 객체는 스프링 컨테이너가 알아서 만들어 준다.

- 스프링 컨테이너가 만들어준 리포지터리 객체를 비즈니스 클랙스에서 사용하기만 하면 된다.

```java
@Service("empService")
@Transactional
public class EmployeeService {

	@Autowired
	private EmployeeRepository empRepository;

	public void insertEmployee(Employee employee) {
		empRepository.save(employee);
	}

	public void updateEmployee(Employee employee) {
		empRepository.save(employee);
	}

	public void deleteEmployee(Employee employee) {
		empRepository.delete(employee);
	}

	public Employee getEmployee(Employee employee) {
		return empRepository.findById(employee.getId()).get();
	}

	public List<Employee> getEmployeeList(Employee employee) {
		return (List<Employee>) empRepository.findAll();
	}
```

<br>

- 스프링 컨테이너가 데이터 JPA 관련 객체 및 리포지터리를 생성할 수 있도록 스프링 설정 클래스를 수정하자.

```java
@Configuration
@ComponentScan(basePackages = "com.rubypaper.biz")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.rubypaper.biz.repository",
					   entityManagerFactoryRef = "factoryBean",
					   transactionManagerRef = "txManager"
		) // 메소드 이름으로 등록, 아니면 @Bean name 으로 설정하자.
public class SpringConfiguration {

	@Bean
	public HibernateJpaVendorAdapter vendorAdaptor() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();

		return adapter;
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:tcp://localhost/~/test");
		dataSource.setUsername("sa");
		dataSource.setPassword("");

		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean factoryBean() {
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setJpaVendorAdapter(vendorAdaptor());
		factoryBean.setDataSource(dataSource());

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
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
