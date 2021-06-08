# JPQL

- SQL은 관계형 데이터베이스를 조작할 때 사용하는 표준 언어 데이터베에스에 따라 지원 하는 함수나 구문이 다르다.

- JPQL 은 데이터베이스와 무관하게 데이터베이스 관련 작업을 처리하는 것을 목표로 한다.

- SQL은 관계형 데이터베이스에 전달되어 직접 데이터를 조작 VS JPQL 은 영속 컨테이너에 전달되어 영속 컨테이너에 등록된 엔티티를 조작

---

## JPQL 사용 시 주의사항

- JPQL은 검색 대상이 데이터베이스의 테이블이 아닌 엔티티 객체, FROM 절에 테이블 이름이 아닌 검색할 엔티티 이름을 사용해야 한다.

- 엔티티 이름은 @Entity의 name 속성으로 지정함 이름, 명시하지 않는 경우 클래스 이름을 엔티티 이름으로 자동으로 설정 한다.

```java
@Entity(name = "Emp")
@Table(name = "S_EMP")
public class Employee {
        ...

-----------------------------

String jpql = "SELECT e FROM Emp AS e";

```

- JPQL 은 SELECT 절 생략이 가능하다. 생략 하는 경우 자동으로 해당 엔티티를 통째로 검색한다는 의미가 된다.

```java
@Entity
@Table(name = "S_EMP")
public class Employee {
        ...

-----------------------------

String jpql = "FROM Employee";

```

- JPQL을 사용할 때 특정 변수 몇 개만 검색하는 경우에는 변수의 이름을 명확하게 지정해야 한다. 변수 몇 개만 선택적으로 검색하는 경우는 검색 결과를 특정 엔티티로 매핑해서 받을 수 없으므로 검색 결과를 배열(Object[])로 받아야 한다.

- 배열 객체 각 인덱스에 SELECT에 나열된 변수 값들이 저장되어 있다.

- 타입을 명확하게 지정하면 TypedQuery 아니면 Query 를 선택하면 된다.

```java
Query query = em.createQuery(jpql);
List<Object[]> resultList = query.getResultList();
```

- List<Object[]> 로 리턴받아 각 배열의 인덱스를 통해 검색 결과를 사용하는데, 이렇게 하면 가독성도 떨어지고 검색 결과를 사용할 때 불편하다. 객체 생성 연산자 new 를 이용하여 검색 결과를 특정 객체에 매핑하여 처리할 수 있도록 지원한다.

- NEW를 사용할 때의 주의사항은 클래스의 이름이 반드시 패키지 경로가 포함된 전체 경로를 지정해야 한다.

```java
@Data
@AllArgsConstructor
public class EmployeeSalaryData {

    private Long id;

    private Double salary;

    private Double commissionPct;

}


---------------------------

String jpql = "SELECT NEW com.rubypaper.biz.domain.EmployeeSalaryData(id, salary, commissionPct) FROM Employee";

TypedQuery<EmployeeSalaryData> query = em.createQuery(jpql, EmployeeSalaryData.class);
```

<br>

---

## 파라미터 바인딩

- JPQL도 JDBC와 마찬가지로 사용자가 입력한 값을 JPQL 에 바인딩하여 영속 컨테이너에 전달할 수 있다.

- 두 가지 방법을 통해 입력 값을 바인딩할 수 있다.

  - 파라미터에 번호를 지정하여 바인딩

    ```java
    String jpql = "SELECT id, name, title, dpetName, salary "
                 +"FROM Employee WHERE id = ?1 AND name = ?2";

    Query query = em.createQuery(jpql);
    query.setParameter(1, 1L);
    query.setParameter(2, "직원 1");

    Object[] result = (Object[])query.getSingleResult();
    ```

  - 파라미터에 이름을 지정하여 바인딩

    ```java
    String jpql = "SELECT id, name, title, deptName, salary "
                 +"FROM Employee WHERE id = :employeeId AND name = :employeeName";

    Query query = em.createQuery(jpql);
    query.setParameter("employeeId", 1L);
    query.setParameter("employeeName", "직원 1");

    Object[] result = (Object[])query.getSingleResult();
    ```

