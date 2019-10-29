/**
 *  Super Virtual Device
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

/*
 *    2019/10/29 >>> v0.0.2.20191029 - Modified motion
 *    2019/10/29 >>> v0.0.1.20191029 - Initialize Super Virtual Device
 */
metadata {
    definition (name: "Super Virtual Device", namespace: "WooBooung", author: "Booung") {
        capability "Switch"
        capability "Presence Sensor"
        capability "Occupancy Sensor"
        capability "Carbon Monoxide Detector"
        capability "Sound Sensor"
        capability "Object Detection"
        capability "Motion Sensor"
        capability "Contact Sensor"
        capability "Smoke Detector"
        capability "Water Sensor"
        capability "Sensor"
        capability "Health Check"

        attribute "humanqty", "number"

        command "sound_detected"
        command "sound_clear"
        command "human1"
        command "human2"
        command "smoke_detected"
        command "smoke_clear"
        command "co_detected"
        command "co_clear"
        command "wet"
        command "dry"
        command "open"
        command "close"
        command "present"
        command "not_present"
        command "occupied"
        command "unoccupied"
        command "on"
        command "off"
        command "active"
        command "inactive"
    }

    simulator {
    }

    tiles {
        standardTile("switch", "device.switch", canChangeIcon: true, width: 3, height: 2) {
            state("off", label: '${currentValue}', icon: "st.switches.switch.off", backgroundColor: "#ffffff", action: "on")
            state("on", label: '${currentValue}', icon: "st.switches.switch.on", backgroundColor: "#00A0DC", action: "off")
        }

        standardTile("smoke", "device.smoke") {
            state("clear", label:"Clear", icon:"st.alarm.smoke.clear", backgroundColor:"#ffffff", action:"smoke_detected")
            state("detected", label:"Smoke!", icon:"st.alarm.smoke.smoke", backgroundColor:"#e86d13", action:"smoke_clear")
        }

        standardTile("co", "device.carbonMonoxide") {
            state("clear", label:"Clear", icon:"st.alarm.smoke.clear", backgroundColor:"#ffffff", action:"co_detected")
            state("detected", label:"CO!", icon:"st.alarm.smoke.smoke", backgroundColor:"#e86d13", action:"co_clear")
        }

        standardTile("sound", "device.sound") {
            state("not detected", label:"Clear", icon:"st.alarm.alarm.alarm", backgroundColor:"#ffffff", action:"sound_detected")
            state("detected", label:"Sound!", icon:"st.alarm.alarm.alarm", backgroundColor:"#e86d13", action:"sound_clear")
        }

        standardTile("motion", "device.motion", inactiveLabel: false, decoration: "flat") {
            state("inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#CCCCCC", action: "active")
            state("active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#00A0DC", action: "inactive")
        }  

        standardTile("door", "device.contact", inactiveLabel: false, decoration: "flat") {
            state("closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00A0DC", action: "open")
            state("open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#e86d13", action: "close")
        }

        standardTile("water", "device.water", inactiveLabel: false, decoration: "flat") {
            state "dry", icon:"st.alarm.water.dry", backgroundColor:"#ffffff", action: "wet"
            state "wet", icon:"st.alarm.water.wet", backgroundColor:"#00A0DC", action: "dry"
        }

        standardTile("humanqty", "device.humanqty", decoration: "flat") {
            state("default", label:'${currentValue}', icon:"st.Health & Wellness.health12", backgroundColors:[
                [value: 0, color: "#CCCCCC"],
                [value: 1, color: "#00A0DC"],
                [value: 2, color: "#00A0DC"]
            ])
        }

        standardTile("human1", "device.detected", inactiveLabel: false, decoration: "flat") {
            state("default", label:'Human 1', action:"human1")
        }

        standardTile("human2", "device.detected", inactiveLabel: false, decoration: "flat") {
            state("default", label:'Human 2', action:"human2")
        }

        standardTile("presence", "device.presence", width: 1, height: 1, canChangeBackground: true) {
            state("present", labelIcon:"st.presence.tile.mobile-present", backgroundColor:"#00A0DC")
            state("not present", labelIcon:"st.presence.tile.mobile-not-present", backgroundColor:"#ffffff")
        }

        standardTile("present", "device.presence", inactiveLabel: false, decoration: "flat") {
            state("present", label:'present',  backgroundColor:"#00A0DC", action: "present")
        }

        standardTile("not present", "device.presence", inactiveLabel: false, decoration: "flat") {
            state("not present", label:'not present', backgroundColor:"#e86d13", action: "not_present")
        }

        standardTile("occupancy", "device.occupancy", canChangeBackground: true) {
            state("occupied", label:'${currentValue}', icon:"st.presence.tile.present", backgroundColor:"#00A0DC")
            state("unoccupied", label:'${currentValue}', icon:"st.presence.tile.not-present", backgroundColor:"#ffffff")
        }

        standardTile("occupied", "device.occupancy", inactiveLabel: false, decoration: "flat") {
            state("occupied", label:'occupied',  backgroundColor:"#00A0DC", action: "occupied")
        }

        standardTile("unoccupied", "device.occupancy", inactiveLabel: false, decoration: "flat") {
            state("unoccupied", label:'unoccupied', backgroundColor:"#e86d13", action: "unoccupied")
        }
    }
}

