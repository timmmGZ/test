package com.gymcn.models.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("users")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 手机号（唯一标识）
     */
    private String phone;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 头像 URL
     */
    private String avatar;
    
    /**
     * 用户类型：personal-个人，enterprise-企业
     */
    private String userType;
    
    /**
     * 企业ID（如果是企业用户）
     */
    private Long enterpriseId;
    
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
     * 是否删除（逻辑删除）
     */
    @TableLogic
    private Integer deleted;
}