---

## 상세 조회와 엔티티 캐시 (find와 createQuery 캐시 이용법 차이)

- find 메소드의 특징은 find 메소드로 검색한 엔티티가 영속 컨테이너가 관리하는 캐시에 등록된다. 동일한 엔티티에 대해 반복적으로 find가 실행되는 경우에 캐시에 등록된 엔티티를 재사용

- createQuery 메소드를 이용하면 캐시에 엔티티가 존재하는 것과 무관하게 반복적으로 SELECT를 수행

- 다만, 반복적인 SELECT 이후 컨테이너가 처리하는 작업은 createQuery 와 find 가 동일하다. (첫번째만 등록)

---

## 조인, 그룹핑, 정렬

- JPQL의 조인은 SQL의 조인과 개념도 다르고 적용 방법도 완전히 다르다.

- JPQL은 외래 키를 기반으로 조인을 처리하는 것이 아니라 연관관계를 기반으로 조인을 처리한다. 조인 쿼리 결과도 일반적인 데이터가 아닌 연관관계에 있는 엔티티 즉, 객체인 것이다.

- JPQL에서 사용하는 조인은 묵시적(Implicit) 조인, 명시적(Explicit) 조인이 있다.

  - 묵시적 조인 : JPQL에 조인에 대한 언급이 없는데도, 영속 컨테이너가 내부적으로 조인 처리

    - SELECT 시, 연관관계 매핑에 있는 엔티티 까지 조인 쿼리가 실행되지 않고 ,각 테이블에 대해서 별도의 쿼리가 실행될 수 있으니, 연관 엔티티를 반드시 언급할 것

  - 명시적 조인 : JPQL에 조인 관련 구문을 명시하는 것
    - 묵시적 조인을 명시적 조인으로 변경하려면 JPQL 에서 INNER JOIN 을 사용하여 직접 조인 조건을 기술하면 된다.

- 영속 컨테이너는 JPQLA 을 이용하여 객체를 검색할 때, 검색한 객체와 연관관계에 있는 객체도 같이 조회한다. 연관 객체가 JPQL에 언급되지 않으면 조인으로 처리하는 것이 아니라 개별적인 SELECT 로 처리한다.

- 영속 컨테이너가 해당 객체까지 동시에 가져오기 위해 묵시적 조인을 처리하려면 JPQL 에 반드시 연관관계에 있는 개체가 언급되어야 한다.

---

## 세타(THETA) 조인

- 데이터를 조회할 때 객체 사이의 연관성이 없는 경우, 객체가 가진 값을 기준으로 조인을 처리해야 하는데, WHERE 절에 일반 변수로 조인하는 것이다.

---

## 조인 페치

- 연관관계 매핑이 설정된 객체를 조회하면 묵시적 조인에 의해 연관관계에 있는 객체도 같이 조회된다.

- JPQL에서 연관 객체를 언급하지 않는 경우에는 묵시적 조인이 자동으로 실행되지 않는다.

- 이 경우, 조인 페치를 이용하면 처음부터 조인 쿼리를 이용하여 연관관계에 있는 객체를 가져오기 때문에 검색을 효율적으로 처리할 수 있다.

```java
String jpql = "SELECT e FROM Employee e JOIN FETCH e.dept";
```

- 조인 조건을 만족하지 못하는 데이터 목록도 보고 싶으면 외부 조인을 결합하면 된다.

```java
String jpql = "SELECT e FROM Employee e LEFT JOIN FETCH e.dept";
```

---

## 그룹함수(GROUP BY, HAVING)

- 그룹 함수를 사용할 때 몇가지 주의사항이 있는데, null은 데이터가 없는 상태이므로 결과 데이터에 포함도지 않는다, 데이터가 없는 상태에서 그룹 함수를 적용하면 null 을 리턴하기 때문에 대체 값을 지정해야 한다.

