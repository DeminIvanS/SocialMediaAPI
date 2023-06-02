package example.controller;

import example.aop.InfoLogger;
import example.model.dto.response.StatisticResponse;
import example.service.StatisticService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@InfoLogger
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Tag(name = "statistics", description = "Interaction with statistics")
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping
    public StatisticResponse getStatistics() {
        return statisticService.getStatistics();
    }
}
