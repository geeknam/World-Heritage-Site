#!/usr/bin/env python

import urllib
import json
from google.appengine.api import mail
from google.appengine.ext import webapp, db
from google.appengine.ext.webapp import util
from google.appengine.ext.webapp.util import run_wsgi_app


class Feedback(db.Model):
    user     = db.StringProperty()
    heritage = db.StringProperty()
    comment  = db.StringProperty(multiline=True)
    star     = db.IntegerProperty()
    date     = db.DateTimeProperty(auto_now_add=True)

    #Convert feedback object into a dictionary
    def toDict(self):
	    return {'user': self.user,'heritage': self.heritage,'comment': self.comment,'star': self.star,'date': self.date}
	       

class FeedbackGetter(webapp.RequestHandler):
    def get(self,heritage):
	    #Retrieve feedbacks of a particular heritage requested
        heritage = urllib.unquote(heritage)
        feedbacks = db.GqlQuery("SELECT * FROM Feedback WHERE heritage = :1", heritage)
        
        feedDict = {}
        for feedback in feedbacks:
	        feedDict[str(feedback.key())] = feedback.toDict()
	  		
        self.response.out.write(json.encode(feedDict));

class FeedbackStore(webapp.RequestHandler):
    def post(self):
	    #Storing feedback in datastore
        feedback = Feedback()
        feedback.user     = self.request.get('email')
        feedback.heritage = self.request.get('heritage')
        feedback.comment  = self.request.get('comment')
        feedback.star     = int(self.request.get('star'))
        feedback.put()
     
        #Sending confirmation email to the user
        myemail         = mail.EmailMessage()
        myemail.sender  = "World Heritage Site <noreply@androidnam.appspotmail.com>"
        myemail.to      = feedback.user
        myemail.subject = "Feedback on " + feedback.heritage
        myemail.body    = '''
        Thank you for submitting your feedback, \n
        Heritage: %s \n
        Comment: %s \n
        Rating: %s 
        ''' % (feedback.heritage, feedback.comment, feedback.star)
        myemail.send()

        self.redirect('/get/'+feedback.heritage)

class FeedbackForm(webapp.RequestHandler):
	def get(self):
		self.response.out.write("""
		<html>
          <body>
            <form action="/store" method="post">
              Heritage:<div><input name="heritage" /></div>
			  Email:<div><input name="email" /></div>
			  Comment:<div><input name="comment" /></div>
			  Star:<div><input name="star" /></div>
              <div><input type="submit" value="Submit"></div>
            </form>
          </body>
        </html>
		""")


application = webapp.WSGIApplication([(r'/get/(.*)', FeedbackGetter),
						  			  ('/store', FeedbackStore),
									  ('/feedback', FeedbackForm)],
                                      debug=True)


def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
