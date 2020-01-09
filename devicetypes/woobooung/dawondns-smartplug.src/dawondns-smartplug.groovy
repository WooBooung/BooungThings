
/**
 *	Copyright 2020 SmartThings
 *
 *	Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *	in compliance with the License. You may obtain a copy of the License at:
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *	on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *	for the specific language governing permissions and limitations under the License.
 *
 */

metadata {
    definition (name: "DawonDNS SmartPlug", namespace: "WooBooung", author: "Booung", ocfDeviceType: "oic.d.smartplug", mnmn: "SmartThings",  vid: "generic-switch-power-energy") {
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Power Meter"
        capability "Sensor"
        capability "Switch"
        capability "Health Check"
        capability "Light"

        fingerprint endpointId: "0x01", profileId: "0104", deviceId: "0051", inClusters: "0000, 0002, 0003, 0004, 0006, 0019, 0702, 0B04, 0008, 0009", outClusters: "0000, 0002, 0003, 0004, 0006, 0019, 0702, 0B04, 0008, 0009", manufacturer: "DAWON_DNS", model: "PM-B530-ZB", deviceJoinName: "DAWON SmartPlug 16A" 
        fingerprint endpointId: "0x01", profileId: "0104", deviceId: "0051", inClusters: "0000, 0004, 0003, 0006, 0019, 0702, 0B04", outClusters: "0000, 0004, 0003, 0006, 0019, 0702, 0B04", manufacturer: "DAWON_DNS", model: "PM-B430-ZB", deviceJoinName: "DAWON SmartPlug 10A" 
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4, canChangeIcon: true){
            tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
                attributeState("on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00a0dc")
                attributeState("off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff")
            }
        }
        valueTile("power", "device.power", decoration: "flat", width: 2, height: 2) {
            state "default", label:'${currentValue} W'
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        main "switch"
        details(["switch", "power"])
    }
}

// Parse incoming device messages to generate events
def parse(String description) {
    log.debug "description is $description"
    def event = zigbee.getEvent(description)
    if (event) {
        if (event.name == "power") {
            def powerValue
            def div = device.getDataValue("divisor")
            div = div ? (div as int) : 10
            powerValue = (event.value as Integer) / div
            sendEvent(name: "power", value: powerValue)
        }
        else {
            sendEvent(event)
        }
    }
    else {
        log.warn "DID NOT PARSE MESSAGE for description : $description"
        log.debug zigbee.parseDescriptionAsMap(description)
    }
}

def off() {
    zigbee.off()
}

def on() {
    zigbee.on()
}

def installed() {
    log.debug "installed()"
    device.updateDataValue("divisor", "1")
    refresh()
}

def refresh() {
    Integer reportIntervalMinutes = 5
    def cmds = zigbee.onOffRefresh() + zigbee.simpleMeteringPowerRefresh() + zigbee.electricMeasurementPowerRefresh()
    cmds + zigbee.onOffConfig(0, reportIntervalMinutes * 60) + zigbee.simpleMeteringPowerConfig() + zigbee.electricMeasurementPowerConfig()
}

def configure() {
    log.debug "in configure()"
    return configureHealthCheck()
}

def configureHealthCheck() {
    Integer hcIntervalMinutes = 12
    sendEvent(name: "checkInterval", value: hcIntervalMinutes * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
    return refresh()
}

def updated() {
    log.debug "in updated()"
    // updated() doesn't have it's return value processed as hub commands, so we have to send them explicitly
    device.updateDataValue("divisor", "1")
    def cmds = configureHealthCheck()
    cmds.each{ sendHubCommand(new physicalgraph.device.HubAction(it)) }
}

def ping() {
    return zigbee.onOffRefresh()
}