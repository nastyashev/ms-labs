from scipy.stats import chi2

def chi_squared_test(frequencies, theoretical_density, size_of_sample, degrees_of_freedom, significance_level=0.05):
    chi_squared = sum(((frequencies[i] - theoretical_density[i] * size_of_sample) ** 2) / (theoretical_density[i] * size_of_sample) for i in range(len(frequencies)))
    chi_squared_critical = chi2.ppf(1 - significance_level, degrees_of_freedom)
    return chi_squared, chi_squared_critical

def check_sample_distribution_law(distribution_name, frequencies, theoretic_density, size_of_sample, degrees_of_freedom):
    chi_squared, chi_squared_critical = chi_squared_test(frequencies, theoretic_density, size_of_sample, degrees_of_freedom)
    print(
        f'Значення χ-квадрат: {chi_squared}'
        f'\nКритичне значення: {chi_squared_critical}'
    )
    if chi_squared < chi_squared_critical:
        print(f'Гіпотеза про {distribution_name} розподіл приймається')
    else:
        print(f'Гіпотеза про {distribution_name} розподіл відхиляється')