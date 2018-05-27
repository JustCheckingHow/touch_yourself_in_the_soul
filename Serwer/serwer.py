#/usr/bin/env python

from http.server import BaseHTTPRequestHandler, HTTPServer
import json
import urllib

from aiinterface import TestAiInterface
from dBinterface import DBInterface

interface = TestAiInterface()

class testHTTPServer_RequestHandler(BaseHTTPRequestHandler):

    def do_GET(self):
        print("do_GET")
        self.send_response(200)

        self.send_header('Content-type','application/json')
        self.end_headers()

        db = DBInterface()

        opts = self.path[2:]
        print(opts)

        if "howMany" in opts:
            jsonIds = []
            howMany = int(opts.split("=")[1])
            for id in interface.onExhibitsRequested(howMany):
                imgId = db.getPhotoId(id)
                jsonIds.append({"id": str(id) + "/" + imgId[0]})

            jsonMsg = {}
            jsonMsg["exhibitsIds"] = jsonIds
            jsonData = json.dumps(jsonMsg)
            self.wfile.write(bytes(jsonData, "utf8"))
            print("GET after message write")
        elif "options" in opts:
            id = opts.split("=")[1]
            options = db.getObject(id)
            optionsDict = {"title": options[0], "creator": options[1], "format": options[2], "date": options[3], "identifier": options[4]}
            jsonData = json.dumps(optionsDict)
            self.wfile.write(bytes(jsonData, "utf8"))
            print("GET after message write")
        elif "suggestion" in opts:
            # TODO get proposition now
            idDict = {}
            idDict["id"] = 1234 # Get suggested exhibit id
            jsonData = json.dumps(idDict)
            self.wfile.write(bytes(jsonData, "utf8"))
            print("GET after message write")

        return

    def do_POST(self):
        print("do_POST")
        self.send_response(200)

        self.send_header('Content-type','application/json')
        self.end_headers()

        # Make object connector
        db = DBInterface()

        jsonString = urllib.parse.unquote(str(self.rfile.read(int(self.headers['Content-Length']))))[2:-1]

        print(type(jsonString))
        print("POST: " + jsonString)

        ratesJson = json.loads(jsonString)
        rates = ratesJson["rates"]
        print(rates)
        print("POST done json object")

        categoriesAndRates = {}

        for r in rates:
            id = r["id"]
            rate = r["rate"]

            # Translate id to category
            style = db.getSubject(id)
            genre = db.getType(id)
            categoriesAndRates[id] = [style, genre, self.rateToNumber(rate)]

        interface.onExhibitsRates(categoriesAndRates)

        """message = "Hello world!"
        #here we can process data and the send it to client

        self.wfile.write(bytes(message, "utf8"))"""
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
