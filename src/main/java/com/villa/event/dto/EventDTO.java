package com.villa.event.dto;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 */
public class EventDTO {
    private Boolean hasEvent;
    private int value = 1;

    public EventDTO(Boolean hasEvent) {
        this.hasEvent = hasEvent;
    }

    public EventDTO(Boolean hasEvent, int value) {
        this.hasEvent = hasEvent;
        this.value = value;
    }

    public Boolean getHasEvent() {
        return hasEvent;
    }

    public void setHasEvent(Boolean hasEvent) {
        this.hasEvent = hasEvent;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
