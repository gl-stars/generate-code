package com.central.generator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.centre.common.model.Result;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author GL
 */
@Service
public interface SysGeneratorService extends IService {
     Result queryList(Map<String, Object> map);

     Map<String, String> queryTable(String tableName);

     List<Map<String, String>> queryColumns(String tableName);

     byte[] generatorCode(String[] tableNames,Map<String,String> basicMap);
}