```java
String jpql = "SELECT d.name, MAX(e.salary), MIN(e.salary), SUM(e.salary), COUNT(e.salary), AVG(e.salary) FROM Employee e JOIN e.dept d GROUP BY d.name
```

---

## 정렬

- 검색 결과를 정렬하려면 ORDER BY 절을 사용하며, 정렬 기준을 명시하지 않으면 기본적으로 오름차순(ASC)이 적용된다.

```java
String jpql = "SELECT e, e.dept FROM Employee e ORDER BY e.dept.name DESC, e.salary ASC";
```

---

## 페이징 처리

- 페이징 관련 쿼리가 데이터베이스마다 다르거나 오라클을 비롯한 몇몇 데이터베이스의 경우 복잡한 쿼리를 사용해야 한다.

- JPA는 페이징 처리를 영속 컨테이너가 담당하고, 개발자는 페이징 처리를 제외한 기본 쿼리 작성에 집중할 수 있도록 한다.

- 페이징 처리와 관련한 setFirstResult 메소드와 setMaxResults 메소드를 사용한다.

```java
Strign jpql = "SELECT e, e.dept FROM Employee e ORDER BY e.id";
TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
int pageNumber = 2;
int pageSize = 5;
int startNum = (pageNumber * pageSize) - pageSize;
query.setFirstREsult(startNum);
query.setMaxResults(pageSize);

-------------------------------------------------------------------

Hibernate:
    select
        ~ 생략 ~
    from
        S_EMP employee0_
    inner join
        S_DEPT department1_
            on employee0_.DEPT_ID=department1_.DEPT_ID
    order by
        employee0_.id limit ? offset ?
```

---

## 서브 쿼리 사용하기

- 일반적인 쿼리는 실제 테이블에 저장되어 있는 데이터를 대상으로 작성하지만 데이터를 검색하다 보면 테이블에 없는 데이터를 기반으로 쿼리를 작성해야 하는 경우도 있다.

```java
String jpql = "SELECT d FROM Department d "
             +"WHERE (SELECT COUNT(e) "
             +"       FROM Employee e "
             +"       WHERE d.id = e.dept) >= 3";
```

- 여러 번 쿼리를 실행하지 않고 처음부터 조인 쿼리를 실행하도록 하고 싶으면 조인 페치를 적용하면 된다.

```java
String jpql = "SELECT d FROM Department d JOIN FETCH d.employee"
             +"WHERE (SELECT COUNT(e) "
             +"       FROM Employee e "
             +"       WHERE d.id = e.dept) >= 3";
```

- 서브 쿼리를 사용 시 EXISTS, IN, ANY, ALL 같은 함수를 같이 사용할 수 있다.

---

## 연산자와 함수 사용하기

- SQL과 똑같이 사용할 수 있다. 차이점은 없다.

- JPA는 일대다, 다대다 연관관계를 매핑할 떄 컬렉션을 사용한다. JPQL은 이와 관련된 컬렉션 연산자나 함수를 제공한다.

| 연산자            | 설명                                             | 사용 예                                  |
| ----------------- | ------------------------------------------------ | ---------------------------------------- |
| is [not] empty    | 컬렉션이 비어 있는지 확인한다.                   | WHERE d.employeeList is empty            |
| [not] member [of] | 특정 엔티티가 컬렉션에 포함되어 있는지 확인한다. | WHERE :employee member of d.employeeList |

<br>

```java
String jpql = "SELECT d FROM Department d WHERE d.employeeList is empty";
```

<br>

| 함수               | 기능                                         |
| ------------------ | -------------------------------------------- |
| SIZE(컬렉션)       | 컬렉션에 들어있는 객체의 개수를 구한다.      |
| INDEX(컬렉션 별칭) | 컬렉션에서 특정 객체의 인덱스 정보를 구한다. |

<br>

```java
String jpql = "SELECT d FROM Department d WHERE SIZE(d.employeeList) >= 3";
```

---

