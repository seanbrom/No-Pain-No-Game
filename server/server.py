'''
Created on May 19, 2010

@author: Sean Bromage

Python webserver continually runs on the host's machine and receives commands from a web interface.
When a start command is received, it sends a serial command to turn on the Insteon home automation
hardware and kicks off a timer in a separate thread that is concurrent. The timer does a GET request
to the web interface's database to retrieve the amount of time the user has and begins counting down.
If the time expires, it sends a serial command to turn off the Insteon hardware and sends a POST
request to the database with the username and timeleft=0. If the user stops the timer before it expires, 
the server kills the timer thread, sends a serial command to turn off the Insteon hardware, calculates 
the time used and sends a POST request to the database with the username and time they have left.

Usage note: Be sure to enable traffic through the firewall on Port 80 for a webserver when using with a 
            dynamic DNS so incoming requests can be seen
            
            Also, be sure to change the file path for files returned in the do_GET method under the
            'learn' path as it is specific to the machine
'''

import insteon, time, threading
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer

class TimerThread(threading.Thread):
    def __init__(self, start, user):
        self._stop = threading.Event()
        self.start_time = start
        self.user_time = user
        threading.Thread.__init__(self, name="TimerThread")
        
    def run(self):
        self.timer()

    def timer(self):
        #start_time = time.clock()
#TODO find the user time and set user_time to it
        #user_time = 10.0 #this is for temporary testing
        user_time = self.user_time
        print "User time: %i" % user_time
        if(user_time <= 0):
            print "*** User time = 0 ***"
        while time.clock() < (self.start_time + user_time) and not self._stop.isSet():
            time.sleep(1)
        if(time.clock() >= (self.start_time + user_time)):
#TODO inform the system that the user is out of tv time
            x = 0
            while(x < 50):
                if insteon.turn_off():
                    print "Turn Off Counter: %i" % x
                    break
                else:
                    x += 1
            #self.send_response(0)
        else:
            print "thread has stopped"
            
    def stop(self):
        self._stop.set()
    
    def unStop(self):
        self._stop.clear()

class MyHandler(BaseHTTPRequestHandler):    
    
    def __init__(self, *args, **kwargs):
        self.numThreads = len(threading.enumerate())
        print "Init was called and numThreads = %s" % self.numThreads
        self.p = None
        self.cnt = 0
        self.start_time = 0
        BaseHTTPRequestHandler.__init__(self, *args, **kwargs)
    
    def do_POST(self):
        self.cnt = self.cnt + 1
        print self.cnt
        string = self.path
        stripped_string = string.lstrip('/')
        list = stripped_string.partition('/')
        action = list[0]
        print "Action: " + action
        tv_time = (int)(list[2])
        print "TV time: " + (str)(tv_time)
        if(action == 'start'):
            started = 0
            x = 0
            while(x < 50):
                if insteon.turn_on():
                    print "Turn On Counter: %i" % x
                    started = 1
                    break
                else:
                    x += 1
            if(started == 1):
                self.send_response(200, '200')
                self.start_time = time.clock()
                print "Start time: %i" % self.start_time
                for thread in threading.enumerate():
                    if thread.getName() == 'TimerThread':
                        print "Thread already running"
                        return
                    self.p = TimerThread(self.start_time, tv_time) 
                    self.p.start()
            else:
                self.send_response(400, '400')
            
        elif(action == 'stop'):
            foundThread = False
            for thread in threading.enumerate():
                if thread.getName() == 'TimerThread':
                    stopped1 = 0
                    x = 0
                    while(x < 50):
                        if insteon.turn_off():
                            print "Turn Off Counter: %i" % x
                            stopped1 = 1
                            break
                        else:
                            x += 1
                    if(stopped1 == 1):
                        foundThread = True
                        time_used = time.clock() - thread.start_time
                        print "Time used: %i" % time_used
                        #user_time = 10.0 #this is for temporary testing
                        time_left = tv_time - time_used
                        if(time_left < 0):
                            time_left = 0
                        self.send_response(200, (int)(time_left))
                        
                        print "Time left: %i" % time_left
                        thread.stop()
                    else:
                        print "Unable to reach automation hardware"
                        self.send_response(400)
                    
            if foundThread == False:
                x = 0
                while(x < 50):
                    if insteon.turn_off():
                        print "Turn Off Counter: %i" % x
                        break
                    else:
                        x += 1
                print "Thread Not Found, either it previously stopped or has not been started" 
                self.send_response(200, 0)  
        else:
            print "There was a POST path error."
            self.send_response(400)
                
                
    def do_GET(self):
        #used to test Insteon outlet from web browser
        x = 0
        if(self.path == '/start'):
            while(x < 50):
                if insteon.turn_on():
                    print "Turn On Counter: %i" % x
                    break
                else:
                    x += 1
            self.send_response(200)
            return
        
        elif(self.path == '/stop'):
            while(x < 50):
                if insteon.turn_off():
                    print "Turn Off Counter: %i" % x
                    break
                else:
                    x += 1
            self.send_response(200)
            return
        
        #find a file and return its contents
        string = self.path
        stripped_string = string.lstrip('/')
        list = stripped_string.partition('/')
        action = list[0]
        print "Action: " + action
        file_num = (list[2])
        print "File Num: " + (str)(file_num)
        if(action == 'learn'):
            self.send_response(200)
            self.send_header("Content-type", "text/html")
            self.end_headers()
            #!!! CHANGE THE FILE PATH FOR DIFFERENT MACHINES/USERS!!!
            f = open('C:\Users\Sean\workspace\NPNG\health_info'+(str)(file_num)+'.html', 'r')
            info = f.read()
            self.wfile.write(info)
        else:
            self.send_response(400)
            return
        
        
    def do_HEAD(self):
        self.send_response(200)
        self.send_header("Content-type", "text/html")
        self.end_headers()

    
def main():
    try:
        server = HTTPServer(('', 80), MyHandler)
        print 'Started httpserver...'
        print 'Press Ctrl+C to exit'
        server.serve_forever()
        #server.handle_request()
    except KeyboardInterrupt:
        print '^C received, shutting down server'
        server.socket.close()

if __name__=="__main__":
    main()
    
