package com.gymcn.api.controllers;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gymcn.models.dto.ApiResponse;
import com.gymcn.models.entity.Venue;
import com.gymcn.models.mapper.VenueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 场馆控制器
 */
@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VenueController {
    
    private final VenueMapper venueMapper;
    
    /**
     * 获取场馆列表
     */
    @GetMapping
    public ApiResponse<List<Venue>> getVenueList(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String type) {
        
        QueryWrapper<Venue> query = new QueryWrapper<>();
        query.eq("status", "active");
        
        if (city != null && !city.isEmpty()) {
            query.eq("city", city);
        }
        
        if (type != null && !type.isEmpty()) {
            query.eq("type", type);
        }
        
        query.orderByDesc("created_at");
        
        List<Venue> venues = venueMapper.selectList(query);
        return ApiResponse.success(venues);
    }
    
    /**
     * 获取场馆详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Venue> getVenueDetail(@PathVariable Long id) {
        Venue venue = venueMapper.selectById(id);
        if (venue == null) {
            return ApiResponse.error("场馆不存在");
        }
        return ApiResponse.success(venue);
    }
}
