package cs455.scaling.task;

public enum TaskType {
    VOID_TASK(0, "default"),
    READ_COMPUTE(1,"read-compute-hash"),
    WRITE(2, "write");

    private final int value;
    private final String taskName;

    TaskType(final int value, final String taskName) {
        this.value = value;
        this.taskName = taskName;
    }
}
