import java.util.HashMap;

public class TypeModifyingProcess extends Process {

    private HashMap<Integer, Integer> typeModifyingMap;

    public TypeModifyingProcess(String name, double delayMean, int channelsNum) {
        super(name, delayMean, channelsNum);
    }

    public TypeModifyingProcess(String name, double delayMean, double delayDev, int channelsNum) {
        super(name, delayMean, delayDev, channelsNum);
    }

    public void setTypeModifyingMap(int[] types, int[] modifiedTypes) {
        this.typeModifyingMap = new HashMap<>();
        for (int i = 0; i < types.length; i++) {
            this.typeModifyingMap.put(types[i], modifiedTypes[i]);
        }
    }

    @Override
    protected void processCurrentTasks() {
        var channelsWithMinTNext = getChannelsWithMinTNext();
        for (var channel : channelsWithMinTNext) {
            var task = channel.getCurrentTask();
            var patient = (Patient) task;
            if (typeModifyingMap.get(patient.getType()) != null) {
                patient.setType(typeModifyingMap.get(patient.getType()));
            }
            var nextRoute = getNextRoute(task);
            if (nextRoute.isBlocked(task)) {
                continue;
            }
            if (nextRoute.getElement() != null) {
                task.setTimeOut(super.getTCurr());
                nextRoute.getElement().inAct(task);
            }
            channel.setCurrentTask(null);
            channel.setTNext(Double.MAX_VALUE);
            addQuantity(1);
            totalLeaveTime += super.getTCurr() - previousLeaveTime;
            previousLeaveTime = super.getTCurr();
        }
    }
}