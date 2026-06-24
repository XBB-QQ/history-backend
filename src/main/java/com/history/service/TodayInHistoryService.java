package com.history.service;

import com.history.dto.EventDTO;

import java.util.List;

/**
 * 历史上的今天服务
 * 按月份和日期索引事件，返回当天发生的历史事件
 */
public interface TodayInHistoryService {

    /**
     * 获取指定日期（月-日）发生的事件列表
     * @param month 月份 1-12
     * @param day 日期 1-31
     */
    List<EventDTO> getEventsForDay(int month, int day);

    /**
     * 获取今天的 events（基于当前日期）
     */
    List<EventDTO> getTodayEvents();

    /**
     * 获取一个随机事件
     */
    EventDTO getRandomEvent();
}
