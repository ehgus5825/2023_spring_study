package hello.typeconverter.formatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.Formatter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

@Slf4j
public class MyNumberFormatter implements Formatter<Number> {

    // 문자를 객체로 변경 (문자 -> 숫자)
    @Override
    public Number parse(String text, Locale locale) throws ParseException {
        // "1,000" -> 1000
        log.info("text={}, locale={}", text, locale);
        // 자바의 기본 객체 locale 정보를 넣어서 나라별로 다른 숫자 포맷을 만듬 => parse()
        return NumberFormat.getInstance(locale).parse(text);
    }

    // 객체를 문자로 변경 (숫자 -> 문자)
    @Override
    public String print(Number object, Locale locale) {
        // 1000 -> "1,000"
        log.info("object={}, locale={}", object, locale);
        // 자바의 기본 객체 locale 정보를 넣어서 나라별로 다른 숫자 포맷을 만듬 => format()
        return NumberFormat.getInstance(locale).format(object);
    }
}
