package ru.yandex.servise;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void defaultManagerShouldBeInitializedTest() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);
    }

}