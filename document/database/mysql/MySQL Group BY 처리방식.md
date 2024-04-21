일반적으로 `GROUP BY` 에 사용된 조건은 인덱스를 사용해서 처리될 수 없으므로 **`HAVING` 절을 튜닝하려고 인덱스를 생성하거나 다른 방법을 고민할 필요는 없다.**

`GROUP BY` 작업도 인덱스를 사용하는 경우와 그렇지 못한 경우로 나눠 볼 수 있다. 인덱스를 이용할 때는 인덱스를 차례대로 이용하는 인덱스 스캔 방법과 인덱스를 건너뛰면서 읽는 루스 인덱스 스캔이라는 방법으로 나뉜다. 그리고 **인덱스를 사용하지 못하는 쿼리에서 `GROUP BY` 작업은 임시 테이블을 사용한다.**

---

### 인덱스 스캔을 이용하는 GROUP BY(타이트 인덱스 스캔)

`ORDER BY` 의 경우와 마찬가지로 조인의 드라이빙 테이블에 속한 컬럼만 이용해 그룹핑할 때 `GROUP BY` 컬럼으로 이미 인덱스가 있다면 그 인덱스를 차례대로 읽으면서 그룹핑 작업을 수행하고 그 결과로 조인을 처리한다.

설령 `GROUP BY` 가 인덱스를 사용해서 처리된다 하더라도 그룹(Aggregation function) 함수 등의 그룹값을 처리해야 해서 임시 테이블이 필요할 수 있다.

`GROUP BY` 가 인덱스를 통해 처리되는 쿼리는 이미 정렬된 인덱스를 읽는 것이기 때문에 추가적인 정렬 작업은 필요하지 않다. → 그렇지 않다면, (Using index for group-by)이나 (Using temporary, Using filesort)가 표시된다.

### 루스(loose) 인덱스 스캔을 이용하는 GROUP BY

루스 인덱스 스캔 방식은 인덱스의 레코드를 건너뛰면서 필요한 부분만 탐색하는 것을 말한다.

```sql
EXPLAIN
SELECT emp_no
FROM salaries
WHERE from_date = '1985-03-01'
GROUP BY emp_no;
```

salaries 테이블에는 (emp+no + from_date)로 인덱스가 생성되어 있다고 가정해보자.

위 쿼리 문장에서 `WHERE` 조건은 인덱스 레인지 스캔 접근 방식으로 이용할 수 없는 쿼리이다. 하지만 이 쿼리의 실행 계획은 **다음과 같이 인덱스 레인지 스캔을 이용**했으며,  Extra 컬럼의 메세지를 보면 GROUP BY 처리까지 인덱스를 사용한 것을 알 수 있다.

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/12bf542b-5018-42ea-832d-feeb60ef9155/Untitled.png)

실행 계획을 하나씩 뜯어 보자.

`emp_no`가 PK로 `emp_no=10001` 값이 가장 먼저 정렬된 순서라고 가정해보면 쉽게 이해할 수 있다.

1. (`emp_no`, `from_date`) 인덱스를 순차적으로 스캔 하면서 `emp_no` 의 첫 번째 유일한 값(그룹 키) `"10001"`을 찾아낸다.
2. (`emp_no`, `from_date`) 인덱스에서 `emp_no` 가 `'10001'`인 것들 중 `from_date` 같이 `1985-03-01`인 레코드만 가져온다.
    - 이 방법은 위 1번 조건을 합쳐 `emp_no=10001 AND from_date='1985-03-01` 조건으로 (`emp_no`, `from_date`)인덱스를 검색하는 것과 유사하다.
3. (`emp_no`, `from_date`) 인덱스에서 `emp_no`의 그 다음 유니크한(그룹 키) 값을 가져온다.
4. 3번 단계에서 결과가 더 없으면 처리를 종료하고, 결과가 있다면 2번으로 돌아가서 반복 수행한다.

**MySQL 의 루스 인덱스 스캔 방식은 단일 테이블에 대해 수행되는 `GROUP BY` 처리에만 사용될 수 있다. → 그렇기 때문에 위 예제에서는 레인지 스캔이 동작한 것.**

