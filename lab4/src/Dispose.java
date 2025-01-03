import java.util.ArrayList;

public class Dispose extends Element {
    ArrayList<Task> processedTasks = new ArrayList<>();

    public Dispose(String name) {
        super(name);
    }

    @Override
    public void inAct(Task task) {
        super.inAct(task);
        processedTasks.add(task);
        super.outAct();
    }

    @Override
    public void printInfo() {
        System.out.println(getName() + "кількість = " + getQuantity());
    }

    public ArrayList<Task> getProcessedJobs() {
        return processedTasks;
    }
}