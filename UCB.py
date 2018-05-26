import numpy as np
import math

n_classes = 15
repetitions = 10000

MODE = 'multiple'
# MODE = 'single'


class Generator:
    def __init__(self, probab):
        self.probab = probab

    def generate(self):
        return int(np.random.random() < self.probab)

    @staticmethod
    def gen_prob(probab):
        return int(np.random.random() < probab)


hits_one = 0
hits_two = 0
hits_three = 0
hits = 0

error = 0


class UCB:
    def __init__(self, test_subjects):
        self.test_subjects = np.asarray(test_subjects)
        self.sum_of_hits = np.asarray([0] * len(self.test_subjects))
        self.number_of_selections = np.asarray([0] * len(self.test_subjects))

        self.average_hits = None
        self.ucb = None
        self.delta = None

        self.choice = None

    def update_subject(self, subject_index, result):
        self.number_of_selections[subject_index] += 1
        self.sum_of_hits[subject_index] += result

    def init_tests(self):
        answers = [i.generate() for i in self.test_subjects]

        self.number_of_selections = np.asarray([1] * len(self.test_subjects))
        self.sum_of_hits = np.asarray(answers)

    def recalculate(self, turn):
        self.average_hits = self.sum_of_hits / self.number_of_selections
        self.delta = np.sqrt(3 / 2 * np.log(turn) / self.number_of_selections)
        self.ucb = self.average_hits * 2 + self.delta

    def get_best_subjects_indices(self):
        return np.argsort(-self.ucb)

    def test_best_subject(self):
        best = self.get_best_subjects_indices()[0]
        res = self.test_subjects[best].generate()
        self.update_subject(best, res)


def generate_varying_results(ucb, num_of_test_subjects, offset=None):
    offset = num_of_test_subjects if offset is None else offset
    # Get best subject
    first_best = ucb.get_best_subjects_indices()[:num_of_test_subjects]
    first_probs = np.mean([i.probab for i in ucb.test_subjects[first_best]])

    # Get second best subject
    second_best = ucb.get_best_subjects_indices()[offset:offset+num_of_test_subjects]
    second_probs = np.mean([i.probab for i in ucb.test_subjects[second_best]])

    # Generate until their results vary
    res_one = 0
    res_two = 0
    while(res_one == res_two):
        res_one = Generator.gen_prob(first_probs)
        res_two = Generator.gen_prob(second_probs)

    return first_best, second_best, res_one, res_two


for t in range(repetitions):
    generators = []
    for i in range(n_classes):
        generators.append(Generator(np.random.random()))

    ucb = UCB(generators)

    # Start by exploring every option
    ucb.init_tests()

    # Proceed with UCB
    for i in range(n_classes, 20):
        # Recalculate UBC parameters
        ucb.recalculate(i)

        if MODE == 'multiple':
            first_best, second_best, res_one, res_two = generate_varying_results(ucb, 1)
            ucb.update_subject(first_best, res_one)
            ucb.update_subject(second_best, res_two)
        else:
            ucb.test_best_subject()

    choices = np.argsort(-ucb.sum_of_hits)[:3]
    good = np.intersect1d(choices, np.argsort([-i.probab for i in generators])[:3])
    hits += len(good)
    if len(good) >= 1:
        hits_one += 1
    if len(good) >= 2:
        hits_two += 1
    if len(good) == 3:
        hits_three += 1

print(f'Mean result: {hits/repetitions : .3}/3 ({(hits/repetitions/3*100) : .3}% )')
print(f'1+ hits: ({(hits_one/repetitions*100) : .3}% )')
print(f'2+ hits: ({(hits_two/repetitions*100) : .3}% )')
print(f'3  hits: ({(hits_three/repetitions*100) : .3}% )')
