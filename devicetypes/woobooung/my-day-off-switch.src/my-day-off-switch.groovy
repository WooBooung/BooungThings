/*
 *  My Day-off With Google Calendar
 *
 *
 *  Copyright 2019 WooBooung <woobooung@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Version history
*/
public static String version() { return "v0.0.6.20191005" }
/*
 *	2019/10/05 >>> v0.0.6.20191005 - Added tag #workday (feat. Naver cafe 럽2유3)
 *	2019/04/26 >>> v0.0.4.20190426 - Added polling interval preference
 */
metadata {
    definition (name: "My Day-off Switch", namespace: "WooBooung", author: "Booung", vid: "generic-switch") {
        capability "Switch"
        capability "Sensor"
        capability "Actuator"

        attribute "dayOffCheck", "string"
        attribute "holidayCheck", "string"
        attribute "tagDayOffCheck", "string"
        attribute "tagWorkDayCheck", "string"
        attribute "nextTagdays", "string"
        
        attribute "lastCheckin", "Date"
        
        command "refresh"
    }
    preferences {
        input name: "pollingInterval", title: "Update interval", type: "enum", options:[1 : "1 Hour", 3 : "3 Hour", 6 : "6 Hour", 12 : "12 Hour"], defaultValue: 3, displayDuringSetup: true
        input type: "paragraph", element: "paragraph", title: "Version", description: "${version()}", displayDuringSetup: false
	}
    
    tiles(scale: 2) {
        multiAttributeTile(name: "holiday", type: "lighting", width: 6, height: 4){
            tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label: 'Day-off', action: "off", icon: "st.Entertainment.entertainment5", backgroundColor: "#ff0000"
                attributeState "off", label: 'Not day-off', action: "on", icon: "st.Home.home11", backgroundColor: "#00a0dc"
            }

            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
                attributeState("default", label: 'Last Checkin ${currentValue}')
            }
        }
        
        valueTile("dayOffCheckLabel", "", decoration: "flat", width: 2, height: 1) {
            state "default", label: 'Dayoff Check'
        }

        valueTile("dayOffCheck", "device.dayOffCheck", decoration: "flat", width: 4, height: 1) {
            state "default", label: '${currentValue}'
        }
        
        valueTile("holidayCheckLabel", "", decoration: "flat", width: 2, height: 1) {
            state "default", label: 'Holiday Check'
        }
        
        valueTile("holidayCheck", "device.holidayCheck", decoration: "flat", width: 4, height: 1) {
            state "default", label: '${currentValue}'
        }
        
        valueTile("tagDayOffCheckLabel", "", decoration: "flat", width: 2, height: 1) {
            state "default", label: 'TagDayOff Check'
        }
        
        valueTile("tagDayOffCheck", "device.tagDayOffCheck", decoration: "flat", width: 4, height: 1) {
            state "default", label: '${currentValue}'
        }
        
        valueTile("tagWorkDayCheckLabel", "", decoration: "flat", width: 2, height: 1) {
            state "default", label: 'TagWorkDay Check'
        }
        
        valueTile("tagWorkDayCheck", "device.tagWorkDayCheck", decoration: "flat", width: 4, height: 1) {
            state "default", label: '${currentValue}'
        }
        
        valueTile("nextTagdaysLabel", "", decoration: "flat", width: 2, height: 1) {
            state "default", label: 'Next #dayoff'
        }
        
        valueTile("nextTagdays", "device.nextTagdays", decoration: "flat", width: 4, height: 1) {
            state "default", label: '${currentValue}'
        }

        valueTile("refresh", "device.refresh", decoration: "flat", width: 1, height: 1) {
            state "default", label: '', action: "refresh", icon: "st.secondary.refresh"
        }
   
        //main(["holiday"])
        //details(["holiday", "dayOffCheck", "holidayCheck", "tagdayCheck", "refresh"])
    }
}

def installed() {
    log.trace "Executing 'installed'"
    off()
    initialize()
}

def updated() {
    log.trace "Executing 'updated'"
    initialize()
}

def updateLastTime(){
	def now = new Date().format("yyyy-MM-dd HH:mm:ss EEE", location.timeZone)
    sendEvent(name: "lastCheckin", value: now, displayed: false)
}

def updateCheckData(String dayOffCheck, String holidayCheck, String tagDayOffCheck, String tagWorkDayCheck){
    log.trace "updated check datas"
    sendEvent(name: "dayOffCheck", value: dayOffCheck)
    sendEvent(name: "holidayCheck", value: holidayCheck)
    sendEvent(name: "tagDayOffCheck", value: tagDayOffCheck)
    sendEvent(name: "tagWorkDayCheck", value: tagWorkDayCheck)
}

def updateNextTagdays(ArrayList nextTagdays) {
	log.trace "updateNextTagdays ${nextTagdays}"
    def resultString = ""
    nextTagdays.each {
    	resultString += it + "\n"
    }

	sendEvent(name: "nextTagdays", value: resultString)
}

private initialize() {
    log.trace "Executing 'initialize'"
    poll()
    startPoll()
}

def poll() {
    log.debug "poll()"
    parent.pullData()
}

def refresh() {
    log.debug "refresh()"
    parent.pullData()
}

def startPoll() {
    unschedule()
    def checkInterval = "0/${settings.pollingInterval}"
    log.debug "startPoll $checkInterval"
    
    schedule("0 0 $checkInterval * * ?", poll) // "the hours 0, 3, 6..."
}

def parse(description) {
}

def on() {
    log.debug "on()"
    sendEvent(name: "switch", value: "on")
}

def off() {
    log.debug "off()"
    sendEvent(name: "switch", value: "off")
}
