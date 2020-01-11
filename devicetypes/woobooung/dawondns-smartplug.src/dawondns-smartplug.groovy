
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

import physicalgraph.zigbee.zcl.DataType

metadata {
    definition (name: "DawonDNS SmartPlug", namespace: "WooBooung", author: "Booung", ocfDeviceType: "oic.d.smartplug", mnmn: "DawonDNS", vid: "generic-switch-power-energy") {
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Energy Meter"
        capability "Power Meter"
        capability "Sensor"
        capability "Switch"
        capability "Health Check"

        fingerprint endpointId: "0x01", profileId: "0104", deviceId: "0051", inClusters: "0000, 0002, 0003, 0004, 0006, 0019, 0702, 0B04, 0008, 0009", outClusters: "0000, 0002, 0003, 0004, 0006, 0019, 0702, 0B04, 0008, 0009", manufacturer: "DAWON_DNS", model: "PM-B530-ZB", deviceJoinName: "DAWON SmartPlug 16A" 
        fingerprint endpointId: "0x01", profileId: "0104", deviceId: "0051", inClusters: "0000, 0004, 0003, 0006, 0019, 0702, 0B04", outClusters: "0000, 0004, 0003, 0006, 0019, 0702, 0B04", manufacturer: "DAWON_DNS", model: "PM-B430-ZB", deviceJoinName: "DAWON SmartPlug 10A" 
        fingerprint endpointId: "0x01", profileId: "0104", deviceId: "0051", inClusters: "0000, 0002, 0003, 0004, 0006, 0019, 0702, 0B04, 0008, 0009", outClusters: "0000, 0002, 0003, 0004, 0006, 0019, 0702, 0B04, 0008, 0009", manufacturer: "DAWON_DNS", model: "PM-C140-ZB", deviceJoinName: "DAWON Embeded Plug" 
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
        valueTile("energy", "device.energy", decoration: "flat", width: 2, height: 2) {
            state "default", label:'${currentValue} kWh'
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:'refresh', action:"refresh.refresh", icon:"st.secondary.refresh"
        }

        /*
        standardTile("reset", "device.energy", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
                state "default", label:'reset kWh', action:"reset"
        } 
        */

        main(["switch"])
        details(["switch","power","energy","refresh"])
    }
}

def getATTRIBUTE_READING_INFO_SET() { 0x0000 }
def getATTRIBUTE_HISTORICAL_CONSUMPTION() { 0x0400 }

// Parse incoming device messages to generate events
def parse(String description) {
    log.debug "description is $description"
    def event = zigbee.getEvent(description)
    def powerDiv = 1
    def energyDiv = 1000

    if (event) {
        log.info "event enter:$event"
        if (event.name== "power") {
            event.value = event.value/powerDiv
            event.unit = "W"
        } else if (event.name== "energy") {
            event.value = event.value/energyDiv
            event.unit = "kWh"
        }
        log.info "event outer:$event"
        sendEvent(event)
    } else {
        List result = []
        def descMap = zigbee.parseDescriptionAsMap(description)
        log.debug "Desc Map: $descMap"

        List attrData = [[clusterInt: descMap.clusterInt ,attrInt: descMap.attrInt, value: descMap.value]]
        descMap.additionalAttrs.each {
            attrData << [clusterInt: descMap.clusterInt, attrInt: it.attrInt, value: it.value]
        }

        attrData.each {
            def map = [:]
            if (it.value && it.clusterInt == zigbee.SIMPLE_METERING_CLUSTER && it.attrInt == ATTRIBUTE_HISTORICAL_CONSUMPTION) {
                log.debug "power"
                map.name = "power"
                map.value = zigbee.convertHexToInt(it.value)/powerDiv
                map.unit = "W"
            }
            else if (it.value && it.clusterInt == zigbee.SIMPLE_METERING_CLUSTER && it.attrInt == ATTRIBUTE_READING_INFO_SET) {
                log.debug "energy"
                map.name = "energy"
                map.value = zigbee.convertHexToInt(it.value)/energyDiv
                map.unit = "kWh"
            }

            if (map) {
                result << createEvent(map)
            }
            log.debug "Parse returned $map"
        }
        return result
    }
}

def off() {
    sendEvent(name: "power", value: 0, unit: "W")
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

// Reference - https://community.smartthings.com/t/zigbee-something-commands-reference/110615/5
def refresh() {
    log.debug "refresh"
    def cmds = zigbee.onOffRefresh() + zigbee.electricMeasurementPowerRefresh() + zigbee.simpleMeteringPowerRefresh()
    cmds + 
        zigbee.onOffConfig() +
        zigbee.simpleMeteringPowerConfig(1, 600, 0x01) +
        zigbee.electricMeasurementPowerConfig(1, 600, 0x0001) 
}

def configure() {
    // this device will send instantaneous demand and current summation delivered every 1 minute
    sendEvent(name: "checkInterval", value: 2 * 60 + 10 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
    log.debug "Configuring Reporting"
    return refresh()
}

def updated() {
    log.debug "in updated()"
    // updated() doesn't have it's return value processed as hub commands, so we have to send them explicitly
    device.updateDataValue("divisor", "1")
    def cmds = configure()
    cmds.each{ sendHubCommand(new physicalgraph.device.HubAction(it)) }
}

def ping() {
    return refresh()
}