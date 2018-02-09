package ch.aaap.harvestclient.impl.taskAssignment;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

import ch.aaap.harvestclient.HarvestTest;
import ch.aaap.harvestclient.api.TaskAssignmentsApi;
import ch.aaap.harvestclient.api.filter.TaskAssignmentFilter;
import ch.aaap.harvestclient.domain.Project;
import ch.aaap.harvestclient.domain.Task;
import ch.aaap.harvestclient.domain.TaskAssignment;
import ch.aaap.harvestclient.domain.param.TaskAssignmentCreationInfo;
import util.ExistingData;
import util.TestSetupUtil;

@HarvestTest
public class TaskAssignmentsApiListTest {

    private static final TaskAssignmentsApi taskAssignmentApi = TestSetupUtil.getAdminAccess().taskAssignments();

    private static final Logger log = LoggerFactory.getLogger(TaskAssignmentsApiListTest.class);

    private static final Project project = ExistingData.getInstance().getProject();
    private static final Task task = ExistingData.getInstance().getTask();

    private TaskAssignment taskAssignment;

    @AfterEach
    void afterEach() {
        if (taskAssignment != null) {
            taskAssignmentApi.delete(project, taskAssignment);
        }
    }

    @Test
    void list() {

        TaskAssignmentFilter filter = new TaskAssignmentFilter();
        List<TaskAssignment> taskAssignments = taskAssignmentApi.list(project, filter);

        assertThat(taskAssignments).isNotEmpty();
    }

    @Test
    void listByActive() {

        TaskAssignmentCreationInfo creationInfo = new TaskAssignmentCreationInfo(task);
        creationInfo.setActive(true);
        taskAssignment = taskAssignmentApi.create(project, creationInfo);

        TaskAssignmentFilter filter = new TaskAssignmentFilter();
        filter.setActive(true);

        List<TaskAssignment> taskAssignments = taskAssignmentApi.list(project, filter);

        assertThat(taskAssignments).usingFieldByFieldElementComparator().contains(taskAssignment);

    }

    @Test
    void listByUpdatedSince() {

        Instant creationTime = Instant.now().minusSeconds(1);
        TaskAssignmentCreationInfo creationInfo = new TaskAssignmentCreationInfo(task);
        taskAssignment = taskAssignmentApi.create(project, creationInfo);
        log.debug("time now: {}", Instant.now());
        log.debug("Creation time for taskAssignment: {}", taskAssignment.getCreatedAt());
        log.debug("Update time for taskAssignment: {}", taskAssignment.getUpdatedAt());

        TaskAssignmentFilter filter = new TaskAssignmentFilter();
        filter.setUpdatedSince(creationTime);

        List<TaskAssignment> taskAssignments = taskAssignmentApi.list(project, filter);

        assertThat(taskAssignments).usingFieldByFieldElementComparator().containsExactly(taskAssignment);

    }

}