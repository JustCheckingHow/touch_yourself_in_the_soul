from UCB_adjusted import *
from Serwer.aiinterface import *
from dBinterface import DBInterface

class UCBInterface(TestAiInterface):

    def __init__(self):
        self.suggestedId = 0
        
    def onExhibitsRequested(self, howMany):
        return (39000, 39009, 39013, 39020, 39021, 39024, 39027, 39028, 39029, 39041, 39042, 39031, 39075, 39087, 39101, 39107, 39108, 40746, 40792, 41030)

    def onExhibitsRates(self, rates):
        ratesList = list(rates.values())
        self.ucb = UCB_Assessment(ratesList)
        self.ucb.run_assessment()
        suggestedCats = self.ucb.yield_results()
        
        suggestedCats.sort()
        
        print(suggestedCats)
        self.db = DBInterface("baza1.db")
        ids1 = self.db.getIdsWithSubjectOrType(suggestedCats[0][1])
        ids2 = self.db.getIdsWithSubjectOrType(suggestedCats[1][1])
        self.db.close()
        
        print(ids1)
        print(ids2)
        
        isBetter = False
        for i in ids2:
            if i in ids1:
                self.suggestedId = i[0]
                isBetter = True
            else:
                if not isBetter: self.suggestedId = i[0]
                
    def getSuggestedId(self):
        return self.suggestedId
        