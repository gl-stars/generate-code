# knife4j-swagger接入

`GitHub`地址：[https://github.com/xiaoymin/swagger-bootstrap-ui](https://github.com/xiaoymin/swagger-bootstrap-ui)

官网：[https://xiaoym.gitee.io/knife4j/documentation/](https://xiaoym.gitee.io/knife4j/documentation/)

# 一、接入项目

## 1.1、公共配置

将`swagger`公共使用的东西拆分到`nm-common-core`模块中，避免每个模块都需要些重复的东西。

- 引入依赖

```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-spring-boot-starter</artifactId>
    <version>2.0.4</version>
</dependency>
```

只需要引入这个依赖即可，`springfox-swagger2`这些依赖都包含在内了，不需要再次引入。

- 定义自定义配置对象

```java
package com.centre.common.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Swagger自定义配置
 * Created by macro on 2020/7/16.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class SwaggerDO {
    /**
     * API文档生成基础路径
     */
    private String apiBasePackage;
    /**
     * 是否要启用登录认证
     */
    private boolean enableSecurity;
    /**
     * 文档标题
     */
    private String title;
    /**
     * 文档描述
     */
    private String description;
    /**
     * 文档版本
     */
    private String version;
    /**
     * 文档联系人姓名
     */
    private String contactName;
    /**
     * 文档联系人网址
     */
    private String contactUrl;
    /**
     * 文档联系人邮箱
     */
    private String contactEmail;
}
```

这个配置可以写也可以不用写，使用默认呗。

- `Swagger`基础配置

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201127153931929.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)



- 定义自定义配置类

这个类用于是否使用`swagger`，`swagger`我们只是在开发环境下使用，如果是线上环境是不需要使用的，如果想`swagger`应用到线上环境是非常危险。所以这里的定义是如果不做任何配置的情况下，默认是关闭`swagger`的，为了安全着想。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201127154334185.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)



## 1.2、在需要使用的模块下配置

### 创建配置文件

```java
package com.user.auth.config;

import com.centre.common.config.BaseSwaggerConfig;
import com.centre.common.model.SwaggerDO;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger API文档相关配置
 * @author: stars
 * @data: 2020年 11月 27日 11:38
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@ConditionalOnProperty(prefix = "nm.swagger",value = {"enable"},havingValue = "true")
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    public SwaggerDO swaggerProperties() {
        return SwaggerDO.builder()
                .apiBasePackage("com.user.auth.controller")
                .title("用户管理系统")
                .description("用户相关接口文档")
                .contactName("stars")
                .version("1.0")
                .enableSecurity(false)
                .build();
    }
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201127154646312.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)



**注意：`apiBasePackage`这里是API生成路径，需要指定前端控制器的包路径，否则不知道前端控制器在哪个地方。**

# 二、使用介绍

## `@ApiOperation`

当前接口的名称

```java
@ApiOperation("添加品牌")
// 或者
@ApiOperation(value = "添加品牌")
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201127155640395.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)



## `@ApiOperationSupport`

需要过滤掉的参数

过滤掉不用显示的参数，如果接口参数接收一个对象，但是这个对象里面可能存在一些当前接口不需要的参数，就可以使用这个注解过滤掉。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201127160028381.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

## `@ApiModelProperty`

这个参数一般都是使用在对象上面，指定这个对象的描述和是否必须。如果普通对象，只需要指定描述就可以了，如果这个对象是作为参数使用，那么可以指定描述和当前参数是否必须。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020112716040638.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)



`@Api(tags = "代码生成")`

使用在类明上

## 界面介绍

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201127172628944.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

