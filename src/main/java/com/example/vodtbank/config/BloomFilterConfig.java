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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BloomFilterConfig {
	public static final String FILE_PATH = "emails_bloom_filter.ser";
	private final Log logger = LogFactory.getLog(getClass());

	@Bean
	public BloomFilter<String> bloomFilter() {
		if(Files.exists(Paths.get(FILE_PATH))) {
			try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
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
