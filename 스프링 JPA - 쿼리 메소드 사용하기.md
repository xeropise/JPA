## 쿼리 메소드 사용하기

- 복잡한 검색을 처리하기 위해서는 당연히 JPQL 을 이용해야 한다. 검색 쿼리가 단순하거나 개발자가 JPQL에 익숙하지 않은 경우, JPQL은 적합하지 않을 수 있다.

- 스프링에서는 메소드 이름에 기반하여 JPQL 쿼리를 생성하는 기능을 제공한다. 이를 쿼리 메소드라고 한다.

- find + 엔티티 이름 + By + 변수 이름 으로 작성할 수 있는데, 엔티티 이름은 생략할 수 있다. 엔티티 이름을 생략하면 리포지터리 인페이스에 선언된 타입 정보를 기준으로 자동으로 엔티티 이름이 적용된다.

- 쿼리 메소드의 리턴 타입으로는 List, Page, Slice 를 사용할 수 있으며 모두 컬렉션 타입이다. 가장 많이 사용하는 것은 List 와 Page 이다.

```java
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    List<Employee> findByName(String name);
}

----------------------------------------------------------------------

@Service("empService")
@Transactional
public class EmployeeService {
    @Autowired
    private EmployeeRepository empRepository;

    public List<Employee> getEmployeeList(Employee employee) {
        return (List<Employee>) empRepository.findByName(employee.getName());
    }
}
```

<br>

- 여러가지 유형이 있다.

| 키워드                  | 예                                |
| ----------------------- | --------------------------------- |
| And                     | findByLastnameAndFirstName        |
| Or                      | findByLastnameOrFirstName         |
| Between                 | findByStartDateBetween            |
| LessThan                | findByAgeLessThan                 |
| LessThanEqual           | findByAgeLessThanEqual            |
| GreaterThan             | findByAgeGraterThan               |
| GreaterThanEqual        | findByAgeGraterThanEqual          |
| After                   | findByStartDateAfter              |
| Before                  | findByStartDateBefore             |
| IsNull                  | findByAgeIsNull                   |
| IsNotNull, <br> NotNull | findByAge(Is)Null                 |
| Like                    | findByFirstnameLike               |
| Not Like                | findByFirstnameNotLike            |
| StartingWith            | findByFirstnameStartingWith       |
| EndingWith              | findByFirstnameEndingWith         |
| Containing              | findByFirstnameContaining         |
| OrderBy                 | findByAgeOrderByLastnameDesc      |
| Not                     | findByLastnameNot                 |
| In                      | findByAgeIn(Collection<Age> ages) |

<br>

- 여러 조건을 사용하는 경우 And Or 키워드를 사용하면 된다. 단, 변수가 2개여야 한다.

```java
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    List<Employee> findByNameContainingOrMailIdContaining(String name, String mailId);
}

----------------------------------------------------------------------

@Service("empService")
@Transactional
public class EmployeeService {
    @Autowired
    private EmployeeRepository empRepository;

    public List<Employee> getEmployeeList(Employee employee) {
        return (List<Employee>) empRepository.findByNameContainingOrMailIdContaining(employee.getName(), employee.getMailId());
    }
}
```

<br>

- 데이터를 정렬하는 경우에는 메소드 이름에 OrderBy + 변수이름 + Asc 혹은 Desc 를 추가하면 된다.

```java
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    List<Employee> findByMailIdContainingOrderByNameDesc(String mailId);
}

----------------------------------------------------------------------

@Service("empService")
@Transactional
public class EmployeeService {
    @Autowired
    private EmployeeRepository empRepository;

    public List<Employee> getEmployeeList(Employee employee) {
        return (List<Employee>) empRepository.findByMailIdContainingOrderByNameDesc(employee.getName());
    }
}
```

<br>

- 모든 쿼리 메소드는 마지막 패러미터로 페이징 처리를 위한 Pageable 과 정렬을 처리하는 Sort 인터페이스를 추가할 수 있다.

<br>

### 페이징 처리

