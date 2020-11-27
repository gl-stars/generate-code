# 代码生成器[code-generator]

# 一、template模板中变量的来源

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019122608424625.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

模板中的这些变量都在utils包中定义的，有些变量是在配置文件 `generator.properties`中配置的。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191226084539520.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

这里需要注意：实体类中定义了一个表的对象，是专门用于存储表的相关信息。也定义了一个表字段的对象，专门存储表字段的相关信息的。而表中有一个集合 `private List<ColumnEntity> columns;` 这个是存储当前表的字段，不包括主键。而模板中的`pk`指的是 `TableEntity`对象中的 `pk`。

# 二、分页

`MybatisPlusAutoConfigure`将自动填充注入，并实例化分页拦截器。

- 分页

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020052922483731.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

直接实例化分页拦截器就搞定，不需要任何配置就可以。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200529225035931.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200529225208318.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

`MybatisPlusAutoConfigure`需要在 `META-INF/spring.factories`文件中指定加载，否则配置无效。

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020052922531640.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxODUzNDQ3,size_16,color_FFFFFF,t_70)

# 三、使用

## 3.1、查询所有表

```http
localhost:7300/generator/list
```

## 3.2、代码生成

```http
localhost:7300/generator/code?tables=make_an_appointment,order_form&package=com.central&moduleName=after&author=stars&display=true
```

<table>
      <tr>
          <td>参数</td>
          <td>默认值</td>
          <td>是否必须</td>
          <td>描述</td>
      </tr>
      <tr>
          <td>tables</td>
          <td></td>
          <td>是</td>
          <td>需要生成的表名称，多张使用英文逗号隔开。</td>
      </tr>
      <tr>
          <td>package</td>
          <td>com.central</td>
          <td>否</td>
          <td>生成代码的包名</td>
      </tr>
      <tr>
          <td>moduleName</td>
          <td>after</td>
          <td>否</td>
          <td>工程名，生成代码后，包名和工程名在一起组成完成的包名，例如：com.central.after</td>
      </tr>
      <tr>
          <td>author</td>
          <td>stars</td>
          <td>否</td>
          <td>作者名</td>
      </tr>
      <tr>
          <td>email</td>
          <td>2115g@sina.com</td>
          <td>否</td>
          <td>邮箱</td>
      </tr>
      <tr>
          <td>tablePrefix</td>
          <td>""</td>
          <td>否</td>
          <td>表前缀，默认生成实体类时，会过滤掉表的前缀。</td>
      </tr>
      <tr>
          <td>display</td>
          <td>true</td>
          <td>否</td>
          <td>是否显示@Mapper注解</td>
      </tr>
      <tr>
          <td>isFacade</td>
          <td>true</td>
          <td>否</td>
          <td>是否生成前端控制器</td>
      </tr>
    <tr>
        <td>IdType</td>
        <td>AUTO</td>
        <td>否</td>
        <td>主键策略<ul>
            <li>AUTO(数据库ID自增)</li>
            <li>INPUT(用户输入ID)</li>
            <li>ID_WORKER(全局唯一ID)</li>
            <li>UUID(全局唯一ID)</li>
            <li>NONE(该类型为未设置主键类型)</li>
            <li>ID_WORKER_STR(字符串全局唯一ID)</li>
            </ul></td>
    </tr>
</talbe>


在模板下面有一个 `Mapper.xml.vm`和 `Mapper-copy.xml.vm`文件，这两个文件的作用都是一样的，但是后者会更全面。为了考虑到今后可能会添加数据字段的问题，代码生成的不要太多，避免后期更改麻烦。所以建议使用前者，默认的是使用前者的。如果想要使用后者，将前者的删除，将后者的名称改为前者的文件名就OK。

`display`是否生成 `@Mapper`注解，考虑到有些代码可能是在父功能中使用，而其他功能通过jar的形式引入，那么`@Mapper`注解派不上用场，所以这个也是动态决定的。