import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Element {

    private final String name;
    private final int id;
    private static int nextId = 0;
    private int quantity = 0;
    private int state = 0;
    private String routing = "prior";
    private String distribution;
    private double tNext;
    private double tCurr;
    private double delayMean;
    private double delayDev;
    private final ArrayList<Route> routes = new ArrayList<>();

    public Element(String nameOfElement) {
        this(nameOfElement, 1.0, Double.MAX_VALUE);
    }

    public Element(String nameOfElement, double delayMean) {
        this(nameOfElement, delayMean, 0.0);
    }

    public Element(String nameOfElement, double delayMean, double delayDev) {
        name = nameOfElement;
        tNext = 0.0;
        tCurr = tNext;
        this.delayMean = delayMean;
        this.delayDev = delayDev;
        distribution = "norm";
        id = nextId++;
    }

    private static ArrayList<Route> getUnblockedRoutes(ArrayList<Route> routes, Task routedTask) {
        var unblockedRoutes = new ArrayList<Route>();
        for (var route : routes) {
            if (!route.isBlocked(routedTask)) {
                unblockedRoutes.add(route);
            }
        }
        return unblockedRoutes;
    }

    private static double[] getScaledProbabilities(ArrayList<Route> routes) {
        var probabilities = new double[routes.size()];
        for (int i = 0; i < routes.size(); i++) {
            probabilities[i] = routes.get(i).getProbability() + (i == 0 ? 0 : probabilities[i - 1]);
        }
        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] *= 1 / (probabilities[probabilities.length - 1]);
        }
        return probabilities;
    }

    public double getDelay() {
        switch (distribution) {
            case "exp":
                return FunRand.Exponential(delayMean);
            case "unif":
                return FunRand.Uniform(delayMean, delayDev);
            case "norm":
                return FunRand.Normal(delayMean, delayDev);
            case "erl":
                return FunRand.Erlang(delayMean, delayDev);
            default:
                return delayMean;
        }
    }

    public double getDelayMean() {
        return delayMean;
    }
    public void setDelayMean(double delayMean) {
        this.delayMean = delayMean;
    }

    public double getDelayDev() {
        return delayDev;
    }
    public void setDelayDev(double delayDev) {
        this.delayDev = delayDev;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void addQuantity(int delta) {
        this.quantity += delta;
    }

    public void inAct(Task task) {
        // Override in subclasses if needed
    }
    public void outAct() {
        quantity++;
    }

    public double getTNext() {
        return tNext;
    }
    public void setTNext(double tNext) {
        this.tNext = tNext;
    }

    public double getTCurr() {
        return tCurr;
    }
    public void setTCurr(double tCurr) {
        this.tCurr = tCurr;
    }

    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public Route getNextRoute(Task routedTask) {
        if (routes.isEmpty()) {
            return new Route(null);
        }
        switch (routing) {
            case "prob":
                return getNextRouteByProbability(routedTask);
            case "prior":
                return getNextRouteByPriority(routedTask);
            case "comb":
                return getNextRouteCombined(routedTask);
            default:
                throw new IllegalStateException("Unexpected value: " + routing);
        }
    }

    private Route getNextRouteByPriority(Task routedTask) {
        var unblockedRoutes = getUnblockedRoutes(routes, routedTask);
        return unblockedRoutes.isEmpty() ? routes.get(0) : unblockedRoutes.get(0);
    }

    private ArrayList<Route> findRoutesByPriority(int priority) {
        var routesByPriority = new ArrayList<Route>();
        for (var route : routes) {
            if (route.getPriority() == priority) {
                routesByPriority.add(route);
            }
        }
        return routesByPriority;
    }

    public void addRoutes(Route... routes) {
        for (var route : routes) {
            this.routes.add(route);
        }
        this.routes.sort(Comparator.comparingInt(Route::getPriority).reversed());
    }

    private Route getNextRouteByProbability(Task routedTask) {
        var unblockedRoutes = getUnblockedRoutes(routes, routedTask);
        if (unblockedRoutes.isEmpty()) {
            return routes.get(0);
        }
        double probability = Math.random();
        var scaledProbabilities = getScaledProbabilities(unblockedRoutes);
        for (int i = 0; i < scaledProbabilities.length; i++) {
            if (probability < scaledProbabilities[i]) {
                return unblockedRoutes.get(i);
            }
        }
        return unblockedRoutes.get(unblockedRoutes.size() - 1);
    }

    private Route getNextRouteCombined(Task routedTask) {
        Route selectedRoute = null;
        for (var route : routes) {
            if (!route.isBlocked(routedTask)) {
                selectedRoute = route;
                break;
            }
        }
        if (selectedRoute == null) {
            return routes.get(0);
        }

        var samePriorityRoutes = findRoutesByPriority(selectedRoute.getPriority());
        double probability = Math.random();
        var scaledProbabilities = getScaledProbabilities(samePriorityRoutes);
        for (int i = 0; i < scaledProbabilities.length; i++) {
            if (probability < scaledProbabilities[i]) {
                selectedRoute = samePriorityRoutes.get(i);
                break;
            }
        }
        return selectedRoute;
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }

    public void printInfo() {
        System.out.println(name + " state = " + getState() + " quantity = " + getQuantity() + " tnext = " + getTNext());
    }

    public void printResult() {
        System.out.println(name + " quantity = " + getQuantity());
    }

    public int getId() {
        return id;
    }

    public void doStatistics(double delta) {
        // Override in subclasses if needed
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }
}