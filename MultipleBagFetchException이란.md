### MultipleBagFetchException

- 하이버네이트에서 [Bag](https://commons.apache.org/proper/commons-collections/javadocs/api-2.1.1/org/apache/commons/collections/Bag.html)이란?

  - 컬렉션과 유사한 중복된 Element를 들고 있을 수 자료구조, 하지만 순서를 보장하지 않는다.

  

  - 자바 컬렉션 프레임워크에는 Bag이 없어, List를 Bag으로 사용하고 있다.

    - List에 @OrderColumn 이나 @OrderBy 를 사용하는 경우에는, Bag과 구별하여 가져온다. (매우 중요)

      

    - @OrderColumn 은 잘 사용하지 않는데 다음과 같은 이유가 있다.

      - Column이 엔티티에서 직접 관리 되므로, 관련 엔티티를 가져오려는 경우 Update 쿼리가 날아간다.

        

      - 요소가 하나만 바뀌어도, 모든 위치 값이 변경된다. 순서 값을 보장하기 위함

        

      - 중간에 관련 컬럼값이 없으면 null 이 저장이 된다. 012 순서에서 023로 바꾸는 경우, 1의 위치에 null 값이 들어간 컬렉션이 반환된다.

        

- @OneToMany 관계인 엔티티에서 Bag으로 된(다시 한번 말하지만 List와는 순서와 있고 없고에서 확실하게 구분이 된다.) 엔티티를 2개 이상 가져오려고 하는 경우, 해당 에러가 발생한다.

  - 컬렉션을 조회 할 때, 무조건 2개 이상 페치 조인을 할  수 없는 것은 아니다. (반성...)

    

  - 하지만 그렇다고 해서, 문제가 없는 것은 아니다. 카티션 곱과 같은 문제가 발생하는 것은 피할 수 없다.

    

- 가장 이상적인 방법은, 두 엔티티를 분리해서 가져오는 것이다.

  - 이로 인해, MultipleBagFetchException과 카티션 곱을 모두 피할 수 있다.



> https://www.baeldung.com/java-hibernate-multiplebagfetchexception
>
> https://vladmihalcea.com/hibernate-multiplebagfetchexception/
>
> https://joont92.github.io/jpa/%EC%BB%AC%EB%A0%89%EC%85%98%EA%B3%BC-%EB%B6%80%EA%B0%80%EA%B8%B0%EB%8A%A5/