**즉, 루스 인덱스 스캔은 분포도가 좋지 않은 인덱스 일수록 더 빠른 결과를 만들어낸다. 루스 인덱스 스캔으로 처리되는 쿼리에서는 별도 임시 테이블이 필요하지 않다.**

> 루스 인덱스 스캔이 사용 될 수 있을지 없을지 판단하는 것은 `WHERE` 절의 조건이나 ORDER BY 절이 인덱스를 사용할 수 있을지 없을지는 판단하기 어렵다.
>

루스 인덱스 스캔을 사용할 수 있는 쿼리 예시

```sql
SELECT col1, col2 FROM tb_test GROUP BY col1, col2;
SELECT DISTINCT col1, col2 FROM tb_test;
SELECT col1, MIN(col2) FROM tb_test GROUP BY col1;
SELECT col2 FROM tb_test WHERE col1 < const GROUP BY col1, col2;
SELECT col1, col2 FROM tb_test WHERE col3 = const GROUP BY col1, col2;
```

루스 인덱스 스캔을 사용할 수 없는 쿼리 예시

```sql
// MIN()과 MAX() 이외의 집합 함수가 사용되었기에 루스 인덱스 스캔 사용 불가
SELECT col1, SUM(col2) FROM tb_test GROUP BY col1;

// GROUP BY에 사용된 컬럼이 인덱스 구성 컬럼의 왼쪽부터 일치하지 않기 때문에 사용 불가
SELECT col1, col2 FROM tb_test GROUP BY col2, col3;

// SELECT 절의 컬럼이 GROUP BY와 일치하지 않기 때문에 사용 불가
SELECT col1, col3 FROM tb_test GROUP BY col1, col2;
```

### 임시 테이블을 사용하는 GROUP BY

`GROUP BY` 의 기준 컬럼이 드라이빙 테이블에 있든 드라이븐 테이블에 있든 관계없이 인덱스를 전혀 사용하지 못할 때 이 방식으로 처리된다.

```sql
EXPLAIN
SELECT e.last_name, AVG(s.salary)
FROM employees e, salaries s
WHERE s.emp_no=e.emp_no
GROUP BY e.last_name;
```

이 쿼리의 실행 계획에서는 Extra 컬럼에 ‘Using temporary’와 ‘Using filsort’ 메시지가 표시된다.

이 실행 계획에서 임시 테이블이 사용된 것은 employees 테이블을 풀 스캔하기 때문이 아니라 인덱스를 전혀 사용할 수 없는 `GROUP BY` 이기 때문이다.

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/00e330e7-51ab-4bec-8563-04c3107650c3/Untitled.png)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/c4208ea1-f20c-48bd-b05a-8f485cb16b9b/e5bfa8c3-f108-4721-b454-dbb1893a8183/Untitled.png)

실행 계획을 하나씩 뜯어 보자.

1. Employees 테이블을 풀 테이블 스캔 방식으로 읽는다.
2. 1번 단계에서 읽은 Employees 테이블의 emp_no 값을 이용해 salaries 테이블을 검색한다.
3. 2번 단계에서 얻은 조인 결과 레코드를 임시 테이블에 저장한다.
    1. 임시 테이블은 원본 쿼리에서 `GROUP BY` 절에 사용된 컬럼과 `SELECT` 하는 컬럼만 저장한다. 이 임시 테이블에서 중요한 것은 `GROUP BY` 절에 사용된 컬럼은 유니크 키를 생성한다는 점이다. **즉, `GROUP BY` 가 임시 테이블로 처리되는 경우 사용되는 임시 테이블은 항상 유니크 키를 가진다.**
4. 1~3단계를 조인이 완료 될 때까지 반복한다. 조인이 완료되면 임시 테이블의 유니크 키 순서대로 읽어서 클라이언트로 전송된다. **만약 `ORDER BY` 절에 명시된 컬럼과 `GROUP BY` 절에 명시된 컬럼이 같으면 별도의 정렬 작업을 수행하지 않는다. `ORDER BY` 절과 `GROUP BY` 절에 명시된 컬럼이 다르다면 Filesort 과정을 거치면서 다시 한번 작업을 수행한다.**