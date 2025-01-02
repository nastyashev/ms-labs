import matplotlib.pyplot as plt

def get_frequency_table(values, k=20):
    min_value, max_value = min(values), max(values)
    interval_length = (max_value - min_value) / k
    intervals = [min_value + i * interval_length for i in range(k + 1)]
    frequencies = [0] * k
    for value in values:
        for i in range(k):
            if intervals[i] <= value < intervals[i + 1] or (i == k - 1 and value == intervals[i + 1]):
                frequencies[i] += 1
                break
    return intervals, frequencies

def merge_intervals(intervals, frequencies, min_falls_per_interval=5):
    merged_intervals = [intervals[0], intervals[1]]
    merged_frequencies = [frequencies[0]]
    for i in range(2, len(intervals)):
        if merged_frequencies[-1] < min_falls_per_interval:
            merged_intervals[-1] = intervals[i]
            merged_frequencies[-1] += frequencies[i - 1]
        else:
            merged_intervals.append(intervals[i])
            merged_frequencies.append(frequencies[i - 1])
    if merged_frequencies[-1] < min_falls_per_interval:
        merged_frequencies[-2] += merged_frequencies[-1]
        merged_intervals[-2] = merged_intervals[-1]
        merged_intervals.pop()
        merged_frequencies.pop()
    return merged_intervals, merged_frequencies

def find_sample_mean_and_variance(sample):
    mean = sum(sample) / len(sample)
    variance = sum((x - mean) ** 2 for x in sample) / len(sample)
    unbiased_variance = variance * len(sample) / (len(sample) - 1)
    print(
        f'Вибіркове середнє: {mean}'
        f'\nВибіркова дисперсія: {variance}'
        f'\nВиправлена вибіркова дисперсія: {unbiased_variance}'
    )
    return mean, unbiased_variance

def build_histogram(sample, intervals, theoretic_density, merged_intervals, law_name, parameters):
    plt.hist(sample, bins=intervals, edgecolor='black')
    plt.xticks(intervals, rotation='vertical')
    plt.title(f'Гістограма частот вибірки ({law_name} генератор), {parameters}')
    plt.xlabel('Інтервали')
    plt.ylabel('Кількість попадань')

    theoretic_law = [x * len(sample) for x in theoretic_density]
    intervals_centers = [(merged_intervals[i] + merged_intervals[i + 1]) / 2 for i in range(len(merged_intervals) - 1)]
    plt.plot(intervals_centers, theoretic_law, color='red')
    plt.subplots_adjust(bottom=0.2)
    plt.savefig(f'out/histogram_{law_name}_{parameters}.png')
    plt.show()