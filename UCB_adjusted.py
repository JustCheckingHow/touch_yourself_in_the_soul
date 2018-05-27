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
        try:
            self.restore()
        except IOError as e:
            print("Restore data not found")

    def category_into_assesment(self, user_list):
        """
        parse style, genre, assesment into object list cat_list
        """
        category_list = []
        for element in user_list:
            category_list.append(CategoryStyle(element[0]),
                                 CategoryGenre(element[1]),
                                 element[2])
        return np.array(category_list)


    def update_ucb(self, turn, hits, selection):
        average_hits = hits / selection
        delta = np.sqrt(3 / 2 * np.log(turn) / selection)
        print(delta)
        ucb = average_hits * 2 + delta
        return ucb

    def run_assessment(self):
        # separate assesments for each category
        # fetch all categories and initialize dicts inside them
        self.global_categories = [Category() for i in range(len(self.cat_list)-1)]
        print(len(self.cat_list))
        for turn, judgement in enumerate(self.cat_list):
            turn += 1
            if judgement[-1] == 1: # like
                for i, category in enumerate(judgement[:-1]): # skip assesment
                    if category in self.global_categories[i].cat_dictionary:
                        self.global_categories[i].cat_dictionary[category]["hits"] += 1
                        self.global_categories[i].cat_dictionary[category]["selection"] += 1
                        self.global_categories[i].cat_dictionary[category]["ucb"] = self.update_ucb(turn,
                                                                                               self.global_categories[i].cat_dictionary[category]["hits"],
                                                                                               self.global_categories[i].cat_dictionary[category]["selection"])
                    else:
                        self.global_categories[i].cat_dictionary[category] = {"hits": 1,
                                                                         "selection": 1,
                                                                         "ucb": self.update_ucb(turn, 1, 1)}
            elif judgement[-1] == 2: # dont like
                for i, category in enumerate(judgement[:-1]): # skip assesment
                    if category in self.global_categories[i].cat_dictionary:
                        self.global_categories[i].cat_dictionary[category]["selection"] += 1
                        self.global_categories[i].cat_dictionary[category]["ucb"] = self.update_ucb(turn,
                                                                                               self.global_categories[i].cat_dictionary[category]["hits"],
                                                                                               self.global_categories[i].cat_dictionary[category]["selection"])
                    else:
                        self.global_categories[i].cat_dictionary[category] = {"hits": 0,
                                                                         "selection": 1,
                                                                         "ucb": self.update_ucb(turn, 0, 1)}
            else:
                pass

        ucb.dump()


    def yield_results(self):
        result = []
        for category in self.global_categories:
            maximal = 0
            best_element = None
            for element in category.cat_dictionary:
                print(category.cat_dictionary[element])
                if category.cat_dictionary[element]["ucb"] > maximal:
                    best_element = category.cat_dictionary[element]
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
