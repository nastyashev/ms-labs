import java.util.ArrayList;

public class BankModel extends Model {
    private double meanClientsNum = 0;
    private int totalSwitchedTasks = 0;

    public BankModel(Element... elements) {
        super(elements);
    }

    @Override
    public void printResult() {
        for (var element : elements) {
            if (element instanceof Process p) {
                System.out.println("----" + p.getName() + "----");
                System.out.println("Середнє завантаження = " + p.getWorkTime() / tCurr);
                System.out.println("Середня довжина черги = " + p.getMeanQueue() / tCurr);
                System.out.println("Середній інтервал = " + p.getMeanLeaveInterval());
            }
            if (element instanceof SwitchingProcess sp) {
                System.out.println("Зміни смуг = " + sp.getSwitchedTasks());
            }
        }
        System.out.println(("----Загальні показники----"));
        System.out.println("Середнє число клієнтів = " + meanClientsNum / tCurr);
        System.out.println("Середній час перебування в банку = " + getAverageTaskInSystemTime());
        System.out.println("Середній інтервал = " + getGlobalMeanLeaveInterval());
        System.out.println("Відсоток відмов = " + getTotalFailureProbability() * 100 + "%");
        System.out.println("Кількість змін смуг = " + totalSwitchedTasks);
    }

    @Override
    protected void doModelStatistics(double delta) {
        super.doModelStatistics(delta);
        for (var element : elements) {
            if (element instanceof Process p) {
                meanClientsNum += p.getQueueSize() * delta + p.getState() * delta;
            }
        }
    }

    private double getTotalFailureProbability() {
        double totalFailures = 0;
        double totalQuantity = 0;
        for (var element : elements) {
            if (element instanceof Process p) {
                totalFailures += p.getFailures();
                totalQuantity += p.getQuantity();
            }
            if (element instanceof SwitchingProcess sp) {
                totalSwitchedTasks += sp.getSwitchedTasks();
            }
        }
        return totalFailures / totalQuantity;
    }

    private double getAverageTaskInSystemTime() {
        var tasks = new ArrayList<Task>();
        for (var element : elements) {
            if (element instanceof Process p) {
                tasks.addAll(p.getUnprocessedJobs());
            }
            if (element instanceof Dispose d) {
                tasks.addAll(d.getProcessedJobs());
            }
        }
        double totalTaskInSystemTime = 0;
        for (var task : tasks) {
            totalTaskInSystemTime += task.getTimeOut() - task.getTimeIn();
        }
        return totalTaskInSystemTime / tasks.size();
    }

    private double getGlobalMeanLeaveInterval() {
        double totalLeaveInterval = 0;
        double totalQuantity = 0;
        for (var element : elements) {
            if (element instanceof Process p) {
                totalLeaveInterval += p.getMeanLeaveInterval() * p.getQuantity();
                totalQuantity += p.getQuantity();
            }
        }
        return totalLeaveInterval / totalQuantity;
    }
}