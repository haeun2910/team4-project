package com.example.moveSmart;
import com.example.moveSmart.api.config.Client;
import com.example.moveSmart.api.config.RouteSearcher;
import com.example.moveSmart.api.entity.RemainingTimeInfoVo;
import com.example.moveSmart.api.entity.RemainingTimeResponse;
import com.example.moveSmart.api.entity.RouteSearchRequest;
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
}
