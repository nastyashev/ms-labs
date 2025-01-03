public class Main {
    public static void main(String[] args) {
        int numberOfServiceSystems = 5;
        simulateBank(numberOfServiceSystems);
//        simulateClinic(numberOfServiceSystems);
    }

    private static void simulateBank(int numberOfServiceSystems) {
        var create = new Create("Create 1", 0.5, 0.1);
        var cashierWindow1 = new SwitchingProcess("Касир 1", 1, 0.3, 1, 2);
        var cashierWindow2 = new SwitchingProcess("Касир 2", 1, 0.3, 1, 2);
        var dispose = new Dispose("Dispose 1");

        initializeCashierWindow(cashierWindow1, cashierWindow2);
        initializeCashierWindow(cashierWindow2, cashierWindow1);

        create.setDistribution("exp");
        cashierWindow1.setDistribution("exp");
        cashierWindow1.setDelayMean(0.3);
        cashierWindow2.setDistribution("exp");
        cashierWindow2.setDelayMean(0.3);

        cashierWindow1.setMaxQueueSize(3);
        cashierWindow2.setMaxQueueSize(3);

        create.setRouting("prior");
        create.addRoutes(
                new Route(cashierWindow1, 0.5, 1, (Task task) -> cashierWindow2.getQueueSize() < cashierWindow1.getQueueSize()),
                new Route(cashierWindow2, 0.5, 0)
        );

        cashierWindow1.addRoutes(new Route(dispose));
        cashierWindow2.addRoutes(new Route(dispose));

        var model = new BankModel(numberOfServiceSystems, create, cashierWindow1, cashierWindow2, dispose);
        model.simulate(10000);
    }

    private static void initializeCashierWindow(SwitchingProcess cashierWindow, SwitchingProcess neighbor) {
        cashierWindow.initializeChannelsWithTasks(1);
        cashierWindow.initializeQueueWithTasks(2);
        cashierWindow.setNeighbors(neighbor);
    }

    private static void simulateClinic(int numberOfServiceSystems) {
        final int[] patientTypes = {1, 2, 3};
        final double[] patientFrequencies = {0.5, 0.1, 0.4};
        final double[] patientDelays = {15, 40, 30};

        var create = new PatientCreate("Patient Creator", 15);
        var registration = new RegistrationProcess("Registration", 15, 2);
        var wardsTransfer = new Process("Wards Transfer", 3, 8, 3);
        var laboratoryTransfer = new Process("Laboratory Transfer", 2, 5, 100);
        var laboratoryRegistration = new Process("Laboratory Registration", 4.5, 3, 1);
        var laboratoryAnalysis = new TypeModifyingProcess("Laboratory Analysis", 4, 2, 2);
        var registrationTransfer = new Process("Registration Transfer", 2, 5, 100);

        var wardsDispose = new Dispose("Dispose [Type 1 & 2]");
        var laboratoryDispose = new Dispose("Dispose [Type 3]");

        create.setPatientTypedFrequencies(patientTypes, patientFrequencies);
        registration.setPatientTypedDelays(patientTypes, patientDelays);
        registration.setPrioritizedPatientType(1);
        laboratoryAnalysis.setTypeModifyingMap(new int[]{2}, new int[]{1});

        setDistributions(create, registration, wardsTransfer, laboratoryTransfer, laboratoryRegistration, laboratoryAnalysis, registrationTransfer);

        create.addRoutes(new Route(registration));
        registration.addRoutes(
                new Route(wardsTransfer, 0.5, 1, (Task task) -> ((Patient) task).getType() != 1),
                new Route(laboratoryTransfer, 0.5, 0)
        );
        registration.setRouting("prior");
        wardsTransfer.addRoutes(new Route(wardsDispose));
        laboratoryTransfer.addRoutes(new Route(laboratoryRegistration));
        laboratoryRegistration.addRoutes(new Route(laboratoryAnalysis));
        laboratoryAnalysis.addRoutes(
                new Route(laboratoryDispose, 0.5, 1, (Task task) -> ((Patient) task).getType() != 3),
                new Route(registrationTransfer, 0.5, 0)
        );
        laboratoryAnalysis.setRouting("prior");
        registrationTransfer.addRoutes(new Route(registration));

        var model = new ClinicModel(numberOfServiceSystems, create, registration, wardsTransfer, laboratoryTransfer, laboratoryRegistration,
                laboratoryAnalysis, registrationTransfer, wardsDispose, laboratoryDispose);
        model.simulate(10000);
    }

    private static void setDistributions(Element... elements) {
        for (Element element : elements) {
            if (element instanceof Create || element instanceof RegistrationProcess) {
                element.setDistribution("exp");
            } else if (element instanceof Process) {
                if (element.getName().contains("Transfer")) {
                    element.setDistribution("unif");
                } else if (element.getName().contains("Registration")) {
                    element.setDistribution("erl");
                }
            } else if (element instanceof TypeModifyingProcess) {
                element.setDistribution("erl");
            }
        }
    }
}
