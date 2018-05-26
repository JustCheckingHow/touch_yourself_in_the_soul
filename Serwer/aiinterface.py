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
		
class TestAiInterface(AIInterface):

	def onExhibitsRequested(self, howMany):
		return (123, 4556, 7890)

	def onExhibitsRates(self, rates):
		pass
	