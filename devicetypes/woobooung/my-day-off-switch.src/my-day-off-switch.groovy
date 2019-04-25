/**
 *  Copyright 2019 WooBooung
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
    definition (name: "My Day-off Switch", namespace: "WooBooung", author: "Booung", vid: "generic-switch") {
        capability "Switch"
        capability "Sensor"
        capability "Actuator"

        attribute "dayOffCheck", "string"
        attribute "holidayCheck", "string"
        attribute "tagdayCheck", "string"
        
        attribute "lastCheckin", "Date"
        
        command "refresh"
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
        
		valueTile("tagdayCheckLabel", "", decoration: "flat", width: 2, height: 1) {
            state "default", label: 'Tagday Check'
        }
        
        valueTile("tagdayCheck", "device.tagdayCheck", decoration: "flat", width: 4, height: 1) {
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
    sendEvent(name: "lastCheckin", value: now)
}

def updateCheckData(String dayOffCheck, String holidayCheck, String tagdayCheck){
    log.trace "updated check datas"
    sendEvent(name: "dayOffCheck", value: dayOffCheck)
    sendEvent(name: "holidayCheck", value: holidayCheck)
    sendEvent(name: "tagdayCheck", value: tagdayCheck)
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
	log.debug "startPoll"
    unschedule()
    schedule("0 0 0/3 * * ?", poll) // "the hours 0, 3, 6..."
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
