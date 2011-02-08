'''
Created on May 19, 2010

@author: Sean Bromage

This module provides the utilities to send a command to the SmartHome PowerLinc Modem over a serial connection. 
It can turn the outlet on and off.

Note: This script requires the pySerial module for serial communication
'''

import serial

def main():
    print "This is insteon.py"      

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
        print "SmartHome Turn On(close): Error - COM port not connected"
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
        print "SmartHome Turn On(open): Error - COM port not connected"
    
    

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
        print "SmartHome Turn Off(close): Error - COM port not connected"
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
        print "SmartHome Turn Off(open): Error - COM port not connected"

    
if __name__=="__main__":
    main()

