public class Create extends Element {

    public Create(String name, double delay) {
        super(name, delay);
        super.setTNext(0.0);
    }

    public Create(String name, double delay, double initialTNext) {
        super(name, delay);
        super.setTNext(initialTNext);
    }

    @Override
    public void outAct() {
        int failures = 0;

        super.outAct();
        super.setTNext(super.getTCurr() + super.getDelay());
        var createdTask = createTask();
        var nextRoute = super.getNextRoute(createdTask);

        if (nextRoute.getElement() == null || nextRoute.isBlocked(createdTask)) {
            failures++;
        } else {
            nextRoute.getElement().inAct(createdTask);
        }
    }

    protected Task createTask() {
        return new Task(super.getTCurr());
    }
}