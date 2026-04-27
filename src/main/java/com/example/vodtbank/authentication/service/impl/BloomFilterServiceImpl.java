package com.example.vodtbank.authentication.service.impl;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.vodtbank.authentication.repository.UserRepository;
import com.example.vodtbank.authentication.service.BloomFilterService;
import com.example.vodtbank.config.BloomFilterConfig;
import com.google.common.hash.BloomFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BloomFilterServiceImpl implements BloomFilterService {
	private static final int BATCH_SIZE = 1;
	private final Object lock = new Object();
	private final AtomicInteger counter = new AtomicInteger(0);
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final Log logger = LogFactory.getLog(getClass());

	private final BloomFilter<String> bloomFilter;
	private final UserRepository userRepository;

	public BloomFilterServiceImpl(BloomFilter<String> bloomFilter, UserRepository userRepository) {
		this.bloomFilter = bloomFilter;
		this.userRepository = userRepository;
	}

	@Override
	public void addEmail(String email) {
		bloomFilter.put(email);
		int currentCount = counter.incrementAndGet();
		if(currentCount >= BATCH_SIZE) {
			persistBloomFilter();
		}
	}

	@Scheduled(fixedRate = 60000)
	public void persistBloomFilter() {
		// Double-check locking to minimize synchronization overhead
		if(counter.get() < BATCH_SIZE) {
			return;
		}

		synchronized(lock) {
			if(counter.get() < BATCH_SIZE) {
				return;
			}

			counter.set(0);

			executor.submit(() -> {
				try(ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream(BloomFilterConfig.FILE_PATH))) {

					oos.writeObject(bloomFilter);

					counter.set(0);
				} catch(Exception e) {
					logger.error(e);
				}
			});
		}
	}

	@Override
	public boolean isEmailExisting(String email) {
		if(bloomFilter.mightContain(email)) {
			return userRepository.findByEmail(email).isPresent();
		}

		return false;
	}
}
