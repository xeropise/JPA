- 회사 JPA 코드를 보던 중, JPA 와 관련이 된 Repository Interface 를 단 하나도 implements 하지 않았는데, impl Class를 자동 주입하는 메소드를 보았다.

- 뭐지? 책에서는 없던 내용인데 하고 검색해 보았더니 바로 [SPRING DATA JPA 커스텀 리포지터리](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.custom-implementations) 였다.

- 커스텀 리포지터리 인터페이스와 사용할 메소드를 먼저 선언한다.

```java
interface CustomizedUserRepository {
  void someCustomMethod(User user);
}
```

- 커스텀 리포지터리 인터페이스를 구현한 Impl 클래스를 통해 메소드를 정의 한다.

- 중요한점은 인터페이스명 + Impl(prefix) 가 정확해야 한다.

```java
class CustomizedUserRepositoryImpl implements CustomizedUserRepository {

  public void someCustomMethod(User user) {
    // Your custom implementation
  }
}
```

- 일반 JPA 리포지터리와 함께 다중상속으로 상속하면 된다.

```java
interface UserRepository extends CrudRepository<User, Long>, CustomizedUserRepository {

  // Declare query methods here
}
```
