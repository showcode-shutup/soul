package org.dromara.soul.client.springmvc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The type Soul http config. [bins-comments:这个就是客户自己的项目里接入soul在yml要配置的项目]
 *
 * @author xiaoyu
 */
@Data
@ConfigurationProperties(prefix = "soul.http")
public class SoulHttpConfig {

    // [bins-comments: 这个是soul-admin的地址]
    private String adminUrl;

    private String zookeeperUrl;

    private String contextPath;

    private String appName;
}
