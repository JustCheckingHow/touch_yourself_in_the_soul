import numpy as np
import math
from Serwer.dBinterface import DBInterface
import pickle

class Category:
    def __init__(self):
        # initializes the dictionaries for each category e.g:
        # Category style:
        # impressionism : {"selection": 0, "hits": 0,  "ucb": 0} # zero is number of view and
        # surrealism : 0
        self.cat_dictionary = {}

class UCB_Assessment:
    def __init__(self, categories_list):
        self.cat_list = categories_list
        self.cat_len = len(categories_list[0])
        print(self.cat_len)
        try:
            self.restore()
        except IOError as e:
            print("Restore data not found")

    def update_ucb(self, turn, hits, selection):
        average_hits = hits / selection
        delta = np.sqrt(3 / 2 * np.log(turn) / selection)
        ucb = average_hits * 2 + delta
        print(turn, average_hits, delta, ucb)
        return ucb

    def run_assessment(self):
        # separate assesments for each category
        # fetch all categories and initialize dicts inside them
        self.global_categories = [Category() for i in range(self.cat_len-1)]
        print(len(self.cat_list))
        for turn, judgement in enumerate(self.cat_list):
            turn += 1
            if judgement[-1] == 1: # like
                for i, category in enumerate(judgement[:-1]): # skip assesment
                    for element in category:
                        if element in list(self.global_categories[i].cat_dictionary.keys()):
                            self.global_categories[i].cat_dictionary[element]["hits"] += 1
                            self.global_categories[i].cat_dictionary[element]["selection"] += 1
                            self.global_categories[i].cat_dictionary[element]["ucb"] = self.update_ucb(turn,
                                                                                                   self.global_categories[i].cat_dictionary[element]["hits"],
                                                                                                   self.global_categories[i].cat_dictionary[element]["selection"])
                        else:
                            self.global_categories[i].cat_dictionary[element] = {"hits": 1,
                                                                         "selection": 1,
                                                                         "ucb": self.update_ucb(turn, 1, 1)}
            elif judgement[-1] == 2: # dont like
                for i, category in enumerate(judgement[:-1]): # skip assesment
                    for element in category:
                        if category in list(self.global_categories[i].cat_dictionary.keys()):
                            self.global_categories[i].cat_dictionary[element]["selection"] += 1
                            self.global_categories[i].cat_dictionary[element]["ucb"] = self.update_ucb(turn,
                                                                                                   self.global_categories[i].cat_dictionary[element]["hits"],
                                                                                                   self.global_categories[i].cat_dictionary[element]["selection"])
                        else:
                            self.global_categories[i].cat_dictionary[element] = {"hits": 0,
                                                                             "selection": 1,
                                                                             "ucb": self.update_ucb(turn, 0, 1)}
            else:
                pass

        self.dump()


    def yield_results(self):
        result = []
        for category in self.global_categories:
            print("CATEGORY :{}\n".format(category))
            maximal = 0
            best_element = None
            for element in category.cat_dictionary:
                print(element, category.cat_dictionary[element])
                if category.cat_dictionary[element]["ucb"] > maximal:
                    best_element = element
                    maximal = category.cat_dictionary[element]["ucb"]
            result.append([maximal, best_element])
        return result

    def dump(self):
        with open("UCB_params.pkl", "wb") as f:
            pickle.dump(self.global_categories, f)

    def restore(self):
        with open("UCB_params.pkl", "rb") as f:
            self.global_categories = pickle.load(f)

if __name__ == "__main__":
    db = DBInterface("Serwer/baza1.db")
    # 39000 - 42000
    global_cat_list = []
    for i in range(39000, 39100):
        sublist = db.getSubject(i)
        if len(sublist) != 0:
            sublist.append(np.random.randint(0, 3))
            global_cat_list.append(sublist)
    print(global_cat_list)
    ucb = UCB_Assessment(global_cat_list)
    ucb.run_assessment()
    print(ucb.yield_results())
