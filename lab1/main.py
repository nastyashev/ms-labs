from generators import generate_n_exponential, generate_n_normal, generate_n_uniform
from chi_squared import check_sample_distribution_law
from scipy.stats import expon, norm, uniform
from custom_statistics import find_sample_mean_and_variance, get_frequency_table, merge_intervals, build_histogram
from math import sqrt

def process_distribution(law_name, sample, intervals, merged_intervals, merged_frequencies, theoretic_density, parameters, degrees_of_freedom):
    build_histogram(sample, intervals, theoretic_density, merged_intervals, law_name, parameters)
    check_sample_distribution_law(law_name, merged_frequencies, theoretic_density, len(sample), degrees_of_freedom)

if __name__ == '__main__':
    SAMPLE_SIZE = 10000

    # Exponential distribution
    lambdas = [0.05, 0.5, 0.8, 1.5]
    for lamda in lambdas:
        parameters = f'λ = {lamda}'
        print(f'========== Перевірка для {parameters}, експоненційний розподіл ==========')
        sample = generate_n_exponential(SAMPLE_SIZE, lamda)
        mean, variance = find_sample_mean_and_variance(sample)
        intervals, frequencies = get_frequency_table(sample)
        merged_intervals, merged_frequencies = merge_intervals(intervals, frequencies)
        theoretic_density = [expon.cdf(merged_intervals[i + 1], scale=1 / (1/mean)) - expon.cdf(merged_intervals[i], scale=1 / (1/mean)) for i in range(len(merged_intervals) - 1)]
        process_distribution('експоненційний', sample, intervals, merged_intervals, merged_frequencies, theoretic_density, parameters, len(merged_frequencies) - 2)

    # Normal distribution
    sigmas = [10, 20, 40, 50]
    alphas = [5, 40, 100, 200]
    for sigma, alpha in zip(sigmas, alphas):
        parameters = f'σ = {sigma}, α = {alpha}'
        print(f'========== Перевірка для {parameters}, нормальний розподіл ==========')
        sample = generate_n_normal(SAMPLE_SIZE, sigma, alpha)
        mean, variance = find_sample_mean_and_variance(sample)
        intervals, frequencies = get_frequency_table(sample)
        merged_intervals, merged_frequencies = merge_intervals(intervals, frequencies)
        theoretic_density = [norm.cdf(merged_intervals[i + 1], loc=mean, scale=sqrt(variance)) - norm.cdf(merged_intervals[i], loc=mean, scale=sqrt(variance)) for i in range(len(merged_intervals) - 1)]
        process_distribution('нормальний', sample, intervals, merged_intervals, merged_frequencies, theoretic_density, parameters, len(merged_frequencies) - 3)

    # Uniform distribution
    a_values = [5 ** 13, 7 ** 5, 10 ** 10, 95]
    c_values = [2 ** 31, 2 ** 12, 42 ** 10, 108]
    for a, c in zip(a_values, c_values):
        parameters = f'a = {a}, c = {c}'
        print(f'========== Перевірка для {parameters}, рівномірний розподіл ==========')
        sample = generate_n_uniform(SAMPLE_SIZE, a, c)
        find_sample_mean_and_variance(sample)
        intervals, frequencies = get_frequency_table(sample)
        merged_intervals, merged_frequencies = merge_intervals(intervals, frequencies)
        theoretic_density = [uniform.cdf(intervals[i + 1], loc=0, scale=1) - uniform.cdf(intervals[i], loc=0, scale=1) for i in range(len(intervals) - 1)]
        build_histogram(sample, intervals, theoretic_density, intervals, 'рівномірний', parameters)
        theoretic_density = [uniform.cdf(merged_intervals[i + 1], loc=0, scale=1) - uniform.cdf(merged_intervals[i], loc=0, scale=1) for i in range(len(merged_intervals) - 1)]
        if len(merged_frequencies) - 3 < 0:
            print('Занадто мала кількість ступенів свободи, перевірка не може бути проведена')
            continue
        check_sample_distribution_law('рівномірний', merged_frequencies, theoretic_density, SAMPLE_SIZE, len(merged_frequencies) - 3)