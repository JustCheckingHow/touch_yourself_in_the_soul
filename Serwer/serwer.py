#/usr/bin/env python

from http.server import BaseHTTPRequestHandler, HTTPServer
import json

class testHTTPServer_RequestHandler(BaseHTTPRequestHandler):

    def __init__(self, aiInterface, *args):
        BaseHTTPRequestHandler.__init__(self, *args)
        self.aiInterface = aiInterface 
	
    def do_GET(self):
        self.send_response(200)

        self.send_header('Content-type','application/json')
        self.end_headers()

        jsonIds = []
        for id in self.aiInterface.onExhibitsRequested(howMany):
            jsonIds.append({"id": id})
		
        jsonMsg = {}
        jsonMsg["exhibitsIds"] = jsonIds
        jsonData = json.dump(jsonMsg)
        self.wfile.write(bytes(jsonData, "utf8"))
        return

    def do_POST(self):
        self.send_response(200)

        jsonString = self.rfile.read(self.headers.getheader('content-length'))
        ratesJson = json.loads(jsonString)
        rates = ratesJson["exhibitsRates"]
        
        for r in rates:
            id = r["id"]
            rate = r["rate"]
            
            # Translate id to category
            
        
        """self.send_header('Content-type','text/html')
        self.end_headers()

        message = "Hello world!"
        #here we can process data and the send it to client

        self.wfile.write(bytes(message, "utf8"))"""
        return

def run():
    print('starting server...')
    server_address = ('127.0.0.1', 80)
    httpd = HTTPServer(server_address, testHTTPServer_RequestHandler)
    print('running server...')
    httpd.serve_forever()

run()
