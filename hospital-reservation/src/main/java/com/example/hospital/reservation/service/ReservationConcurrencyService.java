package com.example.hospital.reservation.service;

import com.example.hospital.reservation.exception.SlotAlreadyReservedException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
public class ReservationConcurrencyService {

	public <T> T withOptimisticRetry(int maxAttempts, RetryableWork<T> work) {
		int attempt = 0;
		while (true) {
			attempt++;
			try {
				return work.run();
			} catch (OptimisticLockingFailureException e) {
				if (attempt >= maxAttempts) {
					throw new SlotAlreadyReservedException("동시 예약으로 인해 실패했습니다. 다시 시도해 주세요.");
				}
			}
		}
	}

	@FunctionalInterface
	public interface RetryableWork<T> {
		T run();
	}
}