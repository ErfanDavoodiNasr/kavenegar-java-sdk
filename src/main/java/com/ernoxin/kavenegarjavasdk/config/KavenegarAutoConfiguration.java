package com.ernoxin.kavenegarjavasdk.config;

import com.ernoxin.kavenegarjavasdk.client.KavenegarClient;
import com.ernoxin.kavenegarjavasdk.http.KavenegarHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot auto-configuration for Kavenegar SDK beans.
 *
 * <p>Opt-in: set {@code kavenegar.enabled=true}.
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "kavenegar", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(KavenegarProperties.class)
public class KavenegarAutoConfiguration {
    /**
     * Creates validated config from properties.
     *
     * @param properties bound properties
     * @return config
     */
    @Bean
    @ConditionalOnMissingBean
    public KavenegarConfig kavenegarConfig(KavenegarProperties properties) {
        return properties.toConfig();
    }

    /**
     * Creates the default HTTP client.
     *
     * @param config config
     * @return HTTP client
     */
    @Bean
    @ConditionalOnMissingBean
    public KavenegarHttpClient kavenegarHttpClient(KavenegarConfig config) {
        return KavenegarHttpClient.create(config);
    }

    /**
     * Creates the high-level client.
     *
     * @param config     config
     * @param httpClient HTTP client
     * @return SDK client
     */
    @Bean
    @ConditionalOnMissingBean
    public KavenegarClient kavenegarClient(KavenegarConfig config, KavenegarHttpClient httpClient) {
        return new KavenegarClient(config, httpClient);
    }
}
