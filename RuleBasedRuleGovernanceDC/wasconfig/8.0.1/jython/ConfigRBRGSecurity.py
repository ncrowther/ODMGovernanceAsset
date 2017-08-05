''' 
   Licensed Materials - Property of IBM
   5725-B69 5655-Y17 5655-Y31
   Copyright IBM Corp. 1987, 2012. All Rights Reserved.
   
   Note to U.S. Government Users Restricted Rights: 
   Use, duplication or disclosure restricted by GSA ADP Schedule 
   Contract with IBM Corp.
'''

import com.ibm.rules.sampleserver
import re
import sys

global AdminTask
global AdminApp

def addUser(userId, password):
    try:
        AdminTask.getUser(["-uniqueName", "uid=" + userId + ",o=defaultWIMFileBasedRealm"])
    except:
        AdminTask.createUser(["-uid", userId, "-password",password ,"-cn", userId, "-sn", userId])
# endDef


def addGroup(groupId, description):
    try:
        AdminTask.getGroup(["-uniqueName", "cn="+ groupId +",o=defaultWIMFileBasedRealm"])
    except:
        AdminTask.createGroup(["-cn", groupId, "-description", description])
    
#endDef

def addMemberToGroup(userId,groupId):
    AdminTask.addMemberToGroup(["-memberUniqueName", "uid="+ userId +",o=defaultWIMFileBasedRealm",
                                "-groupUniqueName", "cn=" + groupId +",o=defaultWIMFileBasedRealm"])
#endDef    

def addUsersAndGroups():
    
    addUser("admUser", "admUser")
    addUser("autL1User", "autL1User")
    addUser("autL2User","autL2User")
    addUser("revUser", "revUser")
    addUser("tesUser", "tesUser")
    addUser("depUser", "depUser")
    addUser("rroUser", "rroUser")
    addUser("gladys", "gladys")
    addUser("ned", "ned")
    addUser("simon", "simon")
    addUser("frank", "frank")
    addUser("bert", "bert")
    
    addGroup("MiniLoanRules","MiniLoanRules")
    addGroup("rbrgRules","rbrgRules")
    addGroup("authorL1","authorL1")
    addGroup("authorL2","authorL2")    
    addGroup("reviewer","reviewer")
    addGroup("deployer","deployer")
    addGroup("tester","tester")    
    addGroup("administrator","administrator")
    addGroup("ruleReadOnly","ruleReadOnly")
    

    addMemberToGroup("admUser", "rtsUser")
    addMemberToGroup("admUser", "rtsConfigManager") 
    addMemberToGroup("admUser", "MiniLoanRules")
    addMemberToGroup("admUser", "rbrgRules")
    addMemberToGroup("admUser", "administrator")
    
    addMemberToGroup("autL1User", "rtsUser")
    addMemberToGroup("autL1User", "MiniLoanRules")
    addMemberToGroup("autL1User", "authorL1")   
    
    addMemberToGroup("autL2User","rtsUser")
    addMemberToGroup("autL2User", "MiniLoanRules")
    addMemberToGroup("autL2User", "authorL2")    
    
    addMemberToGroup("revUser", "rtsUser")
    addMemberToGroup("revUser", "MiniLoanRules")
    addMemberToGroup("revUser", "reviewer")  
    
    addMemberToGroup("tesUser", "rtsUser")
    addMemberToGroup("tesUser", "MiniLoanRules")
    addMemberToGroup("tesUser", "tester")  
    
    addMemberToGroup("depUser", "rtsUser")
    addMemberToGroup("depUser", "rtsConfigManager")
    addMemberToGroup("depUser", "MiniLoanRules")
    addMemberToGroup("depUser", "deployer")  
    
    addMemberToGroup("rroUser", "rtsUser")
    addMemberToGroup("rroUser", "MiniLoanRules")
    addMemberToGroup("rroUser", "rbrgRules") 
    addMemberToGroup("rroUser", "ruleReadOnly")  
    
    addMemberToGroup("gladys", "rtsUser")
    addMemberToGroup("gladys", "MiniLoanRules")
    addMemberToGroup("gladys", "rbrgRules")
    addMemberToGroup("gladys", "administrator")      
    addMemberToGroup("ned", "rtsUser")
    addMemberToGroup("ned", "MiniLoanRules")
    addMemberToGroup("ned", "authorL1")
    addMemberToGroup("simon", "rtsUser")
    addMemberToGroup("simon", "MiniLoanRules")
    addMemberToGroup("simon", "reviewer") 
    addMemberToGroup("frank", "rtsUser")
    addMemberToGroup("frank", "MiniLoanRules")
    addMemberToGroup("frank", "tester")
    addMemberToGroup("bert", "rtsUser")
    addMemberToGroup("bert", "MiniLoanRules")
    addMemberToGroup("bert", "authorL2")

#endDef      

def info():
    print "Adding RBRG users and groups."
#endDef

def error():
    print "Not enough arguments."
#endDef

if (len(sys.argv) < 8 ):
    error()
    sys.exit(2)
else:
    info()
    # Configure the RBRG Security
    addUsersAndGroups()
    rolesToUserMap = [
        ["MiniLoanRules", "No", "No", "", "MiniLoanRules"],
        ["rbrgRules", "No", "No", "", "rbrgRules"],
        ["administrator", "No", "No", "", "administrator"],
        ["authorL1", "No", "No", "", "authorL1"],
        ["authorL2", "No", "No", "", "authorL2"],
        ["reviewer", "No", "No", "", "reviewer"],
        ["tester", "No", "No", "", "tester"],
        ["deployer", "No", "No", "", "deployer"],
        ["ruleReadOnly", "No", "No", "", "ruleReadOnly"]
        ]
    attrs = ['-MapRolesToUsers' , rolesToUserMap ]
    AdminApp.edit('teamserver-WAS8', attrs)
    AdminConfig.save()