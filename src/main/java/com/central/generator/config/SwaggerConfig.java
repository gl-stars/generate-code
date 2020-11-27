package com.central.generator.config;

import com.centre.common.config.BaseSwaggerConfig;
import com.centre.common.model.SwaggerDO;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger API文档相关配置
 * @author: stars
 * @data: 2020年 11月 27日 11:38
 */
@Component
@EnableSwagger2
@EnableKnife4j
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    public SwaggerDO swaggerProperties() {
        return SwaggerDO.builder()
                .apiBasePackage("com.central.generator.controller")
                .title("代码生成系统")
                .description("代码生成相关文档")
                .contactName("stars")
                .version("2.0.0")
                .enableSecurity(false)
                .build();
    }
}
