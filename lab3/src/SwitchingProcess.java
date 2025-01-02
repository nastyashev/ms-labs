import java.util.ArrayList;
import java.util.Arrays;

public class SwitchingProcess extends Process {
    private final ArrayList<Process> neighbors = new ArrayList<>();
    private final int deltaToSwitch;
    private int switchedTasks = 0;

    public SwitchingProcess(String name, double delayMean, int channelsNum, int deltaToSwitch) {
        super(name, delayMean, channelsNum);
        this.deltaToSwitch = deltaToSwitch;
    }

    public SwitchingProcess(String name, double delayMean, double delayDev, int channelsNum, int deltaToSwitch) {
        super(name, delayMean, delayDev, channelsNum);
        this.deltaToSwitch = deltaToSwitch;
    }

    public void setNeighbors(Process... neighbors) {
        this.neighbors.addAll(Arrays.asList(neighbors));
    }

    @Override
    public void outAct() {
        trySwitchProcess();
        super.outAct();
        for (var neighbor : neighbors) {
            if (neighbor instanceof SwitchingProcess) {
                ((SwitchingProcess) neighbor).trySwitchProcess();
            }
        }
    }

    @Override
    public void printResult() {
        super.printResult();
        System.out.println("Зміна смуги" + switchedTasks);
    }

    public void trySwitchProcess() {
        for (var neighbor : neighbors) {
            while (this.getQueueSize() - neighbor.getQueueSize() >= deltaToSwitch) {
                var switchedTask = this.queue.pollLast();
                neighbor.inAct(switchedTask);
                switchedTasks++;
            }
        }
    }

    public int getSwitchedTasks() {
        return switchedTasks;
    }
}