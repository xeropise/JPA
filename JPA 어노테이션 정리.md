## 엔티티 관련

**1. @Entity**

- JPA는 Entity가 설정된 클래스로부터 생성된 객체만 엔티티로 인지하고 사용할 수 있다. name 속성으로 엔티티 이름을 지정할 수 있다.
- name 속성을 생략하면 클래스 이름이 엔티티 이름이 된다. 엔티티 이름이 중요한 이유는 JPQL 을 사용 시, 엔티티 이름을 사용하기 때문이다.

<br>

**2. @Table, @SecondaryTable**

- 엔티티와 매핑할 테이블을 지정한다.
- 기본적으로 엔티티 이름과 동일한 이름의 테이블이 매핑되지만 이름이 다른 경우, name 속성을 사용하여 매핑할 테이블을 지정해야 한다.

| 속성              | 설명                                                                                                |
| ----------------- | --------------------------------------------------------------------------------------------------- |
| name              | 매핑될 테이블 이름을 지정한다                                                                       |
| catalog           | 데이터베이스 카탈로그를 지정한다 (MySQL)                                                            |
| schema            | 데이터베이스 스키마를 지정한다 (schema)                                                             |
| uniqueConstraints | 결합 unique 제약조건을 지정하며, 여러 개의 칼럼이 결합되어 유일성을 보장해야 하는 경우 사용(복합키) |

- 하나의 엔티티에 다수의 테이블을 매핑하기 위해서는 @SecondaryTable 을 사용하면 된다.
- name 은 매핑할 다른테이블의 이름
- pkJoinColumns은 매핑할 다른테이블의 키속성을 나타낸다


```java
@Entity
@Table(name = "BOARD")
@SecondaryTable(name = "BOARD_DETAIL",
                pkJoinColumns = @PrimaryKeyJoinColumn(name = "BOARD_DETAL_ID")) // pkJoinColumns 를 사용하지 않으면 @Id 를 기본으로 가져간다.
@Getter
@Setter
public class Board {
    @Id
    @Column(name = "BOARD_ID")
    private String id;
    private String title;
    @Column(table = "BOARD_DETAIL",
            name = "BOARD_CONTENT")
    private String content;
}
```

<br>

**3. @Id, @MapsId**

- 테이블의 기본 키와 매핑되는 식별자 변수를 매핑
- 식별자 변수는 테이블의 기본 키(Primary Key)와 매핑되는 변수를 의미한다.

