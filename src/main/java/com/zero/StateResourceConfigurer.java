package com.zero;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 静态资源配置类
 * @auther Deram Zhao
 * @creatTime 2017/6/20
 */
@Configuration
public class StateResourceConfigurer extends WebMvcConfigurerAdapter{
    /**
     * 配置访问静态资源
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/bookpicture/**").addResourceLocations("classpath:/BookPicture/");
        super.addResourceHandlers(registry);
    }
}
