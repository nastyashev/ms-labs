import java.util.ArrayList;

public class ClinicModel extends Model {
    public ClinicModel(Element... elements) {
        super(elements);
    }

    private double getLaboratoryArrivalInterval() {
        for (var element : elements) {
            if (element.getName().equals("Laboratory Transfer")) {
                return ((Process) element).getMeanLeaveInterval();
            }
        }
        return 0.0;
    }

    @Override
    public void printResult() {
        System.out.println("Середній час в системі  = " + getMeanTimeInSystem());
        System.out.println("Інтервал між прибуттям у лабораторію = " + getLaboratoryArrivalInterval());
    }

    private double getMeanTimeInSystem() {
        var patients = new ArrayList<Task>();
        for (var element : elements) {
            if (element instanceof Dispose d) {
                patients.addAll(d.getProcessedJobs());
            }
        }
        var sum = 0.0;
        for (var patient : patients) {
            sum += patient.getTimeOut() - patient.getTimeIn();
        }
        return sum / patients.size();
    }
}