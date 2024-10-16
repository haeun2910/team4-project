package com.example.moveSmart;
import com.example.moveSmart.odsayApi.config.Client;
import com.example.moveSmart.odsayApi.config.RouteSearcher;
import com.example.moveSmart.odsayApi.entity.RemainingTimeInfoVo;
import com.example.moveSmart.odsayApi.entity.RemainingTimeResponse;
import com.example.moveSmart.odsayApi.entity.RouteSearchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ScheduleApplicationTests {
	@Mock
	private RestTemplate routeSearchClient;
	@InjectMocks
	private RouteSearcher routeSearcher;
	@InjectMocks
	private Client client;
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	@Test
	void contextLoads() {
	}
	@Test
	void testCalcRouteAverageTime() {
		RouteSearchRequest requestDto = new RouteSearchRequest(126.978, 37.566, 127.000, 37.570, 0);

		// Actual call to the API, ensure you use a valid API key
		double averageTime = routeSearcher.calcRouteAverageTime(requestDto);

		// Assert the response (you might not know the exact value, but you can check it's > 0)
		assertTrue(averageTime > 0);
	}
	@Test
	void testRemainingTimeResponse() {
		// Arrange
		Duration remainingTime = Duration.ofHours(2).plusMinutes(30); // 2 hours and 30 minutes
		int routeAverageTimeAsMins = 45;
		int totalReadyTimeAsMins = 15;
		LocalDateTime recentPlanArrivalAt = LocalDateTime.now().plusHours(3); // 3 hours from now

		RemainingTimeInfoVo remainingTimeInfoVo = new RemainingTimeInfoVo(
				remainingTime,
				routeAverageTimeAsMins,
				totalReadyTimeAsMins,
				recentPlanArrivalAt
		);

		// Act
		RemainingTimeResponse response = new RemainingTimeResponse(remainingTimeInfoVo);

		// Assert
		assertEquals(2, response.getRemainingTime().getHours()); // Check hours
		assertEquals(30, response.getRemainingTime().getMinutes()); // Check minutes
		assertEquals(routeAverageTimeAsMins, response.getRouteAverageTimeAsMins()); // Check route average time
		assertEquals(totalReadyTimeAsMins, response.getTotalReadyTimeAsMins()); // Check total ready time
		assertEquals(recentPlanArrivalAt, response.getRecentPlanArrivalAt()); // Check recent plan arrival time
	}
}
