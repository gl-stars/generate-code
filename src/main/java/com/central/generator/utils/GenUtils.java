package com.central.generator.utils;

import cn.hutool.core.date.DateUtil;
import com.central.generator.model.ColumnEntity;
import com.central.generator.model.TableEntity;
import com.centre.common.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器工具类
 *
 * @author ccp
 */
@Slf4j
public class GenUtils {
    private GenUtils() {
        throw new IllegalStateException("Utility class");
    }

    private final static String FILE_NAME_MODEL = "Model.java.vm";
    private final static String FILE_NAME_MAPPER = "Mapper.java.vm";
    private final static String FILE_NAME_MAPPERXML = "Mapper.xml.vm";
    private final static String FILE_NAME_SERVICE = "Service.java.vm";
    private final static String FILE_NAME_SERVICEIMPL = "ServiceImpl.java.vm";
    private final static String FILE_NAME_CONTROLLER = "Controller.java.vm";
    private final static String TEMPLATE_PATH = "template/";

    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<>();
        templates.add(TEMPLATE_PATH+FILE_NAME_MODEL);
        templates.add(TEMPLATE_PATH+FILE_NAME_MAPPER);
        templates.add(TEMPLATE_PATH+FILE_NAME_MAPPERXML);
        templates.add(TEMPLATE_PATH+FILE_NAME_SERVICE);
        templates.add(TEMPLATE_PATH+FILE_NAME_SERVICEIMPL);
        templates.add(TEMPLATE_PATH+FILE_NAME_CONTROLLER);
        return templates;
    }

    /**
     * 生成代码
     * <p>配置代码生成的相关信息，例如：包名，工程名，作者名，邮箱，表前缀等信息。这些信息需要访问时传入到basicMap集合中，不传入也有默认，详细信息如下。</p>
     * <talbe>
     *     <tr>
     *         <td>参数</td>
     *         <td>默认值</td>
     *         <td>是否必须</td>
     *         <td>描述</td>
     *     </tr>
     *     <tr>
     *         <td>package</td>
     *         <td>com.central</td>
     *         <td>否</td>
     *         <td>生成代码的包名</td>
     *     </tr>
     *     <tr>
     *         <td>moduleName</td>
     *         <td>after</td>
     *         <td>否</td>
     *         <td>工程名，生成代码后，包名和工程名在一起组成完成的包名，例如：com.central.after</td>
     *     </tr>
     *     <tr>
     *         <td>author</td>
     *         <td>stars</td>
     *         <td>否</td>
     *         <td>作者名</td>
     *     </tr>
     *     <tr>
     *         <td>email</td>
     *         <td>2115g@sina.com</td>
     *         <td>否</td>
     *         <td>邮箱</td>
     *     </tr>
     *     <tr>
     *         <td>tablePrefix</td>
     *         <td>""</td>
     *         <td>否</td>
     *         <td>表前缀，默认生成实体类时，会过滤掉表的前缀。</td>
     *     </tr>
     *     <tr>
     *         <td>display</td>
     *         <td>true</td>
     *         <td>否</td>
     *         <td>是否显示{@code @Mapper注解}</td>
     *     </tr>
     *     <tr>
     *         <td>isFacade</td>
     *         <td>true</td>
     *         <td>否</td>
     *         <td>是否需要前端控制器</td>
     *     </tr>
     *     <tr>
     *         <td>IdType</td>
     *         <td>AUTO</td>
     *         <td>否</td>
     *         <td>主键策略<ul>
     *             <li>AUTO(数据库ID自增)</li>
     *             <li>INPUT(用户输入ID)</li>
     *             <li>ID_WORKER(全局唯一ID)</li>
     *             <li>UUID(全局唯一ID)</li>
     *             <li>NONE(该类型为未设置主键类型)</li>
     *             <li>ID_WORKER_STR(字符串全局唯一ID)</li>
     *         </ul></td>
     *     </tr>
     * </talbe>
     * @param table 生成的表
     * @param columns 字段
     * @param zip
     * @param map 生成代码相关信息，例如：包名，作者名
     */
    public static void generatorCode(Map<String, String> table,
                                     List<Map<String, String>> columns, ZipOutputStream zip,Map<String,String> basicMap) {
        //配置信息
        Configuration config = getConfig();
        boolean hasBigDecimal = false;
        // 用于判断是否有时间类型
        boolean dateType = false;
        boolean fieldFill = false;
        //表信息
        TableEntity tableEntity = new TableEntity();
        tableEntity.setTableName(table.get("tableName"));
        tableEntity.setComments(table.get("tableComment"));
        //表名转换成Java类名，表前缀
        String className = tableToJava(tableEntity.getTableName(), MapUtils.getString(basicMap,"tablePrefix",""));
        tableEntity.setClassName(className);
        tableEntity.setClassname(StringUtils.uncapitalize(className));

        //列信息
        List<ColumnEntity> columsList = new ArrayList<>();
        for (Map<String, String> column : columns) {
            ColumnEntity columnEntity = new ColumnEntity();
            columnEntity.setColumnName(column.get("columnName"));
            columnEntity.setDataType(column.get("dataType"));
            columnEntity.setComments(column.get("columnComment"));
            columnEntity.setExtra(column.get("extra"));

            //列名转换成Java属性名
            String attrName = columnToJava(columnEntity.getColumnName());
            columnEntity.setAttrName(attrName);
            columnEntity.setAttrname(StringUtils.uncapitalize(attrName));

            //列的数据类型，转换成Java类型
            String attrType = config.getString(columnEntity.getDataType(), "unknowType");
            String columnName = columnEntity.getColumnName();
            columnEntity.setAttrType(attrType);
            if (!hasBigDecimal && "BigDecimal".equals(attrType)) {
                hasBigDecimal = true;
            }
            if (!dateType && "Date".equals(attrType)){
                dateType = true ;
            }
            if (!fieldFill && "create_time".equals(columnName) || "update_time".equals(columnName)){
                fieldFill = true ;
            }
            //是否主键
            if ("PRI".equalsIgnoreCase(column.get("columnKey")) && tableEntity.getPk() == null) {
                tableEntity.setPk(columnEntity);
            }

            columsList.add(columnEntity);
        }
        tableEntity.setColumns(columsList);

        //没主键，则第一个字段为主键
        if (tableEntity.getPk() == null) {
            tableEntity.setPk(tableEntity.getColumns().get(0));
        }

        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);
        String mainPath = config.getString("mainPath");
        mainPath = StringUtils.isBlank(mainPath) ? "io.renren" : mainPath;
        //封装模板数据
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", tableEntity.getTableName());
        map.put("comments", tableEntity.getComments());
        map.put("pk", tableEntity.getPk());
        // 这个是首字母小大写
        map.put("className", tableEntity.getClassName());
        // 这个是首字母小写
        map.put("classname", tableEntity.getClassname());
        // 实体类的命名规范
        map.put("classNameDO",tableEntity.getClassName()+"DO");
        map.put("pathName", tableEntity.getClassname().toLowerCase());
        map.put("columns", tableEntity.getColumns());
        map.put("hasBigDecimal", hasBigDecimal);
        map.put("dateType", dateType);
        map.put("fieldFill", fieldFill);
        map.put("mainPath", mainPath);
        // 包名
        map.put("package", MapUtils.getString(basicMap,"package","com.central"));
        // 工程名
        map.put("moduleName", MapUtils.getString(basicMap,"moduleName","after"));
        // 作者名
        map.put("author", MapUtils.getString(basicMap,"author","stars"));
        // 邮箱
        map.put("email", MapUtils.getString(basicMap,"email","2115g@sina.com"));
        map.put("datetime", DateUtil.format(new Date(), CommonConstant.DATETIME_FORMAT));
        // 是否显示@Mapper注解
        map.put("display",MapUtils.getBoolean(basicMap,"display",true));
        // 主键策略，去除前后空格
        map.put("IdType",MapUtils.getString(basicMap,"IdType","AUTO").trim());
        VelocityContext context = new VelocityContext(map);

        //获取模板列表
        List<String> templates = getTemplates();
        // 判断是否需要控制层
        if (!MapUtils.getBoolean(basicMap, "isFacade", true)) {
            // 判断集合中是否存在 前端控制器
            if (templates.contains(TEMPLATE_PATH + FILE_NAME_CONTROLLER)) {
                // 删除前端控制器
                templates.remove(TEMPLATE_PATH + FILE_NAME_CONTROLLER);
            }
        }
        for (String template : templates) {
            //渲染模板
            try (
                    StringWriter sw = new StringWriter()
            ) {
                Template tpl = Velocity.getTemplate(template, "UTF-8");
                tpl.merge(context, sw);

                //添加到zip
                zip.putNextEntry(new ZipEntry(getFileName(template, tableEntity.getClassName(), MapUtils.getString(basicMap,"package","com.central"), MapUtils.getString(basicMap,"moduleName","after"))));
                IOUtils.write(sw.toString(), zip, StandardCharsets.UTF_8);
                zip.closeEntry();
            } catch (IOException e) {
                log.error("generatorCode-error", e);
            }
        }
    }


    /**
     * 列名转换成Java属性名
     */
    public static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }

    /**
     * 表名转换成Java类名
     */
    public static String tableToJava(String tableName, String tablePrefix) {
        if (StringUtils.isNotBlank(tablePrefix)) {
            tableName = tableName.substring(tablePrefix.length());
        }
        return columnToJava(tableName);
    }

    /**
     * 获取配置信息
     */
    public static Configuration getConfig() {
        try {
            return new PropertiesConfiguration("generator.properties");
        } catch (ConfigurationException e) {
            throw new RuntimeException("获取配置文件失败，", e);
        }
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, String className, String packageName, String moduleName) {
        String packagePath = "main" + File.separator + "java" + File.separator;
        if (StringUtils.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator + moduleName + File.separator;
        }

        if (template.contains(FILE_NAME_MODEL)) {
            return packagePath + "model" + File.separator + className + "DO" + ".java";
        }

        if (template.contains(FILE_NAME_MAPPER)) {
            return packagePath + "mapper" + File.separator + className + "Mapper.java";
        }

        if (template.contains(FILE_NAME_SERVICE)) {
            return packagePath + "service" + File.separator + "I" + className + "Service.java";
        }

        if (template.contains(FILE_NAME_SERVICEIMPL)) {
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }

        if (template.contains(FILE_NAME_CONTROLLER)) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }

        if (template.contains(FILE_NAME_MAPPERXML)) {
            return "main" + File.separator + "resources" + File.separator  + "mapper" + File.separator + className + "Mapper.xml";
        }

        return null;
    }
}
