package com.dkd.test;

import com.dkd.generator.util.VelocityInitializer;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.FileWriter;
import java.util.List;

public class VelocityDemoTest {

    public static void main(String[] args) throws Exception {
        //1. 初始化模板引擎
        VelocityInitializer.initVelocity();
        //2. 准备数据模型
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("message", "加油朋友！！");
        //创建区域对象
        Region region = new Region(1L, "成都");
        velocityContext.put("region", region);
        Region region2 = new Region(2L, "北京");
        List<Region> regionList = List.of(region, region2);
        velocityContext.put("regionList", regionList);
        //3. 读取模板
        Template template = Velocity.getTemplate("vm/index.html.vm", "UTF-8");
        //4. 渲染模板
        FileWriter fileWriter = new FileWriter("C:\\Users\\ASUS\\Desktop\\index.html");
        template.merge(velocityContext, fileWriter);
        fileWriter.close();
    }
}