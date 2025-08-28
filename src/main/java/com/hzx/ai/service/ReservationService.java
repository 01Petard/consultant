package com.hzx.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzx.ai.pojo.Reservation;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 预约服务
 * @author hzx
 */
//@Service
//public class ReservationService {
//    @Autowired
//    private ReservationMapper reservationMapper;
//
//    // 1. 添加预约信息
//    public void insert(Reservation reservation) {
//        reservationMapper.insert(reservation);
//    }
//
//    // 2. 根据手机号查询预约信息
//    public Reservation findByPhone(String phone) {
//        return reservationMapper.findByPhone(phone);
//    }
//}

@Service
public class ReservationService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final String RESERVATION_KEY = "reservation";

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 1. 添加预约信息到 Redis 列表
    public void insert(Reservation reservation) {
        try {
            String json = objectMapper.writeValueAsString(reservation);
            stringRedisTemplate.opsForList().rightPush(RESERVATION_KEY, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化失败", e);
        }
    }

    // 2. 根据手机号查询预约信息
    public Reservation findByPhone(String phone) {
        // 获取列表中所有元素
        List<String> list = stringRedisTemplate.opsForList().range(RESERVATION_KEY, 0, -1);
        if (list == null) return null;

        for (String json : list) {
            try {
                Reservation reservation = objectMapper.readValue(json, Reservation.class);
                if (phone.equals(reservation.getPhone())) {
                    return reservation;
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 可选：获取全部预约信息
    public List<Reservation> findAll() {
        List<String> list = stringRedisTemplate.opsForList().range(RESERVATION_KEY, 0, -1);
        if (list == null) return Collections.emptyList();

        List<Reservation> result = new ArrayList<>();
        for (String json : list) {
            try {
                result.add(objectMapper.readValue(json, Reservation.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}

