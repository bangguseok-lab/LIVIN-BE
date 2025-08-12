package org.livin.config;

import org.livin.risk.dto.RiskTemporaryDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	@Value("${redis.host}")
	private String host;

	@Value("${redis.port}")
	private int port;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(host, port);
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate() {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory());
		return template;
	}

	@Bean
	public RedisTemplate<String, RiskTemporaryDTO> riskTemporaryRedisTemplate(
		RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, RiskTemporaryDTO> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		template.setKeySerializer(new StringRedisSerializer());
		Jackson2JsonRedisSerializer<RiskTemporaryDTO> jsonSerializer = new Jackson2JsonRedisSerializer<>(
			RiskTemporaryDTO.class);
		template.setValueSerializer(jsonSerializer);

		return template;
	}
}
