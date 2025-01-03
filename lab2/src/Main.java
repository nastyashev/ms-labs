import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
//        simpleModel();
        threeProcessesModel();
//        complexModel();
    }

    // 1-2
    public static void simpleModel() {
        Create create = new Create(2.0);
        Process process = new Process(1.0);

        create.setName("CREATE");
        process.setName("PROCESS");

        process.setMaxqueue(5);

        create.addNextElement(process);

        create.setDistribution("exp");
        process.setDistribution("exp");

        ArrayList<Element> elements = new ArrayList<>(
                Arrays.asList(create, process)
        );

        Model model = new Model(elements);
        model.simulate(1000.0);
    }

    // 3-4
    public static void threeProcessesModel() {
        Create create = new Create(2.0);
        Process process1 = new Process(1.0);
        Process process2 = new Process(1.0);
        Process process3 = new Process(2.0);

        create.setName("CREATE");
        process1.setName("PROCESS-1");
        process2.setName("PROCESS-2");
        process3.setName("PROCESS-3");

        process1.setMaxqueue(5);
        process2.setMaxqueue(5);
        process3.setMaxqueue(5);

        create.addNextElement(process1);
        process1.addNextElement(process2);
        process2.addNextElement(process3);

        create.setDistribution("exp");
        process1.setDistribution("exp");
        process2.setDistribution("exp");
        process3.setDistribution("exp");

        ArrayList<Element> elements = new ArrayList<>(
                Arrays.asList(create, process1, process2, process3)
        );

        Model model = new Model(elements);
        model.simulate(1000.0);
    }

    // 5-6
    public static void complexModel() {
        Create create = new Create(1.0);
        Process process1 = new Process(2.0, 4);
        Process process2 = new Process(1.0);
        Process process3 = new Process(1.0);

        create.setName("CREATE");
        process1.setName("PROCESS-1");
        process2.setName("PROCESS-2");
        process3.setName("PROCESS-3");

        process1.setMaxqueue(5);
        process2.setMaxqueue(10);
        process3.setMaxqueue(10);

        create.addNextElement(process1);
        process1.addNextElement(process2);
        process2.addNextElement(process1);
        process2.addNextElement(process3);

        create.setDistribution("exp");
        process1.setDistribution("exp");
        process2.setDistribution("exp");
        process3.setDistribution("exp");

        ArrayList<Element> elements = new ArrayList<>(
                Arrays.asList(create, process1, process2, process3)
        );

        Model model = new Model(elements);
        model.simulate(1000.0);
    }
}