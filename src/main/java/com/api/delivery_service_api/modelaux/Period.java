package com.api.delivery_service_api.modelaux;

import java.util.Date;

public class Period {
    private Date startAt;
    private Date endAt;

    public Period() {
    }

    public Period(Date startAt, Date endAt) {
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Date getEndAt() {
        return endAt;
    }

    public void setEndAt(Date endAt) {
        this.endAt = endAt;
    }
    
    
}
