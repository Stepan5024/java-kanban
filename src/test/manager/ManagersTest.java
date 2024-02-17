package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import manager.Managers;

class ManagersTest {

    @Test
    void getDefault() {
        Assertions.assertNotNull(Managers.getDefault());
    }

    @Test
    void getDefaultHistory() {
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }
}