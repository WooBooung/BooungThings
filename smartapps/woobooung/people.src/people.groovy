/**
 *  Copyright 2020 WooBooung
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
 *
 *  Version history
*/
public static String version() { return "v0.0.2.20210304" }
/*
 *  2021/03/04 >>> v0.0.2 - Not supported occupancy on st app
 *	2020/05/10 >>> v0.0.1 - Init App
 */
definition(
    name: "People",
    namespace: "WooBooung",
    author: "Booung",
    description: "Representative of presence sensor",
    category: "Safety & Security",
    iconUrl: "https://raw.githubusercontent.com/WooBooung/BooungThings/master/icons/family.jpg",
    iconX2Url: "https://raw.githubusercontent.com/WooBooung/BooungThings/master/icons/family.jpg",
    usesThirdPartyAuthentication: false,
    pausable: true
)

preferences {
    page(name: "mainPage", title: "Representative of presence sensor", install: true, uninstall: true)
}

def mainPage() {
    dynamicPage(name: "mainPage") {
        section("Pepple Name") {
            input "peopleName", "text", title: "Enter People name", defaultValue: "Me", required: true, multiple: false
        }

        section("Select devices to send presence events (present/not present)") {
            input "presenceDevices", "capability.presenceSensor", title: "Which Presence Sensor?", required: true, multiple: true
            input "switchDevicesToPresence", "capability.switch", title: "Which Switch?", required: false, multiple: true
        }

       /* section("Select devices to send occupancy events (occupied/unoccupied)") {
            input "occupancyDevices", "capability.occupancySensor", title: "Which Occupancy Sensor?", required: false, multiple: true
            input "switchDevicesToOccupancy", "capability.switch", title: "Which Switch?", required: false,  multiple: true
        } */

        section("About"){
            paragraph "${version()}"
        }
    }
}

private updateLabel() {
    app.updateLabel(settings.peopleName + "'s Presence")
}

def installed() {
    updateLabel()
    eventSubscribes()
    addChildDevice("Member Presence", "people|${location.id}|${peopleName}", theHub?.id, [completedSetup: true, label: peopleName])
}

def updated() {
    updateLabel()
    unsubscribe()
    eventSubscribes()
}

def eventSubscribes() {
    subscribe(presenceDevices, "presence", presenceHandler)
    //subscribe(occupancyDevices, "occupancy", occupancyHandler)
    subscribe(switchDevicesToPresence, "switch", switchToPresenceHandler)
    //subscribe(switchDevicesToOccupancy, "switch", switchToOccupancyHandler)
}

def presenceHandler(evt) {
    getAllChildDevices().each {
        it.sendEvent(name: "presence", value: evt.value, displayed: true)
    }
}

def occupancyHandler(evt) {
    getAllChildDevices().each {
        it.sendEvent(name: "occupancy", value: evt.value, displayed: true)
    }
}

def switchToPresenceHandler(evt) {
    getAllChildDevices().each {
        it.sendEvent(name: "presence", value: "${evt.value == 'on' ? 'present' : 'not present'}", displayed: true)
    }
}

def switchToOccupancyHandler(evt) {
    getAllChildDevices().each {
        it.sendEvent(name: "occupancy", value: "${evt.value == 'on' ? 'occupied' : 'unoccupied'}", displayed: true)
    }
}