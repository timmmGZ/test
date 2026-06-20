package com.gymcn.models.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 场馆实体
 */
@Data
@TableName("venues")
public class Venue {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 场馆名称
     */
    private String name;
    
    /**
     * 场馆地址
     */
    private String address;
    
    /**
     * 所在城市
     */
    private String city;
    
    /**
     * 所在区域
     */
    private String district;
    
    /**
     * 经度
     */
    private Double longitude;
    
    /**
     * 纬度
     */
    private Double latitude;
    
    /**
     * 场馆类型：gym-健身房，swimming-游泳馆，yoga-瑜伽馆等
     */
    private String type;
    
    /**
     * 场馆封面图
     */
    private String coverImage;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 营业时间
     */
    private String businessHours;
    
    /**
     * 状态：active-营业中，inactive-休息中
     */
    private String status;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;
}