def installed() {
    log.trace "Executing 'installed'"
    initialize()
}

def updated() {
    log.trace "Executing 'updated'"
    initialize()
}

private initialize() {
    log.trace "Executing 'initialize'"
    sendEvent(name: "supportedValues", value: "{\"value\": [\"human\"]}", displayed: false)
    sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
    sendEvent(name: "healthStatus", value: "online")
    sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
}

def parse(String description) {
}

def smoke_detected() {
    log.debug "smoke()"
    sendEvent(name: "smoke", value: "detected", descriptionText: "$device.displayName smoke detected!")
}

def smoke_clear() {
    log.debug "smoke_clear()"
    sendEvent(name: "smoke", value: "clear", descriptionText: "$device.displayName clear")
}

def sound_detected() {
    log.debug "sound_detected()"
    sendEvent(name: "sound", value: "detected", descriptionText: "$device.displayName sound detected")
}

def sound_clear() {
    log.debug "not sound_clear()"
    sendEvent(name: "sound", value: "not detected", descriptionText: "$device.displayName sound not detected")
}

def human1() {
    log.debug "human1()"
    active()
    sendEvent(name: "detected", value: "{\"data\": {\"qty\": 1},\"value\": \"human\", \"qty\": 1}", descriptionText: "$device.displayName human 1 detected")
    sendEvent(name: "humanqty", value: 1, displayed: false)
}

def human2() {
    log.debug "human2()"
    active()
    sendEvent(name: "detected", value: "{\"data\": {\"qty\": 2},\"value\": \"human\", \"qty\":  2}", descriptionText: "$device.displayName human 2 detected")
    sendEvent(name: "humanqty", value: 2, displayed: false)
}

def active() {
    log.debug "motion_active()"
    sendEvent(name: "motion", value: "active", descriptionText: "$device.displayName motion active")
}

def inactive() {
    log.debug "motion_inactive()"
    sendEvent(name: "motion", value: "inactive", descriptionText: "$device.displayName motion inactive")
    sendEvent(name: "detected", value: "{\"data\": {\"qty\": 0},\"value\": \"human\", \"qty\":  0}", descriptionText: "$device.displayName human 0 detected")
    sendEvent(name: "humanqty", value: 0, displayed: false)
}

def wet() {
    log.debug "wet()"
    sendEvent(name: "water", value: "wet")
}

def dry() {
    log.debug "dry()"
    sendEvent(name: "water", value: "dry")
}

def open() {
    log.debug "open()"
    sendEvent(name: "contact", value: "open")
}

def close() {
    log.debug "close()"
    sendEvent(name: "contact", value: "closed")
}

def co_detected() {
    log.debug "carbonMonoxide_detected()"
    sendEvent(name: "carbonMonoxide", value: "detected")
}

def co_clear() {
    log.debug "carbonMonoxide_clear()"
    sendEvent(name: "carbonMonoxide", value: "clear")
}

def present() {
    log.debug "present()"
    sendEvent(name: "presence", value: "present")
}

def not_present() {
    log.debug "not_present()"
    sendEvent(name: "presence", value: "not present")
}

def occupied() {
    log.debug "occupied()"
    sendEvent(name: "occupancy", value: "occupied")
}

def unoccupied() {
    log.debug "unoccupied()"
    sendEvent(name: "occupancy", value: "unoccupied")
}

def on() {
    log.debug "switch_on()"
    sendEvent(name: "switch", value: "on")
}

def off() {
    log.debug "switch_off()"
    sendEvent(name: "switch", value: "off")
}