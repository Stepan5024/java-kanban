package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import storage.history.InMemoryHistoryManager;

class ManagersTest {

    @Test
    void getDefault() {
        Assertions.assertNotNull(Managers.getDefault(new InMemoryHistoryManager()));
    }

    @Test
    void getDefaultHistory() {
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }
}