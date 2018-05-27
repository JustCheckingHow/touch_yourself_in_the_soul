from UCB_adjusted import *
from Serwer.aiinterface import *
from dBinterface import DBInterface

class UCBInterface(TestAiInterface):

    def __init__(self):
        self.suggestedId = 0
        self.suggestedCats = None
        
    def onExhibitsRequested(self, howMany):
        return (39553, 41478, 40304, 40744, 39574, 41300, 41700, 41900, 39029, 40097, 39957)

    def onExhibitsRates(self, rates):
        ratesList = list(rates.values())
        self.ucb = UCB_Assessment(ratesList)
        self.ucb.run_assessment()
        self.suggestedCats = self.ucb.yield_results()
        
        self.suggestedCats.sort()
                
    def getSuggestedId(self, howMany):
        if self.suggestedCats is not None:
            self.db = DBInterface("baza1.db")
            ids1 = self.db.getIdsWithSubjectOrType(self.suggestedCats[0][1])
            ids2 = self.db.getIdsWithSubjectOrType(self.suggestedCats[1][1])
            self.db.close()
            
            toReturn = ids2[-3:]
            return [i[0] for i in toReturn]
        else:
            return []
        