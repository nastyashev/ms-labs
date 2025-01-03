import java.util.ArrayList;
import java.util.Arrays;

public class Model {
    protected final ArrayList<Element> elements;
    protected double tCurr;
    protected double tNext;
    protected int nearestEvent;
    protected boolean isFirstIteration = true;
    private int numberOfServiceSystems;

    public Model(int numberOfServiceSystems, Element... elements) {
        this.numberOfServiceSystems = numberOfServiceSystems;
        this.elements = new ArrayList<>(Arrays.asList(elements));
        tNext = 0.0;
        tCurr = tNext;
        nearestEvent = 0;
    }

    public void simulate() {
        long startTime = System.currentTimeMillis();
        int eventCount = 0;
        while (eventCount < numberOfServiceSystems + 1) {
            tNext = Double.MAX_VALUE;
            for (var element : elements) {
                if ((tCurr < element.getTNext() || isFirstIteration) && element.getTNext() < tNext) {
                    tNext = element.getTNext();
                    nearestEvent = element.getId();
                }
            }
            updateBlockedElements();
            double delta = tNext - tCurr;
            doModelStatistics(delta);
            for (Element element : elements) {
                element.doStatistics(delta);
            }
            tCurr = tNext;
            for (var element : elements) {
                element.setTCurr(tCurr);
            }
            elements.get(nearestEvent).outAct();
            for (var element : elements) {
                if (element.getTNext() == tCurr) {
                    element.outAct();
                }
            }
            isFirstIteration = false;
            eventCount++;
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Simulation time: " + (endTime - startTime) + " ms");
        printResult();
    }

    public void printResult() {
        for (var element : elements) {
            element.printResult();
            if (element instanceof Process p) {
                System.out.println("Середня довжина черги = " + p.getMeanQueue() / tCurr);
                System.out.println("Середнє завантаження = " + p.getWorkTime() / tCurr);
                System.out.println("Відмови = " + p.getFailures() / (double) (p.getQuantity() + p.getFailures()));
            }
        }
    }

    protected void doModelStatistics(double delta) {
        // Override in subclasses if needed
    }

    private void updateBlockedElements() {
        for (var element : elements) {
            if (element.getTNext() <= tCurr) {
                element.setTNext(tNext);
            }
        }
    }
}