## 이름이 부여된(Named) 쿼리, 네이티브(Native) 쿼리 사용하기

- 데이터가 필요한 메소드에서 직접 JPQL, 네이티브 쿼리를 작성하는 경우, 쿼리가 한곳에 모여 있지 않아 수정할 쿼리를 찾는 것부터가 쉽지 않다.

- 특정 엔티티와 관련된 쿼리를 해당 엔티티 클래스에 일괄적으로 등록하고 사용할 수 있도록 지원한다. (@NamedQueires, @NamedNativeQueries 사용)

- 위의 어노테이션으로 등록한 JPQL 은 애플리케이션이 로딩되는 시점에 처리되므로, 쿼리에 문제가 있는 경우 애플리케이션 자체가 로딩되지 않으니 주의하자.

- 혹은 외부의 XML 파일에 분리시켜서 관리할 수 있다.

<br>

### 이름이 부여된 쿼리 사용하기

- @NamedQuery 는 엔티티 클래스라면 어디에 작성하든 상관은 없지만, 관리 상 쿼리와 관련된 클래스에 등록하는 것이 좋다.

```java
@Entity
@Table(name = "S_EMP")
@NamedQueries({
    @NamedQuery(name = "Employee.searchById",
                query = "SELECT e FROM Employee e WHERE e.id = :searchKeyword"),
    @NamedQuery(name = "Employee.searchByName",
                query = "SELECT e FROM Employee e WHERE e.name like :searchKeyword")
})
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    ... 생략
```

- src/main/resources/jpql/EntityMap_employee.xml 에 따로 XML로 관리할 수 있다.

```XML
<?xml version="1.0" encoding="UTF-8"?>
<entity-mappings
	xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence/orm http://xmlns.jcp.org/xml/ns/persistence/orm_2_1.xsd"
	version="2.1">

	<named-query name="Employee.searchById">
		<query>
			<![CDATA[
			SELECT e
			FROM Employee e
			WHERE e.id = :searchKeyword
			]]>
		</query>
	</named-query>

	<named-query name="Employee.searchByName">
		<query>
			<![CDATA[
			SELECT e
			FROM Employee e
			WHERE e.name like :searchKeyword
			]]>
		</query>
	</named-query>
</entity-mappings>
```

<br>

### 네이티브 쿼리 사용하기

- 특정 데이터베이스에서만 동작하는 쿼리를 말하는데 JPA는 네이티브 쿼리를 통해 특정 데이터베이스에서만 동작하는 함수와 힌트를 사용할 수 있다.

- 성능상의 문제를 해결하기 위해 사용한다.

```java
String sql = "SELECT * FROM S_EMP "
            +"WHERE DEPT_ID = :deptId " +
            +"ORDER BY SALARY DESC";
Query query = em.createNativeQuery(sql, Employee.class);
```

- 정상적인 SQL을 사용하며, createQuery 가 아닌, createNativeQuery 메소드를 사용했다는 점만 다르다.

```java
@Entity
@Table(name = "S_EMP")
@NamedNativeQueries({
    @NamedNativeQuery(name = "Employee.searchById",
                query = "SELECT E.ID, E.NAME ename, E.SALARY, D.NAME dname "
                       +"FROM S_EMP E, S_DEPT D "...
                )
})
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    ... 생략
```

- 네이티브 쿼리를 사용하기 위한 별도의 메소드를 제공하지는 않으므로 createNamedQuery 메소드를 똑같이 이용해서 실행할 수 있다.

- 네이티브 쿼리 역시 xml 파일로 별도 보관할 수 있다.

```xml
	<named-native-query name="Employee.searchByDeptId">
		<query>
			<![CDATA[
			SELECT E.ID, E.NAME ename, E.SALARY, D.NAME dname
			FROM S_EMP E, S_DEPT D
			WHERE E.DEPT_ID = D.DEPT_ID
				AND E.DEPT_ID = :deptId
			]]>
		</query>
	</named-native-query>
```

- xml로 별도 저장하는 경우에는 persistence.xml 에 반드시 등록해야 한다.

