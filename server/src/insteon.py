'''
Created on May 19, 2010

@author: Sean Bromage

This module provides the utilities to send a command to the Insteon controller over a serial connection. 
It can turn the outlet on and turn it off.

Note: This script requires the pySerial module for serial communication

temp post address: 131.179.144.206
origional: fieldstream.nesl.ucla.edu
'''
import httplib, urllib, serial

def main():
    print "This is insteon.py"      

def do_GET(user_name):
    try:
        params = urllib.urlencode({'user': user_name})
        conn = httplib.HTTPConnection("131.179.144.206:80")
        url = "/npng/getpt/"+user_name
        conn.request("GET", url, params)
        response = conn.getresponse()
        print response.status, response.reason
        data = response.read()
        print 'GET data= %s' % data
        conn.close()
        return data
    except:
        print "Insteon do_GET error."

def do_POST(user_name, time_remaining):
    try:
        params = urllib.urlencode({'user': user_name, 'remaining': time_remaining})
        headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
        conn = httplib.HTTPConnection("131.179.144.206:80")
        conn.request("POST", "/npng/rpt/", params, headers)
        response = conn.getresponse()
        print response.status, response.reason
        data = response.read()
        print 'POST data= %s' % data
        conn.close()
        return 1
    except:
        print "Insteon do_POST error."

def turn_on():
    on_reply = '\x02'+'b'+'\x11'+'\x11'+'\x11'+'\x05'+'\x11'+'\xff'
    on_reply = on_reply+'\x06'+'\x02'+'\x50'+'\x11'+'\x11'+'\x11'+'\xaa'
    on_reply = on_reply+'\xaa'+'\xaa'+'\x21'+'\x11'+'\xff'
    try:
        ser1 = serial.Serial()
        ser1.port = 'COM3'
        ser1.baudrate = 19200
        ser1.timeout = 1
        ser1.close()
    except:
        print "Insteon Turn On(close): Error - COM port not connected"
    try:
        ser1.open()
        ser1.write('\x02')
        ser1.write('b')
        ser1.write('\x11')
        ser1.write('\x11')
        ser1.write('\x11')
        ser1.write('\x05')
        ser1.write('\x11')
        ser1.write('\xff')   
        if(on_reply == ser1.read(20)):
            ser1.close()
            return 1
        else:
            ser1.close()
            return 0
    except:
        print "Insteon Turn On(open): Error - COM port not connected"
    
    

def turn_off():
    off_reply = '\x02'+'b'+'\x11'+'\x11'+'\x11'+'\x05'+'\x11'+'\x00'
    off_reply = off_reply+'\x06'+'\x02'+'\x50'+'\x11'+'\x11'+'\x11'+'\xaa'
    off_reply = off_reply+'\xaa'+'\xaa'+'\x21'+'\x11'+'\x00'
    try:
        ser2 = serial.Serial()
        ser2.port = 'COM3'
        ser2.baudrate = 19200
        ser2.timeout = 1
        ser2.close()
    except:
        print "Insteon Turn Off(close): Error - COM port not connected"
    try:
        ser2.open()
        ser2.write('\x02')
        ser2.write('b')
        ser2.write('\x11')
        ser2.write('\x11')
        ser2.write('\x11')
        ser2.write('\x05')
        ser2.write('\x11')
        ser2.write('\x00') 
        if(off_reply == ser2.read(20)):
            ser2.close()
            return 1
        else:   
            ser2.close()
            return 0
    except:
        print "Insteon Turn Off(open): Error - COM port not connected"

    
if __name__=="__main__":
    main()

