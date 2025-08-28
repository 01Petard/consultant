package com.hzx.ai.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 预约信息 实体类
 * @author hzx
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    private Long id;

    private String name;

    private String gender;

    private String phone;

    private String communicationTime;

    private String province;

    private Integer estimatedScore;
}
