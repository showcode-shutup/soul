package org.dromara.soul.client.springmvc.configuration;

import org.dromara.soul.client.springmvc.config.SoulHttpConfig;
import org.dromara.soul.client.springmvc.spring.ApplicationStartListener;
import org.dromara.soul.client.springmvc.spring.SoulClientBeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * The type Soul spring mvc auto configuration.[bins-comments:bean的配置类，这个类里会创建两个bean就是ApplicationStartListener和SoulClientBeanPostProcessor]
 *
 * @author xiaoyu
 */
@Configuration
@EnableConfigurationProperties(SoulHttpConfig.class)
public class SoulSpringMvcAutoConfiguration {

    private final Environment env;

    private final SoulHttpConfig soulHttpConfig;

    /**
     * Instantiates a new Soul spring mvc auto configuration.
     *
     * @param env            the env
     * @param soulHttpConfig the soul http config
     */
    public SoulSpringMvcAutoConfiguration(final Environment env, final SoulHttpConfig soulHttpConfig) {
        this.env = env;
        this.soulHttpConfig = soulHttpConfig;
    }

    /**
     * Soul client bean post processor soul client bean post processor.
     *
     * @return the soul client bean post processor
     */
    @Bean
    public SoulClientBeanPostProcessor soulClientBeanPostProcessor() {
        return new SoulClientBeanPostProcessor(env, soulHttpConfig);
    }

    /**
     * Application start listener application start listener.
     *
     * @return the application start listener
     */
    @Bean
    public ApplicationStartListener applicationStartListener() {
        return new ApplicationStartListener(soulHttpConfig);
    }
}
