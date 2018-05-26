import requests
import xml.etree.ElementTree as ET
from multiprocessing import Pool

main_address = 'http://cyfrowe.mnw.art.pl/dmuseion/rdf.xml?type=e&id='

# idTab = []
def runner(id):
    beg = 0
    r = requests.get(main_address + str(id+beg))
    tree = ET.fromstring(r.text)
    # print("trying id:", id+beg, end=" ")
    try:
        tree[0][0]
        idTab.append(id)
        print(id)
    except:
        # print("nope")
        pass

p = Pool(5)
# f = Finder()
p.map(runner, range(1000))
# print(f.idTab)