```java
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    List<Employee> findByNameContaining(String name, Pageable paging);
}

----------------------------------------------------------------------

@Service("empService")
@Transactional
public class EmployeeService {
    @Autowired
    private EmployeeRepository empRepository;

    public List<Employee> getEmployeeList(Employee employee, int pageNumber) {
        Pageable paging = PageRequest.of(pageNumber - 1, 3); // index 0 부터 시작

        return(List<Employee>)empRepository.findByNameContaining(employee.getName(), paging);
    }
}
```

<br>

### 정렬처리

```java
    public List<Employee> getEmployeeList(Employee employee, int pageNumber) {
        //Pageable paging = PageRequest.of(pageNumber - 1, 3, Sort.Direction.DESC, "id"); // 단일 정렬

        Pageable paging = PageRequest.of(pageNumber - 1, 3,
                Sort.by(new Order(Direction.DESC, "mailId"), new Order(Direction.ASC "salary"))
        );
        return(List<Employee>)empRepository.findByNameContaining(employee.getName(), paging);
    }

```

## 검색 결과를 Page 타입으로 받기

- 특별한 경우에는 [Page 타입](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Page.html)으로 받아 처리해야할 수도 있다.

```java
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    Page<Employee> findByNameContaining(String name, Pageable paging);
}

----------------------------------------------------------------------

public Page<Employee> getEmployeeList(Employee employee, int pageNumber) {
    Pageable paging = PageRequest.of(pageNumber - 1, 3,
        Sort.by(new Order(Direction.DESC, "mailId"), new Order(Direction.ASC, "salary"))
    );

    return empRepository.findByNameContaining(employee.getName(), paging);
}
```

---

## @Query 사용하기

- 복잡한 쿼리를 써야하는 경우에는 결국은 JPQL 혹은 Native Query 를 사용해야 한다.

- 리포지터리 인터페이스에 직접 @Query 를 등록하여, 사용할 수 있다. @Query 로 등록한 SQL은 프로젝트가 로딩되는 시점에 파싱되어 처리되므로 SQL에 오류가 있는 경우 예외가 무조건 발생한다.

```java
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    // 위치로 바인딩
    @Query("SELECT emp FROM Employee emp WHERE emp.name like %?1%")
    List<Employee> findByJPQL(String name);

    // 파라미터 이름으로 바인딩
    @Query("SELECT emp FROM Employee emp WHERE emp.name like %:name% OR emp.mailId like %:mailId%")
    List<Employee> findByJPQL2(@Paran("name") String name,
                               @Param("mailId") String email);

    // 엔티티를 통째로 검색이 아닌 특정 변수만 조회하기
    @Query("SELECT emp.id, emp.name, emp.salary FROM Employee emp WHERE emp.name "
           +"like %:name% ORDER BY emp.id DESC")
    List<Object[]> findByJPQL(@Param("name") String name)
}
```

- 파라미터 이름으로 바인딩하는 경우, @Param 뒤에 선언된 매개변수 이름은 중요하지 않고, JPQL에서 사용한 파라미터 이름이 @Param 으로 선언한 파라미터 이름과 일치해야 한다.

- **네이티브 쿼리** 를 사용하는 경우, @Query 속성에 nativeQuery = true 를 추가해줘야 한다.

```java
    @Query("SELECT emp.id, emp.name, emp.salary FROM Employee emp WHERE emp.name "
           +"like %:name% ORDER BY emp.id DESC", nativeQuery = true)
    List<Object[]> findByJPQL(@Param("name") String name)
```

- @Query 를 사용하더라도 페이징 및 정렬 처리를 할 수 있다.

```java
    @Query("SELECT emp.id, emp.name, emp.salary FROM Employee emp WHERE emp.name "
           +"like %:name% ORDER BY emp.id DESC", nativeQuery = true)
    List<Object[]> findByJPQL(@Param("name") String name, Pageable paging);

----------------------------------------------------------------------

@Service("empService")
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeRepository empRepository;

    public List<Object[]> getEmployeeList(Employee employee) {
        Pageable paging = PageRequest.of(0, 3, Sort.Direction.DESC, "id");
        return empRepository.findByJPQL(employee.getName(), paging);
    }
}
```
