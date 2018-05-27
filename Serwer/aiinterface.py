class AIInterface:

	def __init__(self):
		pass
		
	def onExhibitsRequested(self, howMany):
		"""
		Return tuple with IDs of exhibits to show
		"""
		return ()

	def onExhibitsRates(self, rates):
		pass
		
    def getSuggestedId(self):
        return "01/iver"
        
class TestAiInterface(AIInterface):

	def onExhibitsRequested(self, howMany):
		return (39000, 39009, 39013, 39020, 39021, 39024, 39027, 39028, 39029, 39041, 39042, 39031, 39075, 39087, 39101, 39107, 39108, 40746, 40792, 41030)

	def onExhibitsRates(self, rates):
		print(rates)
	