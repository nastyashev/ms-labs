import math
import random

def generate_n_exponential(n, _lambda, _seed=None):
    uniform_generator = random.Random(_seed)
    return [(-1 / _lambda) * math.log(uniform_generator.random()) for _ in range(n)]

def generate_n_normal(n, _sigma, _alpha, _seed=None):
    uniform_generator = random.Random(_seed)
    mu_i = lambda: sum(uniform_generator.random() for _ in range(12)) - 6
    return [_sigma * mu_i() + _alpha for _ in range(n)]

def generate_n_uniform(n, _a, _c, _z_0=1):
    z_prev = _z_0
    generated_values = []
    for _ in range(n):
        z_i = (_a * z_prev) % _c
        generated_values.append(z_i / _c)
        z_prev = z_i
    return generated_values