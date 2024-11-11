package com.cercli.employee.event;

import com.cercli.employee.entity.AuditableEntity;
import org.springframework.context.ApplicationEvent;

public class EntityHistoryEvent extends ApplicationEvent {

    private final AuditableEntity entity;
    private final String changeType;

    public EntityHistoryEvent(Object source, AuditableEntity entity, String changeType) {
        super(source);
        this.entity = entity;
        this.changeType = changeType;
    }

    public AuditableEntity getEntity() {
        return entity;
    }

    public String getChangeType() {
        return changeType;
    }
}
