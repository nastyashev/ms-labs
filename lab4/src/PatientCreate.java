import java.util.HashMap;

public class PatientCreate extends Create {

    private HashMap<Integer, Double> patientTypedFrequencies;

    public PatientCreate(String name, double delay) {
        super(name, delay);
    }

    public void setPatientTypedFrequencies(int[] types, double[] frequencies) {
        patientTypedFrequencies = new HashMap<>();
        for (int i = 0; i < types.length; i++) {
            patientTypedFrequencies.put(types[i], frequencies[i]);
        }
    }

    @Override
    protected Task createTask() {
        int type = choosePatientType();
        return new Patient(super.getTCurr(), type);
    }

    private int choosePatientType() {
        double random = Math.random();
        double sum = 0.0;
        for (var entry : patientTypedFrequencies.entrySet()) {
            sum += entry.getValue();
            if (random < sum) {
                return entry.getKey();
            }
        }
        return 0;
    }
}