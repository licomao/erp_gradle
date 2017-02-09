package com.daqula.carmore;

import com.daqula.carmore.annotation.ApiJsonIgnore;
import com.daqula.carmore.util.BeanUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnClass({ ObjectMapper.class })
@AutoConfigureAfter({ JacksonAutoConfiguration.class })
public class ApiObjectMapperConfiguer {

    private final static AnnotationIntrospector DEFAULT_ANNOTATION_INTROSPECTOR = new ApiJacksonAnnotationIntrospector();

    @Autowired
    private ListableBeanFactory beanFactory;

    @PostConstruct
    private void changeAnnotationIntrospector() {
        for (ObjectMapper objectMapper : BeanUtil.getBeans(this.beanFactory, ObjectMapper.class)) {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            Object obj_1 = BeanUtil.getPrivateField(objectMapper, "_serializationConfig");
            Object obj_2 = BeanUtil.getPrivateField(obj_1, "_base");
            BeanUtil.setPrivateField(obj_2, "_annotationIntrospector", DEFAULT_ANNOTATION_INTROSPECTOR);
        }
    }

    public static class ApiJacksonAnnotationIntrospector extends JacksonAnnotationIntrospector {

        @Override
        protected boolean _isIgnorable(Annotated a) {
            ApiJsonIgnore ann = a.getAnnotation(ApiJsonIgnore.class);
            return (ann != null && ann.value());
        }
    }
}
