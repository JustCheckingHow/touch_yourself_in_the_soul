import requests
import xml.etree.ElementTree as ET


class Parser:
    def __init__(self):
        pass

    def getDataFromId(self, id):
        self.main_address = 'http://cyfrowe.mnw.art.pl/dmuseion/rdf.xml?type=e&id='
        # id = 40041
        self.r = requests.get(self.main_address + str(id))

    def parseXMLTree(self):
        self.tree = ET.fromstring(self.r.text)

        self.data = []

        i = 0
        for e in self.tree[0]:
            tag = e.tag[e.tag.rfind("}")+1:]
            text = e.text
            self.data.append([tag, text])
            i += 1

    def getData(self):
        return self.data

p = Parser()
p.getDataFromId(40041)
p.parseXMLTree()
print(p.getData())
