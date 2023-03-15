package account.services;

import account.entity.EventLog;
import account.repo.EventLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventLogService {

    private static EventLogRepository eventLogRepo;
    @Autowired
    public EventLogService(EventLogRepository eventLogRepo) {
        EventLogService.eventLogRepo = eventLogRepo;
    }

    public void saveEvent(EventLog event) {
        eventLogRepo.save(event);
    }

    public static void accessDeniedOrLoginFailedEvent(String path, String details ,String email) {
        EventLog event = new EventLog (LocalDate.now(), details, email, path, path);
        eventLogRepo.save(event);
    }

    public void bruteForceEvent(String path, String email) {
        EventLog event = new EventLog (LocalDate.now(), "BRUTE_FORCE", email, path, path);
        eventLogRepo.save(event);
    }

    public List<EventLog> accessLogs() {
        return eventLogRepo.findAll();
    }

}
