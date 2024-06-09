java를 사용하면서 숫자를 핸들링 할 일이 종종 있었지만 매번 구글에 검색하면서 일하기 번거롭기도 하고 다양한 접근 방식을 이번 기회에 정리해보고자 한다.

---

### String.format()를 사용한 기본 숫자 형식 설정

- 해당 방법은 java에서 **숫자를 문자로 나타낼 경우** 두 개의 인수를 사용한다. 첫 번째는 보고 싶은 소수 자리 패턴, 두 번째 인수는 주어진 값을 넣어준다.
    - %d (10진수 형식)
    - %s (문자열 형식)
    - %f (실수형 형식)
    - Locale 설정
    - %t (날짜시간 형식)
    - %c (유니코드 문자 형식)
    - %o, %x(8진수, 16진수 형식)

```java
double value = 4.2352989244d;
assertThat(String.format("%.2f", value)).isEqualTo("4.24");
assertThat(String.format("%.3f", value)).isEqualTo("4.235");
```

### **BigDecimal를 사용한 소수점 형식(반올림,올림,내림 등)**

```java
@Test
public void test(){
	double D = 4.2352989244d;
	assertThat(withBigDecimal(D, 2)).isEqualTo(4.24);
	assertThat(withBigDecimal(D, 3)).isEqualTo(4.235);
}

public double withBigDecimal(double value, int places) {
    BigDecimal bigDecimal = new BigDecimal(value);
    bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
    return bigDecimal.doubleValue();
}
```

위 코드와 같이 dobule형으로 원하는 자릿수를 반올림, 내림, 올림 등을 수행할 수 있다.

### **Math.round()**

Math 클래스를 이용해서 double 값을 소수점 자리를 관리할 수 있다. 아래 코드는 이해를 돕기 위해 *10^n 을* 곱하고 나중에 나누어 소수 자릿수를 조정한다.

```java
@Test
public void test(){
	assertThat(withMathRound(D, 2)).isEqualTo(4.24);
	assertThat(withMathRound(D, 3)).isEqualTo(4.235);
}

public double withMathRound(double value, int places) {
    double scale = Math.pow(10, places);
    return Math.round(value * scale) / scale;
}
```

**이 메서드는 조심해야할 필요가 있는게, 특정 경우에만 권장된다. 가끔씩 출력 값이 입력 값이 다를 수 있다. round() 메서드를 사용해서 값을 임의로 자르기 때문이다.**

```java

@Test
public void test(){
		System.out.println(withMathRound(1000.0d, 17));
		// Gives: 92.23372036854776 !!
		System.out.println(withMathRound(260.775d, 2));
		// Gives: 260.77 instead of expected 260.78
}

public double withMathRound(double value, int places) {
    double scale = Math.pow(10, places);
    return Math.round(value * scale) / scale;
}
```

### 통화, 큰 정수 또는 백분율과 같은 특정 유형에 대한 숫자 형식을 지정할 경우

### DecimalFormat 클래스를 사용한 정수 형식 설정

큰 정수가 있을 때마다 미리 정의된 패턴을 통해 숫자를 핸들링 할 수 있다. 아래는 정수의 쉼표 설정

```java
@Test
public void test(){
		int value = 123456789;
		assertThat(withLargeIntegers(value)).isEqualTo("123,456,789");
}

public String withLargeIntegers(double value) {
    DecimalFormat df = new DecimalFormat("###,###,###");
    return df.format(value);
}
```

소수점 뒤에 두 개의 0이 있는 숫자 서식 설정인 경우

```java
@Test
public void test(){
		int value = 12; 
		assertThat(withTwoDecimalPlaces(value)).isEqualTo(12.00);
}

public static double withTwoDecimalPlaces(double value) {
    DecimalFormat df = new DecimalFormat("#.00");
    return new Double(df.format(value));
}
```

### NumberFormat 클래스를 이용한 숫자 설정

일반적으로 double, float형인 숫자 값을 String.Format()을 이용하여 %f 등 숫자 형식으로 바뀌면 소수점 자리 뒤에 의미 없는 0이 더 붙게 된다. 이를 방지하기 위해 숫자 본연의 형식으로 하기 위해 NumberFormat 클래스를 사용해볼 수 있다. 또한 내부적으로 ‘0’으로 끝나는 소수점은 반환하지 않는다.

- 843.811 → 843.811000

```java
double number = 1234567.89;
String formattedNumber = NumberFormat.getInstance().format(number);
System.out.println("Formatted Number: " + formattedNumber);
// Gives: 1,234,567.89

String stringFormat = String.format("dddd : %f", number)
System.out.println("Formatted String: " + stringFormat);
// Gives: 1234567.890000
```

### NumberFormat 클래스를 **통화 숫자 형식**

해당 클래스를 이용해서 통화를 출력할 수 있다. 거기다 Locale클래스를 통해 지정한 국가에 맞는 형식으로 값을 뽑아낼 수 있다.

```java
@Test
public void test(){
		double value = 23_500;
		assertThat(currencyWithChosenLocalisation(value, new Locale("en", "US"))).isEqualTo("$23,500.00");
		assertThat(currencyWithChosenLocalisation(value, new Locale("zh", "CN"))).isEqualTo("¥23,500.00");
		assertThat(currencyWithChosenLocalisation(value, new Locale("pl", "PL"))).isEqualTo("23 500,00 zł");
}

public String currencyWithChosenLocalisation(double value, Locale locale) {
    NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
    return nf.format(value);
}
```

### NumberFormat 클래스를 서식 및 백분율

아래 메소드를 사용하면 지정한 국가에 맞는 형식으로 값을 나타낼 수 있다. 또 위 통화 출력과 마찬가지로 Locale을 통해 값을 뽑아낼 수 있다.

```java
@Test
public void test(){
		double value = 25f / 100f;
		assertThat(forPercentages(value, new Locale("en", "US"))).isEqualTo("25%");
}

public String forPercentages(double value, Locale locale) {
    NumberFormat nf = NumberFormat.getPercentInstance(locale);
    return nf.format(value);
}
```