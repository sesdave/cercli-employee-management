package com.cercli.employee.listeners;

import com.cercli.employee.event.EntityHistoryEvent;
import com.cercli.employee.factory.HistoryFactory;
import com.cercli.employee.entity.AuditableEntity;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EntityHistoryEventListener {

    private final HistoryFactory historyFactory;

    public EntityHistoryEventListener(HistoryFactory historyFactory) {
        this.historyFactory = historyFactory;
    }

    @EventListener
    public void handleEntityHistoryEvent(EntityHistoryEvent event) {
        AuditableEntity entity = event.getEntity();
        String changeType = event.getChangeType();
        historyFactory.createHistory(entity, changeType);
    }
}
