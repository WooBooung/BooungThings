/**
 *  Hei Home Contact
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy
 *  of the License at:
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 *
 *  author : woobooung@gmail.com
 */
public static String version() { return "v0.0.2.20200415" }
/*
 *   2020/04/15 >>> v0.0.2.20200415 - Modified Device Watch
 *   2020/04/15 >>> v0.0.1.20200415 - Initialize
 */

import physicalgraph.zigbee.clusters.iaszone.ZoneStatus
import physicalgraph.zigbee.zcl.DataType

metadata {
    definition(name: "Hei Home Contact",namespace: "WooBooung", author: "Booung", ocfDeviceType: "x.com.st.d.sensor.contact", vid:"generic-contact-3") {
        capability "Contact Sensor"
        capability "Battery"
        capability "Configuration"
        capability "Refresh"
        capability "Health Check"
        capability "Sensor"

        fingerprint profileId: "0104", deviceId: "0402", inClusters: "0000, 0001, 0003, 0500", outClusters: "0003", manufacturer: "TUYATEC-ktge2vqt", model: "RH3001", deviceJoinName: "Hei Home Contact"
    }

    preferences {
        input type: "paragraph", element: "paragraph", title: "Version", description: version(), displayDuringSetup: false
    }

    tiles(scale: 2) {
        multiAttributeTile(name: "contact", type: "generic", width: 6, height: 4) {
            tileAttribute("device.contact", key: "PRIMARY_CONTROL") {
                attributeState "open", label: '${name}', icon: "st.contact.contact.open", backgroundColor: "#e86d13"
                attributeState "closed", label: '${name}', icon: "st.contact.contact.closed", backgroundColor: "#00A0DC"
            }
        }

        valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false, width: 2, height: 2) {
            state "battery", label: '${currentValue}% battery', unit: ""
        }

        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", action: "refresh.refresh", icon: "st.secondary.refresh"
        }

        main(["contact"])
        details(["contact", "battery", "refresh"])
    }
}

private getBATTERY_VOLTAGE_VALUE_ATTRIBUTE() { 0x0020 }

def installed() {
    log.debug "installed"
    sendEvent(name: "contact", value: "closed", displayed: false,)
    // These devices don't report regularly so they should only go OFFLINE when Hub is OFFLINE
	sendEvent(name: "DeviceWatch-Enroll", value: JsonOutput.toJson([protocol: "zigbee", scheme:"untracked"]), displayed: false)
}

def updated() {
	log.debug "updated"
    refresh()
    // These devices don't report regularly so they should only go OFFLINE when Hub is OFFLINE
	sendEvent(name: "DeviceWatch-Enroll", value: JsonOutput.toJson([protocol: "zigbee", scheme:"untracked"]), displayed: false) 
}

def parse(String description) {
    log.debug "description: $description"
    Map map = zigbee.getEvent(description)
    if (!map) {
        if (description?.startsWith('zone status')) {
            map = parseZoneStatus(description)
        } else {
            Map descMap = zigbee.parseDescriptionAsMap(description)

            if (descMap?.clusterInt == zigbee.POWER_CONFIGURATION_CLUSTER && descMap.commandInt != 0x07 && descMap.value) {
                log.info "BATT METRICS - attr: ${descMap?.attrInt}, value: ${descMap?.value}, decValue: ${Integer.parseInt(descMap.value, 16)}, currPercent: ${device.currentState("battery")?.value}, device: ${device.getDataValue("manufacturer")} ${device.getDataValue("model")}"
                List<Map> descMaps = collectAttributes(descMap)

                def battMap = descMaps.find { it.attrInt == 0x0020 }

                if (battMap) {
                    map = getBatteryResult(Integer.parseInt(battMap.value, 16))
                }
            } 
        }
    } 

    log.debug "Parse returned $map"
    def result = map ? createEvent(map) : [:]

    if (description?.startsWith('enroll request')) {
        List cmds = zigbee.enrollResponse()
        log.debug "enroll response: ${cmds}"
        result = cmds?.collect { new physicalgraph.device.HubAction(it) }
    }
    return result
}

private List<Map> collectAttributes(Map descMap) {
    List<Map> descMaps = new ArrayList<Map>()

    descMaps.add(descMap)

    if (descMap.additionalAttrs) {
        descMaps.addAll(descMap.additionalAttrs)
    }

    return  descMaps
}

private Map parseZoneStatus(String description) {
    log.debug "parseZoneStatus"
    ZoneStatus zs = zigbee.parseZoneStatus(description)
    return zs.isAlarm1Set() ? getContactResult('open') : getContactResult('closed')
}

private Map getBatteryResult(rawValue) {
    log.debug "Battery rawValue = ${rawValue}"
    def linkText = getLinkText(device)

    def result = [:]

    if (!(rawValue == 0 || rawValue == 255)) {
        result.name = 'battery'
        result.translatable = true
        result.descriptionText = "{{ device.displayName }} battery was {{ value }}%"

        def volts = rawValue // For the batteryMap to work the key needs to be an int
        def batteryMap = [28: 100, 27: 100, 26: 100, 25: 90, 24: 90, 23: 70,
                          22: 70, 21: 50, 20: 50, 19: 30, 18: 30, 17: 15, 16: 1, 15: 0]
        def minVolts = 15
        def maxVolts = 28

        if (volts < minVolts)
        volts = minVolts
        else if (volts > maxVolts)
            volts = maxVolts
        def pct = batteryMap[volts]
        result.value = pct
    }

    return result
}

private Map getContactResult(value) {
    log.debug 'Contact Status'
    def linkText = getLinkText(device)
    def descriptionText = "${linkText} was ${value == 'open' ? 'opened' : 'closed'}"
    return [
        name           : 'contact',
        value          : value,
        descriptionText: descriptionText,
        translatable   : true
    ]
}

/**
 * PING is used by Device-Watch in attempt to reach the Device
 * */
def ping() {
    log.debug "ping()"
    refresh()
}

def refresh() {
    log.debug "Refreshing Battery"
    def refreshCmds = zigbee.readAttribute(zigbee.POWER_CONFIGURATION_CLUSTER, BATTERY_VOLTAGE_VALUE_ATTRIBUTE) + zigbee.enrollResponse()

    return refreshCmds
}

def configure() {
    log.debug "configure()"
    log.debug "Configuring Reporting"

    return zigbee.configureReporting(zigbee.POWER_CONFIGURATION_CLUSTER, BATTERY_VOLTAGE_VALUE_ATTRIBUTE, DataType.UINT8, 30, 21600, 0x01) + refresh() + zigbee.batteryConfig()
}