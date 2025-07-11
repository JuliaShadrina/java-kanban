package ru.yandex.servise;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void defaultManagerShouldBeInitialized() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);
    }

}