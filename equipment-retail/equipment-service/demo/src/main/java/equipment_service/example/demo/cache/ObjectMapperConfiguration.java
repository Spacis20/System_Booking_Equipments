package equipment_service.example.demo.cache;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;

@Configuration
public class ObjectMapperConfiguration {

    @Bean
    public JsonMapperBuilderCustomizer jsonCustomizer(){
        return b -> b.enable(SerializationFeature.INDENT_OUTPUT);
    };



}
