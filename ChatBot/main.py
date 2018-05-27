#Python libraries that we need to import for our bot
import random
from flask import Flask, request
from pymessenger.bot import Bot
import requests
import shutil
import json

app = Flask(__name__)
ACCESS_TOKEN = 'EAAGP6gz9vWABAGUJJXtw2FLT33AM4P03EYxQynhEajvtZCZCSk6bAuZBk1zUG6ikpxQZCxEpz1Xf3WEgzJzJq1OAicFvadN561VmeH31IDK0C4izAUzhZAUVBk3pbuJbsyIKDYZAGPLCzWp6fbnZCKzorupxHWQYGM24TJKDjBPZBwZDZD'
VERIFY_TOKEN = 'tokenForVerification'
bot = Bot(ACCESS_TOKEN)

returnDict = []
urls = []
# number = [1]

#We will receive messages that Facebook sends our bot at this endpoint
@app.route("/", methods=['GET', 'POST'])
def receive_message():
    if request.method == 'GET':
        """Before allowing people to message your bot, Facebook has implemented a verify token
        that confirms all requests that your bot receives came from Facebook."""
        token_sent = request.args.get("hub.verify_token")
        return verify_fb_token(token_sent)
    #if the request was not get, it must be POST and we can just proceed with sending a message back to user
    else:
        # get whatever message a user sent the bot
       output = request.get_json()
       for event in output['entry']:
          messaging = event['messaging']
          # print("messaging:", messaging[])
          for message in messaging:
            if message.get('message'):

                # try:
                recipient_id = message['sender']['id']
                # print("Rid:", recipient_id, message['message']['is_echo'])
                print(message)
                echo = None
                try:
                    echo = message['message']['is_echo']
                except:
                    echo = False
                if recipient_id == "455414088249434" or echo:
                    return 'done'
                print(message['message']['text'])
                p = Parsing(recipient_id)
                p.handleMessage(message['message']['text'])
                # if message.get('message').get('recipient').get('text'):
                # get_message(message['message']['text'])
                # send_message(recipient_id)
                #if user sends us a GIF, photo,video, or any other non-text item
                # if message['message'].get('attachments'):
                #     response_sent_nontext = get_message()
                #     send_message(recipient_id, response_sent_nontext)
                # except:
                    # return "Success"
    return "Message Processed"

def verify_fb_token(token_sent):
    #take token sent by facebook and verify it matches the verify token you sent
    #if they match, allow the request, else return an error
    if token_sent == VERIFY_TOKEN:
        return request.args.get("hub.challenge")
    return 'Invalid verification token'


#chooses a random message to send to the user
class Parsing:
    def __init__(self, recipient_id):
        self.recipient_id = recipient_id
        # self.number = 1
        # global number
        # self.urls = []
        print("konstruktor")
        global returnDict
        global urls
        self.returnDict = returnDict
        self.urls = urls



    def sendImage(self):
        # self.number[0] += 1
        print("sending image", self.urls)
        bot.send_text_message(self.recipient_id, ("#"+str(len(self.returnDict)+1)))
        bot.send_image_url(self.recipient_id,  self.urls[0])
        bot.send_text_message(self.recipient_id, "Zaproponowaliśmy obraz wpisz: tak - jeżeli Ci sie podoba lub: nie - jeśli nie, jeśli nie masz zdania wpisz: brak zdania")


    def handleMessage(self, msg):
        print(self.returnDict)
        print("msg:", msg)
        if msg == "start":
            print("starting")

            self.returnDict = []
            ids = self.requestInitialIds()
            for id in ids:
                self.urls.append(self.urlGenerator(id))
            # self.number[0] = 1
            self.sendImage()


            # bot.send_text_message(self.recipient_id, "#2")
            # bot.send_image_url(self.recipient_id,  self.urls[2])
            # bot.send_text_message(self.recipient_id, "Zaproponowaliśmy obrazy")
        elif msg == "tak":
            self.returnDict.append(1)
            print(self.returnDict)
            self.sendImage()
        elif msg == "nie":
            self.returnDict.append(-1)
            print(self.returnDict)
            self.sendImage()
        elif msg == "brak zdania":
            self.returnDict.append(0)
            print(self.returnDict)
            self.sendImage()
        else:
            print("else")
            bot.send_text_message(self.recipient_id, "Hej, witaj na czacie muzeum! Aby poznać swój gust wpisz: start, jeżeli masz jakieś pytania postaram się udzielić informacji :)")

        # saveImage("40048/rysobd6(p)")
        # sample_responses = ["http://cyfrowe.mnw.art.pl/Content/40048/rysobd6(p).jpg"]
        # return selected item to the user
        # return random.choice(sample_responses)
        # return self.send_attachment(recipient_id, "image", image_path, notification_type)


    def requestInitialIds(self):
        ids = []
        r = requests.get("http://192.168.1.106", params={'howMany': 20})
        j = json.loads(r.text)
        for record in j['exhibitsIds']:
            # print(record['id'])
            ids.append(record['id'])

        return ids

    def urlGenerator(self, id):
        return "http://cyfrowe.mnw.art.pl/Content/"+id+".jpg"

    #uses PyMessenger to send response to user
    def send_message(recipient_id):


        #sends user the text message provided via input response parameter
        # bot.send_text_message(recipient_id, response)

        return "success"

if __name__ == "__main__":
    app.run()
