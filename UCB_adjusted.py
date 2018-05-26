import numpy as np
import math


class Category:
    def __init__(self):

        # initializes the dictionaries for each category e.g:
        # Category style:
        # impressionism : {"selection": 0, "hits": 0,  "ucb": 0} # zero is number of view and
        # surrealism : 0
        self.cat_dictionary = {}

class UCB_Assessment:
    def __init__(self, categories_list):
        self.cat_list = self.category_into_assesment(categories_list)

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
        ucb = average_hits * 2 + selection
        return ucb

    def run_assessment(self):
        # separate assesments for each category
        # fetch all categories and initialize dicts inside them
        global_categories = [Category() for i in range(self.cat_list-1)]
        self.assesment = self.cat_list[:, 2]
        for turn, judgement in enumerate(self.cat_list):
            if judgement[-1] == 1: # like
                for i, category in enumerate(judgement[:-1]): # skip assesment
                    if category in global_categories[i].cat_dictionary:
                        global_categories[i].cat_dictionary[category]["hits"] += 1
                        global_categories[i].cat_dictionary[category]["selection"] += 1
                        global_categories[i].cat_dictionary[category]["ucb"] = self.update_ucb(turn,
                                                                                               global_categories[i].cat_dictionary[category]["hits"],
                                                                                               global_categories[i].cat_dictionary[category]["selection"])
                    else:
                        global_categories[i].cat_dictionary[category] = {"hits": 1,
                                                                         "selection": 1,
                                                                         "ucb": self.update_ucb(turn, 1, 1)}
            elif judgement[-1] == 2: # dont like
                    if category in global_categories[i].cat_dictionary:
                        global_categories[i].cat_dictionary[category]["selection"] += 1
                        global_categories[i].cat_dictionary[category]["ucb"] = self.update_ucb(turn,
                                                                                               global_categories[i].cat_dictionary[category]["hits"],
                                                                                               global_categories[i].cat_dictionary[category]["selection"])
                    else:
                        global_categories[i].cat_dictionary[category] = {"hits": 0,
                                                                         "selection": 1,
                                                                         "ucb": self.update_ucb(turn, 0, 1)}
            else:
                pass
