package com.example.schedule.api.controller;

import com.example.schedule.api.service.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class BusController {
    private final BusService busService;

    @GetMapping("pub-trans-path")
    public String getPubTransPath(
            @RequestParam double startX,
            @RequestParam double startY,
            @RequestParam double endX,
            @RequestParam double endY) {

        try {
            return busService.searchPubTransPath(startX, startY, endX, endY);
        } catch (IOException e) {
            return "error: " + e.getMessage();
        }
    }
}
