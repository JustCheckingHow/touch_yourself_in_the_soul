#Python libraries that we need to import for our bot
import random
from flask import Flask, request
from pymessenger.bot import Bot
import requests
import shutil

app = Flask(__name__)
ACCESS_TOKEN = 'EAAGP6gz9vWABAGUJJXtw2FLT33AM4P03EYxQynhEajvtZCZCSk6bAuZBk1zUG6ikpxQZCxEpz1Xf3WEgzJzJq1OAicFvadN561VmeH31IDK0C4izAUzhZAUVBk3pbuJbsyIKDYZAGPLCzWp6fbnZCKzorupxHWQYGM24TJKDjBPZBwZDZD'
VERIFY_TOKEN = 'tokenForVerification'
bot = Bot(ACCESS_TOKEN)

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
          for message in messaging:
            if message.get('message'):

                #Facebook Messenger ID for user so we know where to send response back to
                recipient_id = message['sender']['id']
                if recipient_id == "455414088249434":
                    return 'done'
                print(message)
                if message['message'].get('text'):
                    response_sent_text = get_message()
                    send_message(recipient_id, response_sent_text)
                #if user sends us a GIF, photo,video, or any other non-text item
                if message['message'].get('attachments'):
                    response_sent_nontext = get_message()
                    send_message(recipient_id, response_sent_nontext)
    return "Message Processed"


def verify_fb_token(token_sent):
    #take token sent by facebook and verify it matches the verify token you sent
    #if they match, allow the request, else return an error
    if token_sent == VERIFY_TOKEN:
        return request.args.get("hub.challenge")
    return 'Invalid verification token'


#chooses a random message to send to the user
def get_message():
    pass
    # saveImage("40048/rysobd6(p)")
    # sample_responses = ["http://cyfrowe.mnw.art.pl/Content/40048/rysobd6(p).jpg"]
    # return selected item to the user
    # return random.choice(sample_responses)
    # return self.send_attachment(recipient_id, "image", image_path, notification_type)


def requestInitialIds():
    r = requests.get("http://127.0.0.1?howMany=20")
    print(r.text)




#uses PyMessenger to send response to user
def send_message(recipient_id, response):
    #sends user the text message provided via input response parameter
    # bot.send_text_message(recipient_id, response)
    bot.send_text_message(recipient_id, "123")
    bot.send_image_url(recipient_id,  "http://cyfrowe.mnw.art.pl/Content/40048/rysobd6(p).jpg")
    return "success"

if __name__ == "__main__":
    app.run()