```xml
	<!--  영속성 유닛 설정 -->
	<persistence-unit name="Chapter06">
		<!-- 엔티티 클래스 등록  -->
		<!-- <class>com.rubypaper.biz.domain.Employee</class> --> <!-- 클래스 패스에 등록된 엔티티 클래스는 JPA가 자동으로 인식하기 때문에 persistence.xml 파일에 엔티티 클래스를 반드시 등록해야 하는 것은 아니다. -->

		<!-- Named Query XML -->
		<mapping-file>jpql/EntityMap_employee.xml</mapping-file>

		<!-- JPA 프로퍼티 설정  -->
		<properties>
			<!-- 데이터 소스 관련 설정  -->
			<property name="javax.persistence.jdbc.driver" value="org.h2.Driver" />
			<property name="javax.persistence.jdbc.user" value="sa" />
			<property name="javax.persitence.jdbc.password" value="" />
			<property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test" />

			<!-- JPA 구현체 관련 설정  -->
			<property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect" /> <!-- H2Dialect 클래스로 설정하여 H2 데이터베이스에 최적화된 SQL을 생성 -->
			<property name="hibernate.show_sql" value="true" />	<!-- 실행된 sql을 보여 준다. -->
			<!-- <property name="hibernate.format_sql" value="true" --> <!-- 하이버네이트가 생성한 SQL을 출력할 때, 들여쓰기를 포함하여 보기 좋은 포맷으로 출력 -->
			<!-- <property name="hibernate.id.new_generator_mappings" value="false" --> <!-- JPA 스펙에 맞는 새로운 키 생성 전략을 사용하도록 한다. 기존 버전과 호환성을 고려하면 false  -->
			<property name="hibernate.hbm2ddl.auto" value="create" /> <!-- 엔티티 클래스와 매핑할 테이블과 관련된 설정, create 인 경우, 애플리케이션이 실행될 때마다 JPA가  매번 매핑된 테이블을 새롭게 생성한다. -->

		</properties>
	</persistence-unit>

</persistence>
```

<br>

### 수정 및 삭제 쿼리

- JPQL 은 검색뿐만 아니라 수정이나 삭제 작업도 가능하다.

- JPQL을 이용하여 데이터를 수정하거나 삭제하면 해당 작업이 데이터베이스에 바로 처리된다. JPQL 을 이용한 수정이나 삭제 작업은 영속 컨테이너 캐시에 저장된 엔티티에는 아무런 영향을 주지 않는다.

- JPQL을 이용하여 데이터를 수정하거나 삭제할 때는 수정이나 삭제할 데이터를 먼저 검색하지 말고, 수정이나 삭제를 먼저 처리한 후에 검색하는 것을 원칙으로 정하면 된다.

```java
private static void dataUpdate(EntityManagerFactory emf) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		// 엔티티를 조회하여 캐시에 등록
//		Employee findEmp = em.find(Employee.class, 3L);
//		System.out.println("수정 전 급여 : " + findEmp.getSalary());

		/*
		 * JPQL 을 이용하여 데이터를 수정, 삭제하는 경우에는 삭제할 데이터를
         * 먼저 검색하지 말고 수정이나 삭제를 먼저 처리한 후에 검색하는 것을 원칙으로 하자.
		 * JPQL 을 이용한 데이터 수정, 삭제는 캐시에 영향을 주지 않는다.
		 */

		Query query = em.createQuery("UPDATE Employee e "

								    + "SET e.salary=salary * 1.3 "
								    + "WHERE e.id=:empid");
		query.setParameter("empid", 3L);
		int updateCount = query.executeUpdate();

		String jpql = "SELECT e FROM Employee e WHERE e.id = 3L";
		query = em.createQuery(jpql);
		Employee employee = (Employee) query.getSingleResult();
		System.out.println(employee.getId() + " 번 직원의 수정된 급여 : " + employee.getSalary());
		em.getTransaction().commit();
		em.close();
}
```
