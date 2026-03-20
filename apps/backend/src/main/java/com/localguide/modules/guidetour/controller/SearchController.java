package com.localguide.modules.guidetour.controller;

import com.localguide.modules.guidetour.dto.GuideSummaryResponse;
import com.localguide.modules.guidetour.dto.PagedResponse;
import com.localguide.modules.guidetour.dto.TourResponse;
import com.localguide.modules.guidetour.service.SearchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/guides")
    public PagedResponse<GuideSummaryResponse> searchGuides(
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return searchService.searchGuides(province, city, date, language, category, minPrice, maxPrice, page, size);
    }

    @GetMapping("/tours")
    public PagedResponse<TourResponse> searchTours(
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return searchService.searchTours(province, city, date, language, category, minPrice, maxPrice, page, size);
    }
}
