#/usr/bin/env python

from http.server import BaseHTTPRequestHandler, HTTPServer
import json
import urllib

from aiinterface import TestAiInterface
from dBinterface import DBInterface

import sys
sys.path.append("..")
from UCB_Server_Interface import UCBInterface

interface = UCBInterface()
#interface = TestAiInterface()

class testHTTPServer_RequestHandler(BaseHTTPRequestHandler):

    def do_GET(self):
        print("do_GET")
        self.send_response(200)

        self.send_header('Content-type','application/json')
        self.end_headers()
        
        opts = self.path[2:]
        print(opts)

        if "howMany" in opts:
            jsonIds = []
            howMany = int(opts.split("=")[1])
            
            db = DBInterface()
            for id in interface.onExhibitsRequested(howMany):
                imgId = db.getPhotoId(id)
                if imgId == []:
                    continue
                jsonIds.append({"id": str(id) + "/" + imgId[0]})
            db.close()
            
            jsonMsg = {}
            jsonMsg["exhibitsIds"] = jsonIds
            jsonData = json.dumps(jsonMsg)
            self.wfile.write(bytes(jsonData, "utf8"))
            print("GET after message write")
        elif "options" in opts:
            id = opts.split("=")[1]
            db = DBInterface()
            options = db.getObject(id)
            db.close()
            print(options)
            optionsDict = {"title": options[0], "creator": options[1], "description": options[2], "format": options[3], "date": options[4], "identifier": options[5]}
            jsonData = json.dumps(optionsDict)
            self.wfile.write(bytes(jsonData, "utf8"))
            print("GET after message write")
        elif "suggestion" in opts:
            jsonIds = []
            howMany = int(opts.split("=")[1])
            
            db = DBInterface()
            for id in interface.getSuggestedId(howMany):
                print(id)
                imgId = db.getPhotoId(id)
                print(imgId)
                print(imgId[0])
                jsonIds.append({"id": str(id) + "/" + imgId[0]})
            db.close()
            
            jsonMsg = {}
            jsonMsg["exhibitsIds"] = jsonIds
            jsonData = json.dumps(jsonMsg)
            self.wfile.write(bytes(jsonData, "utf8"))
            print("GET after message write")

        return

    def do_POST(self):
        print("do_POST")
        self.send_response(200)

        self.send_header('Content-type','application/json')
        self.end_headers()

        jsonString = urllib.parse.unquote(str(self.rfile.read(int(self.headers['Content-Length']))))[2:-1]

        ratesJson = json.loads(jsonString)
        rates = ratesJson["rates"]

        categoriesAndRates = {}

        for r in rates:
            try:
                id = r["id"]
                rate = r["rate"]
            except:
                continue

            # Translate id to category
            db = DBInterface()
            style = db.getSubject(id)
            genre = db.getType(id)
            db.close()
            categoriesAndRates[id] = [style, genre, self.rateToNumber(rate)]
            #print(categoriesAndRates[id])
            
        interface.onExhibitsRates(categoriesAndRates)

        return

    def rateToNumber(self, rate):
        if rate == "NONE":
            return 0
        elif rate == "LIKE":
            return 1
        else:
            return 2

def run():
    print('starting server...')
    server_address = ('', 80)
    httpd = HTTPServer(server_address, testHTTPServer_RequestHandler)
    print('running server...')
    httpd.serve_forever()

run()
