#/usr/bin/env python

from http.server import BaseHTTPRequestHandler, HTTPServer
import json

from aiinterface import TestAiInterface
from dBinterface import DBInterface

interface = TestAiInterface()

class testHTTPServer_RequestHandler(BaseHTTPRequestHandler):
    
    def do_GET(self):
        self.send_response(200)

        print("do_GET")
        
        self.send_header('Content-type','application/json')
        self.end_headers()

        jsonIds = []
        howMany = 20
        for id in interface.onExhibitsRequested(howMany):
            jsonIds.append({"id": id})
		
        print("GET before message write")
        jsonMsg = {}
        jsonMsg["exhibitsIds"] = jsonIds
        jsonData = json.dumps(jsonMsg)
        self.wfile.write(bytes(jsonData, "utf8"))
        print("GET after message write")
        
        return

    def do_POST(self):
        self.send_response(200)

        print("do_POST")
        
        self.send_header('Content-type','application/json')
        self.end_headers()
        
        # Make object connector
        db = DBInterface()
        
        jsonString = self.rfile.read(int(self.headers['Content-Length']))
        
        print("POST: " + str(jsonString))
        
        ratesJson = json.loads(jsonString)
        rates = ratesJson["exhibitsRates"]
        
        print("POST done json object")
        
        categoriesAndRates = {}
        
        for r in rates:
            id = r["id"]
            rate = r["rate"]
            
            # Translate id to category
            style = db.getSubject(id)
            genre = db.getType(id)
            categoriesAndRates[id] = [style, type, rate]
        
        interface.onExhibitsRates(categoriesAndRates)

        """message = "Hello world!"
        #here we can process data and the send it to client

        self.wfile.write(bytes(message, "utf8"))"""
        return

def run():
    print('starting server...')
    server_address = ('', 80)
    httpd = HTTPServer(server_address, testHTTPServer_RequestHandler)
    print('running server...')
    httpd.serve_forever()

run()
