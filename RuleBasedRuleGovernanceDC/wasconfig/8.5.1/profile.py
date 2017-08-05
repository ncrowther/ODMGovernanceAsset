''' 
   Licensed Materials - Property of IBM
   5725-B69 5655-Y17 5655-Y31
   Copyright IBM Corp. 1987, 2012. All Rights Reserved.
   
   Note to U.S. Government Users Restricted Rights: 
   Use, duplication or disclosure restricted by GSA ADP Schedule 
   Contract with IBM Corp.
'''
import sys
import os

#-----------------------------------------------------------------------------
# This is to add scripts under jython directory into the system path
#-----------------------------------------------------------------------------
print "Current directory is: %s" % os.getcwd()
sys.path.insert(0, os.getcwd() + '/jython')

# add True and False definition
try:
    True and False
except NameError:
    class bool(type(1)):
        def __init__(self, val = 0):
            if val:
                type(1).__init__(self, 1)
            else:
                type(1).__init__(self, 0)
        def __repr__(self):
            if self:
                return "True"
            else:
                return "False"

        __str__ = __repr__

    __builtin__.bool = bool
    __builtin__.False = bool(0)
    __builtin__.True = bool(1)