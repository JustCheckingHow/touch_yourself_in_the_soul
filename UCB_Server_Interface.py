from UCB_adjusted import *
from Serwer.aiinterface import *

class UCBInterface(TestAiInterface):

    def __init__(self):
        pass
    
    def onExhibitsRequested(self, howMany):
        return (39000, 39009, 39013, 39020, 39021, 39024, 39027, 39028, 39029, 39041, 39042, 39031, 39075, 39087, 39101, 39107, 39108, 40746, 40792, 41030)

    def onExhibitsRates(self, rates):
        self.ucb = UCB_Assessment(rates)
        self.ucb.run_assessment()
        self.ucb.yield_results()
        
    def getSuggestedId(self):
        return 234
        