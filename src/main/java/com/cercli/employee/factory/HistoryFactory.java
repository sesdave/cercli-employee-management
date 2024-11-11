package com.cercli.employee.factory;

import com.cercli.employee.entity.AuditableEntity;
import com.cercli.employee.entity.Employee;
import com.cercli.employee.entity.EmployeeHistory;
import com.cercli.employee.repository.EmployeeHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class HistoryFactory {

    private final EmployeeHistoryRepository historyRepository;
    private static final Logger logger = LoggerFactory.getLogger(HistoryFactory.class);

    public HistoryFactory(EmployeeHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /**
     * Creates a history record for an entity, currently supports Employee entities.
     * @param entity The entity for which the history is being created.
     * @param changeType The type of change (e.g., CREATED, UPDATED, DELETED).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T extends AuditableEntity> void createHistory(T entity, String changeType) {
        if (entity instanceof Employee) {
            // Handle Employee specific history creation
            Employee emp = (Employee) entity;
            EmployeeHistory history = EmployeeHistory.builder()
                    .employeeId(emp.getId())
                    .changeType(changeType)
                    .changes(emp.captureHistory())
                    .timestamp(LocalDateTime.now())
                    .build();

            logger.info("Creating history for Employee ID: {}, Change Type: {}", emp.getId(), changeType);

            try {
                historyRepository.save(history);
                logger.info("Successfully saved history for Employee ID: {}", emp.getId());
            } catch (Exception e) {
                logger.error("Error saving history for Employee ID: {}", emp.getId(), e);
                // If an error occurs, the entire transaction (including the entity save) will be rolled back.
                throw e;  // Rethrow the exception so that the transaction can be rolled back
            }
        } else {
            // Handle other types of AuditableEntity if needed
            logger.warn("Unsupported entity type for history creation: {}", entity.getClass().getName());
        }
    }
}
