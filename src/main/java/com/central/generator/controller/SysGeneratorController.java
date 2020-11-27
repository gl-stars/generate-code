package com.central.generator.controller;

import com.alibaba.fastjson.JSON;
import com.central.generator.service.SysGeneratorService;
import com.centre.common.model.Result;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * @Author: GL
 */
@RestController
@RequestMapping("/generator")
public class SysGeneratorController {

    @Autowired
    private SysGeneratorService sysGeneratorService;

    /**
     * 列表
     */
    @ResponseBody
    @GetMapping("/list")
    public Result getTableList(@RequestParam Map<String, Object> params) {
        return sysGeneratorService.queryList(params);
    }
    /**
     * 生成代码FileUtil
     */
    @GetMapping("/code")
    public void makeCode(HttpServletResponse response,@RequestParam Map<String,String> basicMap) throws IOException {
        // 判断表名是否为空
        if (StringUtils.isBlank(MapUtils.getString(basicMap,"tables"))){
            IOUtils.write(new String(JSON.toJSONString(Result.<String>failed(new ArrayList<>(),"Fatal error: table name cannot be empty。"))).getBytes(), response.getOutputStream());
            return;
        }
        //将字符串以逗号分隔成字符串数组
        byte[] data = sysGeneratorService.generatorCode(MapUtils.getString(basicMap,"tables").split(","),basicMap);
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"generator.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }
}
