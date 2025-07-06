create database  if  not exists volunteer;
use volunteer;
create table if not exists reservation
(
    id                 bigint  primary key auto_increment   not null comment '主键ID',
    name               varchar(50) not null comment '考生姓名',
    gender             varchar(2)  not null comment '考生性别',
    phone              varchar(20) not null comment '考生手机号',
    communication_time datetime    not null comment '沟通时间',
    province           varchar(32) not null comment '考生所处的省份',
    estimated_score    int         not null comment '考生预估分数'
)