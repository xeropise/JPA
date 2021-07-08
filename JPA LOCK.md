## JPA에서 낙관적 락(Optimistic Lock) 과 비관적 락(Pessimistic Lock)

- JPA 에서는 2가지의 LOCK을 제공한다.

  - 낙관적 락(Optimistic Lock)

  - 비관적 락(Pessimistic Lock)

- JPA는 데이터베이스 트랜잭션 격리 수준을 READ COMMITTED로 가정한다. 더 높은 격리 수준이 고려되는 경우에는 낙관적 락과 비관적 락 중 하나를 사용하면 된다.

---

<br>

### 낙관적 락 (Optimistic Lock)

- 트랜잭션들이 충돌이 발생하지 않는다고 가정하는 방법의 락, 어플리케이션 레벨에서의 락, 잠금이라기 보다는 일종의 충돌감지

- 읽는(SELECT) 시점에 Lock을 사용하지 않아 데이터를 수정(UPDATE) 하는 시점에 다른 사용자에 의해 데이터가 변경되었는지 변경여부를 확인해야 한다.

- 트랜잭션을 커밋하기 전까지는 트랜잭션의 충돌 여부를 알 수 없다.

- 낙관적 락을 사용하기 위해서는 @Version 을 사용하거나 @Lock 어노테이션을 통해 명시적으로 적용할 수 있다. Version 번호로 엔티티 상태를 저장한다. 엔티티를 즉시 LOCK 하지 않는다

- 다른 트랜잭션에서 엔티티 상태를 변경하는 경우에 트랜잭션이 변경 중에 존재하는 Version 번호와 비교를 한다. Version 번호가 다른 경우에 엔티티는 변경될수 없다는 뜻이며, 실행중인 트랜잭션이 있다면 OptimisticLockException, ObjectOptimisticLockingFailureException 등을 발생시키며, 롤백된다.

- @Version 에 명시할 타입은 int, Integer, long, Long, short, Short, java.sql.Timestamp 등을 사용할 수 있다.

```java
@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long Id;

    @Version
    private int version;
}
```

- Spring Data JPA Repository 에서 커스텀 쿼리 메소드에 Lock 을 거려면 @Lock 어노테이션을 사용하면 된다.

```java
@Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
@Query("SELECT c FROM Customer c WHERE c.orgId = ?1")
public List<Customer> fetchCustomersByOrgId(Long orgId);

@Lock(LockModeType.PESSIMISTIC_READ)
public Optional<Customer> findById(Long customerId);
```

---

<br>

### 비관적 락 (Pessimistic Lock)

- 트랜잭션끼리 충돌이 발생하다고 가정하고 락을 거는 방법, 데이터베이스 트랜잭션 락에 의존하는 방법

- DB가 제공하는 락 기능을 사용한다. ( SELECT FOR UPDATE 와 같은 명시적 락 )

- 데이터를 수정하면 즉시 트랜잭션의 충돌을 알 수 있다. PessimisticLockException, PessimisticLockingFailureException 등의 에러를 발생 시킨다.

- PESSIMISTIC LOCK을 얻은 트랜잭션 LOCK을 풀기 전가지 다른 트랜잭션이 무한히 대기하게 되므로 timeout 을 설정해야 한다.

```java
Map<String, Object> properties = new HashMap<String, Object>();
// timeout for 10 seconds
properties.put("javax.persistence.lock.timeout", 10 * 1000);

Member member = em.find(Member.class, id, LockModeType.PESSIMISTIC_WRITE, properties);
```

```java
public interface memberRepository extends JpaRepository<Member, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Member m where m.id = :id")
    Board findByIdForUpdate(Long id);
}
```