- @MapsId는 복합 키와 관련이 있는데 [여기](https://incheol-jung.gitbook.io/docs/study/jpa/7) 를 참조하자.
	- 외래 키와 매핑한 연관관계를 기본 키에도 매핑하겠다는 뜻이다. 속성값은 @EmbeddedId 를 사용한 필드명을 지정한다. [여기](https://steady-hello.tistory.com/106)를 참조하자. 
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

- DDD 를 적용할 때는 웬만해서는 사용하지 않는게 좋다.

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

**10. @Embeddable, @Embedded, @AttributeOverrides, @EmbeddedId**

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

- 식별자를 밸류타입으로 사용하려고 하는 경우, @Id 대신 @EmbeddedId 어노테이션을 사용해야 한다.
  - JPA 에서 식별자 타입은 Serializable 타입이어야 하므로, 인터페이스를 상속 받아야 한다.

```java
@Entity
@Table(name = "purchase_order")
public class Order {
	@Embeddedid
	private OrderNo number;
	...
}

@Embeddable
public class OrderNo implements Serializable {
	@Column(name="order_number")
	private String number;
	...
}
```

- 밸류타입으로 식별자를 구현할 때 얻을 수 있는 장점은 식별자에 기능을 추가할 수 있다는 점이다.

```java
@Embeddable
public class OrderNo implements Serializable {
	@Column(name = "order_number")
	private String number;

	public boolean is2ndGeneration() {
		return number.startsWith("N");
	}
	...
}
```

  <br>

**11. @Enumerated**

- 자바 엔티티 필드의 Enum 타입을 데이터베이스에 매핑할 때 어떻게 매핑할 것인지 정해주는 어노테이션이다.

| 속성 명          | 설명                     |
| ---------------- | ------------------------ |
| EnumType.ORDINAL | enum 순서 값을 DB에 저장 |
| EnumType.STRING  | enum 이름을 DB에 저장    |

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

<br>

**12. @Convert**

- int, long, String, LocalDate 와 같은 타입들은 DB 테이블에 한 개 칼럼과 매핑된다.

- 밸류타입들을 한 개 컬럼에 매핑해야 할 때 사용해야 한다. 에로 길이를 값과 단위로 가지고 있는 밸류 타입을 "1000mm" 로 저장해야 할 수도 있다.

- JPA 2.1 에 추가된 컨버터를 통해 밸류 타입과 컬럼데이터 간의 변환처리 기능을 지원해 준다.

```java
package shop.infra;

import shop.common.Money

import javax.persitence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoAppluy = true) // 모델에 출현하는 모든 Money 타입의 프로퍼티에 대해 MoneyConvert를 자동으로 적용
public class MoneyConverter implement AttributeConverter<Money, Integer> {

  	@Override
  	public Integer convertToDatabaseColumn(Money money) {
      if(money == null)
        	return null;
      else
        	return money.getValue();
    }

  	@Override
  	public Money convertToEntityAttribute(Integer value) {
      if(value == null) return null;
      else return new Money(value);
    }
}

/////////////////////////////////////////////////////

@Entity
@Table(name = "purchase_order")
public class Order {
  	...

    @Column(name = "total_amounts")
    private Money totalAmounts; 	// MoneyConverter 를 적용해서 값 변환

  	...
}

////////////////////////////////////////////////////////

// @Converter 의 autoApply 속성을 사용하지 않음 (기본)
import javax.persistence.Convert;

public class Order {

    @Column(name = "total_amounts")
    @Conver(converter = MoneyConverter.class)
    private Money totalAmounts;
}
```

<br>

**13. @Inheritance, @DiscriminatorColumn, @DiscriminatorValue, @DiscriminatorFormula**

- 객체는 상속관계가 존재하지만, 관게형 데이터베이스에는 상속 관계가 없다.
- 상속관계 매핑을 통해 객체의 상속 구조와 DB 의 슈퍼타입,서브타입 관계를 매핑한다.
- DB의 슈퍼타입 서브타입 논리 모델을 실제 물리모델로 구현 하는 방법은 3가지지만 JPA에서는 어떤 방식을 사용하든 매핑이 가능하다.
- 자식 테이블이 부모테이블의 키를 받아서 기본 키 + 외래 키로 사용하는 방법

- @Inheritance(strategy=InheritanceType.XXX)의 stategy를 설정해주면 된다.

  - InheritanceType 종류
    - JOINED
    - SINGLE_TABLE (default)
    - TABLE_PER_CLASS

- @DiscriminatorColumn(name="DTYPE")

  - 부모 클래스에 선언한다. 하위 클래스를 구분하는 용도의 컬럼이다. 관례는 default = DTYPE

- @DiscriminatorValue("XXX")
  - 하위 클래스에 선언한다. 엔티티를 저장할 때 슈퍼타입의 구분 컬럼에 저장할 값을 지정한다.
  - 어노테이션을 선언하지 않을 경우 기본값으로 클래스 이름이 들어간다.

- @DiscriminatorFormula
  - 해당 컬럼의 값으로 자식 클래스가 명확하게 구분되어질 때 사용한다. 
	```java
	@DiscriminatorFormula("case when deal_type = 'AIR' then 'AIR' else 'LODGE' end")
	```

- 관계에 대한 설명은 [여기](https://loosie.tistory.com/211)를 참조하자.
<br>

**14. @TypeDefs, @TypeDef, @Type**

- Package 나 Entity Class 레벨에 부여 가능한 어노테이션 \[ @TypeDefs, @TypeDef \]

- 커스텀 타입을 정의 가능하다. \[ @TypeDefs, @TypeDef \]

- [여기](https://www.baeldung.com/hibernate-custom-types)를 참조하자.

- 특정 자바 타입에 대해 하이버네이트 타입을 사용하도록 강제할 수 있다. [여기](https://kwonnam.pe.kr/wiki/java/hibernate/types)를 참조하자.

<br>

**15. @DynamicUpdate**

- JPA의 기본 동작은 변경되지 않은 컬럼도 update 쿼리에 포함

- 실제 값이 변경된 컬럼으로만 update 쿼리를 만드는 기능

- 자세한 것은 [여기](https://www.baeldung.com/spring-data-jpa-dynamicupdate)를 참조 하자.

- 팀장님께 리뷰 받으면서 왜 이 좋은 기능을 그럼 JPA에서 기본 값으로 쓰지 않을까요 라면서 설명해주셨는데 그 이유는 다음과 같다. 
	- 성능상 문제점이 발생할 수 있다. 각 컬럼을 다 비교해야 하는데 컬럼이 많은 경우에는 어떻게 될까?... 그렇다 ㅠㅠ

<br>

**16. @PrimaryKeyJoinColumn**

- FK이면서 동시에 PK로 id를 사용하는 테이블에 관해 정의하는 테이블

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

***

## 매핑 관계 설정

- 실제 프로젝트에서 쓰는것이 너무 많아 모두 정리는 어려우므로, 일단 [여기](https://data-make.tistory.com/613) 를 참조하자.

1. @JoinColumn

   - 외래 키를 매핑할 때 사용 한다. JoinColumn이 선언되어있다는 것은 외래키를 관리한다는 뜻으로 보면 된다.

  | 속성      | 설명                                | 기본값 |
  | --------- | ----------------------------------- | -----   |
  | name  | 매핑할 외래 키 이름 |  필드명 + _ + 참조하는 테이블의 기본 키 컬럼명		   |
  | referencedColumnName | 외래 키가 참조하는 대상 테이블의 컬럼명        | 참조하는 테이블의 기본키 컬럼명	    |		
  | foreignKey(DDL)	 | 외래 키 제약조건을 직접 지정 가능, 테이블을 생성할 때만 사용 				|
  | unique <br> nullable <br> insertable <br> updatable <br> columnDefinition <br> table <br>  |  @Column 속성과 같다
  
2. @OneToOne

   - 1대1 관계를 매핑할 때 사용한다. JPA에서는 단방향에 대해서는 지원하지 않는다.

   - 1대1 관계는 주 테이블이나 대상 테이블 둘 중 어느 곳이나 외래 키를 가질 수 있다. 1대1 관계는 반대쪽도 1대1 관계다.


3. @OneToMany

   - 엔티티를 하나 이상 참조할 수 있으므로 Collection, List, Set, Map 중에 하나를 사용해야 한다. 

   - 양방향 

	- 1대다 양방향 매핑은 존재하지 않는다. 대신 다대일 양방향 매핑을 사용해야 한다.

	- 성능 문제도 있지만 관리도 부담스럽다. 엔티티를 매핑한 테이블이 아닌 다른 테이블의 외래 키를 관리해야 하는 것이다. 
	
	- 완전히 불가능한 것은 아니지만, 1대다 단방향 매핑이 가지는 모든 단점을 그대로 가지고 있다.

  - 단방향

	- @JoinColumn 을 명시해야 한다. 그렇지 않으면 JPA는 연결 테이블을 중간에 두고 연관관계를 관리하는 조인 테이블(JoinTable) 전략을 기본으로 사용해서 매핑한다.
	
	- 매핑한 객체가 관리하는 외래 키가 다른 테이블에 있다 보니, 연관관계 처리를 위한 UPDATE SQL을 추가로 실행해야 한다. 
	
	- 1대다 단뱡향 매핑보다는 다대1 양방향 매핑을 사용하는 것이 좋다.

3. @ManyToOne

	- 외래 키가 항상 다쪽에 있다.

	- 양방향은 외래 키가 있는 쪽이 연관관계의 주인이므로, mappedBy 속성을 반대편에서 필드값으로 지정해줘야 한다.

	- 양방향 연관관계는 항상 서로를 참조해야 하므로, 편의 메소드를 작성시 한 곳에만 작성하거나 양쪽 다 작성할 수 있는데, 양쪽에 다 작성하면 무한루프에 빠지게되므로 주의해야 한다.

	```java
	public void setTeam(Team team) {
		this.team = team;
		
		if(!team.getMembers().contains(this)) {
			team.getMembers().add(this);
		}
	}
	
	public void addMember(Member member) {
		this.members.add(member);
		if (members.getTeam() != this) {
			members.setTeam(this)'
		}
	}
	```
	
	> 연관관계의 주인
	> JPA에서 두 객체 연관관계 중 하나를 정해서 데이터베이스에서 외래 키를 관리하는데 이것을 연관관계의 주인이라 한다.
	> 외래 키를 가진 테이블과 매핑한 엔티티가 외래키를 관리하는 게 효율적이므로 보통 이곳을 연관관계의 주인으로 선택한다.
	> 주인이 아닌 방향은 외래 키를 변경할 수 없고, 읽기만 가능하다.
	> 연관관계의 주인은 mappedBy 속성ㅇ르 사용하지 않는다. 연관관계의 주인이 아니면 mappedBy 속성을 사용하고, 연관관계의 주인 필드 이름을 값으로 입력해야 한다.
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
