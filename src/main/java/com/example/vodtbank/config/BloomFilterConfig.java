package com.example.vodtbank.config;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BloomFilterConfig {
	private final Log logger = LogFactory.getLog(getClass());

	@Value("${bloom-filter.file-path}")
	private String filePath;

	@Bean
	public BloomFilter<String> bloomFilter() {
		if(Files.exists(Paths.get(filePath))) {
			try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
				if(ois.readObject() instanceof BloomFilter bloomFilter) {
					return bloomFilter;
				}
			} catch(Exception e) {
				logger.error(e);
			}
		}
		// Create a new filter if the file doesn't exist or deserialization failed
		return BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), 1000000, 0.05);
	}
}
