package com.itheima.consultant.tools;

import com.itheima.consultant.pojo.Reservation;
import com.itheima.consultant.service.ReservationService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;

@Component
@CrossOrigin
public class ReservationTool {
    @Autowired
    private ReservationService reservationService;

    //1.工具方法: 添加预约信息
    @Tool("预约志愿填报服务")
    public void addReservation(
            @P("考生姓名，必填") String name,
            @P("考生性别，没有明说就不填") String gender,
            @P("考生手机号，必填") String phone,
            @P("预约沟通时间，如果不确定就填今天,格式为: yyyy-MM-dd'T'HH:mm") String communicationTime,
            @P("考生所在省份，没有明说就不填") String province,
            @P("考生预估分数，没有明说就不填") Integer estimatedScore
    ) {
        Reservation reservation = new Reservation(null, name, gender, phone, LocalDateTime.parse(communicationTime), province, estimatedScore);
        reservationService.insert(reservation);
    }

    //2.工具方法: 查询预约信息
    @Tool("根据考生手机号查询预约单")
    public Reservation findReservation(
            @P("考生手机号") String phone
    ) {
        return reservationService.findByPhone(phone);
    }

    //3.工具方法: 获取当前的时间
    @Tool("获取当前的时间")
    public String getCurrentTime() {
        return LocalDateTime.now().toString();
    }
}
