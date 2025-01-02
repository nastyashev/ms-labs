import java.util.Random;

public class FunRand {
    public static double Exponential(double timeMean) {
        double randomValue;
        do {
            randomValue = Math.random();
        } while (randomValue == 0);
        return -timeMean * Math.log(randomValue);
    }

    public static double Uniform(double timeMin, double timeMax) {
        double randomValue;
        do {
            randomValue = Math.random();
        } while (randomValue == 0);
        return timeMin + randomValue * (timeMax - timeMin);
    }

    public static double Normal(double timeMean, double timeDeviation) {
        Random random = new Random();
        return timeMean + timeDeviation * random.nextGaussian();
    }

    public static double Erlang(double timeMean, double shape) {
        double sum = 0;
        for (int i = 0; i < shape; i++) {
            sum += Math.log(Math.random());
        }
        return (-1 / (timeMean / shape)) * sum;
    }
}