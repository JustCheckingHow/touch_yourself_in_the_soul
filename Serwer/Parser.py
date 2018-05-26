import requests
import xml.etree.ElementTree as ET
import bs4
import pickle

class Parser:
    def __init__(self):
        pass

    def getDataFromId(self, id):
        self.main_address = 'http://cyfrowe.mnw.art.pl/dmuseion/rdf.xml?type=e&id='
        self.r = requests.get(self.main_address + str(id))

    def parseXMLTree(self):
        self.tree = ET.fromstring(self.r.text)

        self.data = []
        i = 0
        try:
            self.tree[0]
        except:
            # self.data.append() = []
            return
        for e in self.tree[0]:
            tag = e.tag[e.tag.rfind("}")+1:]
            text = e.text
            self.data.append([tag, text])
            i += 1

    def getSubject():
        

    def getType():

    def getData(self):
        return self.data

p = Parser()
allData = []
for i in range(3000):
    p.getDataFromId(39000 + i)
    p.parseXMLTree()
    print(p.getData())
    allData.append(p.getData())

pickle.dump(allData, open("data.pickle", "wb"))
