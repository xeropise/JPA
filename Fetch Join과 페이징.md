### Fetch Join과 컬렉션 페이징

- Fetch Join 이란 SQL의 조인 종류가 아닌 JPQL 에서 제공하는 성능 최적화를 위한 기능으로 연관된 엔티티와 SQL 을 한 번에 함께 조회하는 기능을 말한다.

  

- Collection을 조회 (@OneToMany, @ManyToMany) 하는 경우 N+1 문제가 발생할 수 있으니 주의하여야 한다.

<br>

- JPA에서 컬렉션을 Fetch Join + Paging 조회하는 경우, 데이터가 많다고 하면 다음과 같은 경고 메시지를 출력한다.
  - 데이터를 모두 메모리로 읽은 후에, 메모리에서 페이징 처리를 하고 있는 것으로 성능 상 많은 문제점, 최악의 경우 장애가 된다.

```
HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!
```

> 위의 경고 메시지의 경우, 다음과 같이 프로퍼티 설정이 되어야 메시지가 나온다.
>
> hibernate.query.fail_on_pagination_over_collection_fetch: true



- 페이징이 안되는 이유는 엔티티가 A와 B(Many)가 있다고하면 사용자 입장에서는 A를 기준으로 페이징 하려고하지만, JPA 입장에서는 어떤 기준을 가지고 Paging 하는지 알 수 없으므로 컬렉션은 페이징이 동작하지 않는다.



- 위의 해결방법은 다음과 같다.

  - ManyToOne으로 바꿔 조회

    - 조회 쿼리를 날리는 경우, ManyToOne 관계에서 페이징을 조회하여 가져와서 조회하는 방법이다.

      

  - 컬렉션 지연로딩 조회

    - hibernate.default_batch_fetch_size와 @BatchSize를 이용하는 방법이다.

      - hibernate.default_batch_fetch_size 는 Global 로 동작을 설정

        

      - @BatchSize는 개별로 동작하도록 설정할 수 있다.

        

    - 이 옵션을 사용하는 경우, 컬렉션이나 프록시 객체를 한꺼번에 설정한 size만큼 IN쿼리로 조회한다.

      

    - 어떻게 동작하는지는 [여기](https://jojoldu.tistory.com/414)를 참조하자.



