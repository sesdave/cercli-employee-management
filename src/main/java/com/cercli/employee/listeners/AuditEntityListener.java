package com.cercli.employee.listeners;
import com.cercli.employee.event.EntityHistoryEvent;
import com.cercli.employee.entity.AuditableEntity;
import com.cercli.employee.util.DateUtil;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuditEntityListener {

    private final DateUtil dateUtil;

    private final ApplicationEventPublisher eventPublisher;

    @PrePersist
    public void onPrePersist(AuditableEntity entity) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        System.out.println("local time "+ currentDateTime + "server "+ dateUtil.toServerTime(currentDateTime));
        entity.setCreatedAt(dateUtil.toServerTime(currentDateTime));  // Set createdAt
        entity.setModifiedAt(entity.getCreatedAt());  // Initially set modifiedAt to createdAt
    }

    @PreUpdate
    public void onPreUpdate(AuditableEntity entity) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        System.out.println("local time "+ currentDateTime + "server "+ dateUtil.toServerTime(currentDateTime));
        entity.setModifiedAt(dateUtil.toServerTime(currentDateTime));  // Update modifiedAt
    }

    @PostPersist
    @PostUpdate
    public void logChange(AuditableEntity entity) {
        eventPublisher.publishEvent(new EntityHistoryEvent(this, entity, "UPDATED"));
        //historyFactory.createHistory(entity, "UPDATED");
    }
}
