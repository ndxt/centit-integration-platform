package com.centit.framework.ip.config;


import com.centit.framework.config.BaseSpringMvcConfig;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages ="com.centit.product.workgroup.controller",
    includeFilters = {@ComponentScan.Filter(value= org.springframework.stereotype.Controller.class)},
    useDefaultFilters = false)
public class WorkGroupSpringMvcConfig extends BaseSpringMvcConfig {
}
