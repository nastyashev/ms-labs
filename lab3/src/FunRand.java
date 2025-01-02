import java.util.Random;

public class FunRand {
    public static double Exponential(double timeMean) {
        double a = 0;
        while (a == 0) {
            a = Math.random();
        }
        a = -timeMean * Math.log(a);
        return a;
    }

    public static double Uniform(double timeMin, double timeMax) {
        double a = 0;
        while (a == 0) {
            a = Math.random();
        }
        a = timeMin + a * (timeMax - timeMin);
        return a;
    }

    public static double Normal(double timeMean, double timeDeviation) {
        double a;
        Random r = new Random();
        a = timeMean + timeDeviation * r.nextGaussian();
        return a;
    }

    public static double Erlang(double timeMean, double shape) {
        double a = 0;
        for (int i = 0; i < shape; i++) {
            a += Math.log(Math.random());
        }
        return (-1 / (timeMean / shape)) * a;
    }
}