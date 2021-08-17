- 언뜻 보면 @EntityGraph 와 Fetch Join 이 N+1 문제를 해결하는 방법에 있어 동일한 동작을 수행하는 것처럼 보인다.

- 그럼 무조건 @EntityGraph를 쓰는게 좋지 않을까? 물론 세부적으로 쿼리를 날린다면 JPQL 이 좋겠지만..

- 둘의 동작 방식에는 차이가 있다.

- Fetch Join의 경우, 명시적으로 사용하지 않는다면 inner Join 을 하여 처리하는 반면 @EntityGraph 는 left outer join 을 기본으로 한다.
  - left outer join 이기 때문에 카디션 곱이 일어나 필요 이상의 데이터를 가져올 수 있으니 주의하자.
