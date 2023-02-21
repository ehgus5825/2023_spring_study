package hello.typeconverter.converter;

import hello.typeconverter.type.IpPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

// IpPort -> String

@Slf4j
public class IpPortToStringConverter implements Converter<IpPort, String > {

    @Override
    public String convert(IpPort source) {
        log.info("convert sourse={}", source);
        // IpPort 객체 -> "127.0.0.0:8080"
        return source.getIp() + ":" + source.getPort();
    }
}
