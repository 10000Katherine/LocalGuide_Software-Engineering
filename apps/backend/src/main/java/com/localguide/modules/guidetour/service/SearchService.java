package com.localguide.modules.guidetour.service;

import com.localguide.modules.guidetour.domain.AvailabilitySlot;
import com.localguide.modules.guidetour.domain.Guide;
import com.localguide.modules.guidetour.domain.Tour;
import com.localguide.modules.guidetour.dto.GuideSummaryResponse;
import com.localguide.modules.guidetour.dto.PagedResponse;
import com.localguide.modules.guidetour.dto.TourResponse;
import com.localguide.modules.guidetour.repository.GuideRepository;
import com.localguide.modules.guidetour.repository.TourRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class SearchService {

    private static final int DEFAULT_PAGE_SIZE = 12;
    private static final int MAX_PAGE_SIZE = 50;

    private final GuideRepository guideRepository;
    private final TourRepository tourRepository;

    public SearchService(GuideRepository guideRepository, TourRepository tourRepository) {
        this.guideRepository = guideRepository;
        this.tourRepository = tourRepository;
    }

    @Transactional(readOnly = true)
    public PagedResponse<GuideSummaryResponse> searchGuides(
            String province,
            String city,
            LocalDate date,
            String language,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer page,
            Integer size
    ) {
        Pageable pageable = buildPageable(page, size);

        Specification<Guide> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hasText(province)) {
                predicates.add(cb.like(cb.lower(root.get("province")), like(province)));
            }
            if (hasText(city)) {
                predicates.add(cb.like(cb.lower(root.get("city")), like(city)));
            }
            if (hasText(language)) {
                predicates.add(cb.like(cb.lower(root.get("languages")), like(language)));
            }
            if (date != null) {
                Join<Guide, AvailabilitySlot> availabilityJoin = root.join("availabilitySlots", JoinType.LEFT);
                query.distinct(true);
                predicates.add(cb.equal(availabilityJoin.get("slotDate"), date));
            }

            boolean hasTourFilters = hasText(category) || minPrice != null || maxPrice != null;
            if (hasTourFilters) {
                Join<Guide, Tour> tourJoin = root.join("tours", JoinType.LEFT);
                query.distinct(true);
                if (hasText(category)) {
                    predicates.add(cb.equal(cb.lower(tourJoin.get("category")), normalize(category)));
                }
                if (minPrice != null) {
                    predicates.add(cb.greaterThanOrEqualTo(tourJoin.get("price"), minPrice));
                }
                if (maxPrice != null) {
                    predicates.add(cb.lessThanOrEqualTo(tourJoin.get("price"), maxPrice));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<GuideSummaryResponse> results = guideRepository.findAll(spec, pageable)
                .map(GuideSummaryResponse::from);
        return PagedResponse.fromPage(results);
    }

    @Transactional(readOnly = true)
    public PagedResponse<TourResponse> searchTours(
            String province,
            String city,
            LocalDate date,
            String language,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer page,
            Integer size
    ) {
        Pageable pageable = buildPageable(page, size);

        Specification<Tour> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hasText(province)) {
                predicates.add(cb.like(cb.lower(root.get("province")), like(province)));
            }
            if (hasText(city)) {
                predicates.add(cb.like(cb.lower(root.get("city")), like(city)));
            }
            if (hasText(language)) {
                predicates.add(cb.like(cb.lower(root.get("language")), like(language)));
            }
            if (date != null) {
                Join<Tour, Guide> guideJoin = root.join("guide", JoinType.LEFT);
                Join<Guide, AvailabilitySlot> availabilityJoin = guideJoin.join("availabilitySlots", JoinType.LEFT);
                query.distinct(true);
                predicates.add(cb.equal(availabilityJoin.get("slotDate"), date));
            }
            if (hasText(category)) {
                predicates.add(cb.equal(cb.lower(root.get("category")), normalize(category)));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<TourResponse> results = tourRepository.findAll(spec, pageable)
                .map(TourResponse::from);
        return PagedResponse.fromPage(results);
    }

    @Transactional(readOnly = true)
    public PagedResponse<GuideSummaryResponse> filterByCity(String city, Integer page, Integer size) {
        return searchGuides(null, city, null, null, null, null, null, page, size);
    }

    private Pageable buildPageable(Integer page, Integer size) {
        int normalizedPage = page == null || page < 0 ? 0 : page;
        int normalizedSize = size == null || size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        return PageRequest.of(normalizedPage, normalizedSize, Sort.by(Sort.Direction.DESC, "id"));
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String like(String value) {
        return "%" + normalize(value) + "%";
    }
}
