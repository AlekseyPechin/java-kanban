package managers;

import main.managers.InMemoryTaskManager;
import main.managers.Managers;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }
}