from UCB_adjusted import *
from Serwer.aiinterface import *
from Serwer.dBinterface import DBInterface

class UCBInterface(TestAiInterface):

    def __init__(self):
        db = DBInterface("Serwer/baza1.db")
        # 39000 - 42000
        global_cat_list = []
        for i in range(39000, 39100):
            sublist = db.getSubject(i)
            if len(sublist) != 0:
                sublist.append(np.random.randint(0, 3))
                global_cat_list.append(sublist)
        print(global_cat_list)
        self.ucb = UCB_Assessment(global_cat_list)
    
    def onExhibitsRequested(self, howMany):
        return (39000, 39009, 39013, 39020, 39021, 39024, 39027, 39028, 39029, 39041, 39042, 39031, 39075, 39087, 39101, 39107, 39108, 40746, 40792, 41030)

    def onExhibitsRates(self, rates):
        ratesList = list(rates.values())
        self.ucb.update_ucb(ratesList)
        self.ucb.run_assessment()
        self.ucb.yield_results()