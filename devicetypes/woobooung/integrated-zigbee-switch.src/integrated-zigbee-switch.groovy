/*
 *  Integrated ZigBee Switch
 * 
 *  Copyright 2020 SmartThings
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
public static String version() { return "v0.0.5.20200425" }
/*
 *   2020/04/25 >>> v0.0.5.20200425 - Fixed minor issue - child device lebel
 *   2020/04/25 >>> v0.0.4.20200425 - Fixed minor issue
 *   2020/04/25 >>> v0.0.4.20200425 - Modified Child Device Lebel
 *   2020/04/25 >>> v0.0.4.20200425 - Fixed minor bugs
 *   2020/04/25 >>> v0.0.3.20200425 - Added Goqual Multi Switch & Modified refresh() function
 *   2020/04/25 >>> v0.0.2.20200425 - Added : DAWON DNS ZigBee Multi Switch 1 2 3 gang,  eZex ZigBee Multi Switch 6 gang, old Zigbee OnOff Swtich
 *   2020/04/25 >>> v0.0.1.20200425 - Initialize : Bandi ZigBee Switch, Zemi ZigBee Switch
 */
metadata {
    definition(name: "Integrated ZigBee Switch", namespace: "WooBooung", author: "Booung", ocfDeviceType: "oic.d.switch", vid: "generic-switch") {
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Health Check"
        capability "Switch"

        command "childOn", ["string"]
        command "childOff", ["string"]

        // Bandi ZigBee Multi Switch
        fingerprint endpointId: "01", profileId: "0104", deviceId: "0100", inClusters: "0000, 000A, 0004, 0005, 0006", outClusters: "0019", manufacturer: "_TZ3000_pdevogdj", model: "TS0003", deviceJoinName: "Bandi Zigbee Switch 1"
        fingerprint endpointId: "01", profileId: "0104", deviceId: "0100", inClusters: "0000, 000A, 0004, 0005, 0006", outClusters: "0019", manufacturer: "_TZ3000_tas0zemd", model: "TS0002", deviceJoinName: "Bandi Zigbee Switch 1"
        fingerprint endpointId: "01", profileId: "0104", deviceId: "0100", inClusters: "0000, 000A, 0004, 0005, 0006", outClusters: "0019", manufacturer: "_TZ3000_ddg0cycp", model: "TS0001", deviceJoinName: "Bandi Zigbee Switch"
        fingerprint endpointId: "01", profileId: "0104", deviceId: "0100", inClusters: "0000, 000A, 0004, 0005, 0006", outClusters: "0019", manufacturer: "_TYZB01_pdevogdj", model: "TS0003", deviceJoinName: "Bandi Zigbee Switch 1"
        fingerprint endpointId: "01", profileId: "0104", deviceId: "0100", inClusters: "0000, 000A, 0004, 0005, 0006", outClusters: "0019", manufacturer: "_TYZB01_tas0zemd", model: "TS0002", deviceJoinName: "Bandi Zigbee Switch 1"
        fingerprint endpointId: "01", profileId: "0104", deviceId: "0100", inClusters: "0000, 000A, 0004, 0005, 0006", outClusters: "0019", manufacturer: "_TYZB01_ddg0cycp", model: "TS0001", deviceJoinName: "Bandi Zigbee Switch"

        // DAWON DNS ZigBee Multi Switch
        fingerprint endpointId: "01", profileId: "0104", deviceId: "0103", inClusters: "0000, 0004, 0003, 0006, 0019, 0002, 0009", outClusters: "0000, 0004, 0003, 0006, 0019, 0002, 0009", manufacturer: "DAWON_DNS", model: "PM-S140-ZB", deviceJoinName: "DAWON Zigbee Switch"
        fingerprint endpointId: "01", profileId: "0104", deviceId: "0103", inClusters: "0000, 0004, 0003, 0006, 0019, 0002, 0009", outClusters: "0000, 0004, 0003, 0006, 0019, 0002, 0009", manufacturer: "DAWON_DNS", model: "PM-S240-ZB", deviceJoinName: "DAWON Zigbee Switch 1"
        fingerprint endpointId: "01", profileId: "0104", deviceId: "0103", inClusters: "0000, 0004, 0003, 0006, 0019, 0002, 0009", outClusters: "0000, 0004, 0003, 0006, 0019, 0002, 0009", manufacturer: "DAWON_DNS", model: "PM-S340-ZB", deviceJoinName: "DAWON Zigbee Switch 1"

        // Zemi ZigBee Multi Switch
        fingerprint endpointId: "10", profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006", manufacturer: "Feibit Inc co.", model: "FB56+ZSW1GKJ2.7", deviceJoinName: "Zemi Zigbee Switch"
        fingerprint endpointId: "0B", profileId: "C05E", inClusters: "0000, 0004, 0003, 0006, 0005, 1000, 0008", outClusters: "0019", manufacturer: "FeiBit", model: "FNB56-ZSW02LX2.0", deviceJoinName: "Zemi Zigbee Switch 1"
        fingerprint endpointId: "01", profileId: "C05E", inClusters: "0000, 0004, 0003, 0006, 0005, 1000, 0008", outClusters: "0019", manufacturer: "FeiBit", model: "FNB56-ZSW03LX2.0", deviceJoinName: "Zemi Zigbee Switch 1"

        // eZex ZigBee Multi Switch
        fingerprint endpointId: "01", profileId: "0104", deviceId: "0100", inClusters: "0000 0003 0004 0006", outClusters: "0006, 000A, 0019", manufacturer: "", model: "E220-KR6N0Z1-HA", deviceJoinName: "eZex Zigbee Switch 1"

        // Zigbee OnOff Swtich
        fingerprint endpointId: "0B", profileId: "0104", deviceId: "0100", inClusters: "0000, 0003, 0004, 0005, 0006", outClusters: "0000", manufacturer: "SZ", model: "Lamp_01", deviceJoinName: "Zigbee OnOff Switch"

        // Unclear devices without model, meanufacturer
        fingerprint endpointId: "0x01", profileId: "0104", deviceId: "0100", inClusters: "0006, 0000, 0003", outClusters: "0019", manufacturer: "", model: "", deviceJoinName: "GoQual Switch 1"
    }

    preferences {
        input type: "paragraph", element: "paragraph", title: "Version", description: version(), displayDuringSetup: false
    }

    // simulator metadata
    simulator {
        // status messages
        status "on": "on/off: 1"
        status "off": "on/off: 0"

        // reply messages
        reply "zcl on-off on": "on/off: 1"
        reply "zcl on-off off": "on/off: 0"
    }

    tiles(scale: 2) {
        multiAttributeTile(name: "switch", type: "lighting", width: 6, height: 4, canChangeIcon: true) {
            tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.light.on", backgroundColor: "#00A0DC", nextState: "turningOff"
                attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn"
                attributeState "turningOn", label: '${name}', action: "switch.off", icon: "st.switches.light.on", backgroundColor: "#00A0DC", nextState: "turningOff"
                attributeState "turningOff", label: '${name}', action: "switch.on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn"
            }
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label: "", action: "refresh.refresh", icon: "st.secondary.refresh"
        }
        main "switch"
        details(["switch", "refresh"])
    }
}

def installed() {
    log.debug "installed()"
    if (parent) {
        setDeviceType("Child Switch Health")
    } else {
        def endpointCount = getEndpointCount()
        if (endpointCount == 1) {
            // for 1 gang switch - ST Official local dth
            setDeviceType("ZigBee Switch")
        } else if (endpointCount > 1){
            // for multi switch, cloud device
            createChildDevices()
        }
        updateDataValue("onOff", "catchall")
        refresh()
    }
}

def updated() {
    log.debug "updated()"
    updateDataValue("onOff", "catchall")
    refresh()
}

def parse(String description) {
    Map eventMap = zigbee.getEvent(description)
    Map eventDescMap = zigbee.parseDescriptionAsMap(description)

    if (!eventMap && eventDescMap) {
        eventMap = [:]
        if (eventDescMap?.clusterId == zigbee.ONOFF_CLUSTER) {
            eventMap[name] = "switch"
            eventMap[value] = eventDescMap?.value
        }
    }

    if (eventMap) {
        def endpointId = device.getDataValue("endpointId")
        log.debug "eventMap $eventMap | eventDescMap $eventDescMap"

        if (eventDescMap?.sourceEndpoint == endpointId) {
            log.debug "parse - sendEvent parent $eventDescMap.sourceEndpoint"
            sendEvent(eventMap)
        } else {
            log.debug "parse - sendEvent child  $eventDescMap.sourceEndpoint"
            def childDevice = childDevices.find {
                it.deviceNetworkId == "$device.deviceNetworkId:${eventDescMap.sourceEndpoint}"
            }
            if (childDevice) {
                childDevice.sendEvent(eventMap)
            } else {
                log.debug "Child device: $device.deviceNetworkId:${eventDescMap.sourceEndpoint} was not found"
                def parentEndpointInt = zigbee.convertHexToInt(endpointId)
                def childEndpointInt = zigbee.convertHexToInt(eventDescMap?.sourceEndpoint)
                def childEndpointHexString = zigbee.convertToHexString(childEndpointInt, 2).toUpperCase()
                def deviceLabel = "${device.displayName[0..-2]}"
                createChildDevice("$deviceLabel${childEndpointInt - parentEndpointInt + 1}", childEndpointHexString)
            }
        }
    }
}

private getEndpointCount() {
    def model = device.getDataValue("model")

    switch (model) {
        case 'TS0003' : return 3
        case 'TS0002' : return 2
        case 'TS0001' : return 1
        case 'PM-S340-ZB' : return 3
        case 'PM-S240-ZB' : return 2
        case 'PM-S140-ZB' : return 1
        case 'FNB56-ZSW03LX2.0' : return 3
        case 'FNB56-ZSW02LX2.0' : return 2
        case 'FB56+ZSW1GKJ2.7' : return 1
        case 'E220-KR6N0Z1-HA' : return 6
        case 'Lamp_01' : return 1
        default : return 0
    }
}

private void createChildDevices() {
    log.debug("createChildDevices of $device.deviceNetworkId")
    def endpointCount = getEndpointCount()
    def endpointId = device.getDataValue("endpointId")
    def endpointInt = zigbee.convertHexToInt(endpointId)
    def deviceLabel = "${device.displayName[0..-2]}"

    for (i in 1..endpointCount - 1) {
        def endpointHexString = zigbee.convertToHexString(endpointInt + i, 2).toUpperCase()
        createChildDevice("$deviceLabel${i + 1}", endpointHexString)
    }
}

private void createChildDevice(String deviceLabel, String endpointHexString) {
    def childDevice = childDevices.find {
        it.deviceNetworkId == "$device.deviceNetworkId:$endpointHexString"
    }
    if (!childDevice) {
        log.debug("Need to createChildDevice: $device.deviceNetworkId:$endpointHexString")
        addChildDevice("Integrated ZigBee Switch", "$device.deviceNetworkId:$endpointHexString", device.hubId,
                       [completedSetup: true, label: deviceLabel, isComponent: false])
    } else {
        log.debug("createChildDevice: SKIP - $device.deviceNetworkId:${endpointHexString}")
    }
}

private getChildEndpoint(String dni) {
    dni.split(":")[-1] as String
}

def on() {
    log.debug("on")
    zigbee.on()
}

def off() {
    log.debug("off")
    zigbee.off()
}

def childOn(String dni) {
    log.debug("child on ${dni}")
    def childEndpoint = getChildEndpoint(dni)
    zigbee.command(zigbee.ONOFF_CLUSTER, 0x01, "", [destEndpoint: childEndpoint])
}

def childOff(String dni) {
    log.debug("child off ${dni}")
    def childEndpoint = getChildEndpoint(dni)
    zigbee.command(zigbee.ONOFF_CLUSTER, 0x00, "", [destEndpoint: childEndpoint])
}

/**
 * PING is used by Device-Watch in attempt to reach the Device
 * */
def ping() {
    return refresh()
}

def refresh() {
    def cmds = zigbee.onOffRefresh()
    def endpointCount = getEndpointCount()

    if (endpointCount > 1) {
        def endpointId = device.getDataValue("endpointId")
        def endpointInt = zigbee.convertHexToInt(endpointId)

        for (i in 1..endpointCount - 1) {
            def endpointValue = endpointInt + i
            cmds += zigbee.readAttribute(zigbee.ONOFF_CLUSTER, 0x0000, [destEndpoint: endpointValue])
        }
    } else {
        cmds += zigbee.readAttribute(zigbee.ONOFF_CLUSTER, 0x0000, [destEndpoint: 0xFF])
    }

    return cmds
}

def poll() {
    refresh()
}

def healthPoll() {
    log.debug "healthPoll()"
    def cmds = refresh()
    cmds.each { sendHubCommand(new physicalgraph.device.HubAction(it)) }
}

def configureHealthCheck() {
    Integer hcIntervalMinutes = 12
    if (!state.hasConfiguredHealthCheck) {
        log.debug "Configuring Health Check, Reporting"
        unschedule("healthPoll")
        runEvery5Minutes("healthPoll")
        def healthEvent = [name: "checkInterval", value: hcIntervalMinutes * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID]]
        // Device-Watch allows 2 check-in misses from device
        sendEvent(healthEvent)
        childDevices.each {
            it.sendEvent(healthEvent)
        }
        state.hasConfiguredHealthCheck = true
    }
}

def configure() {
    log.debug "configure()"
    configureHealthCheck()

    //other devices supported by this DTH in the future
    def cmds = zigbee.onOffConfig(0, 120)
    def endpointCount = getEndpointCount()

    if (endpointCount > 1) {
        def endpointId = device.getDataValue("endpointId")
        def endpointInt = zigbee.convertHexToInt(endpointId)

        for (i in 1..endpointCount - 1) {
            def endpointValue = endpointInt + i
            cmds += zigbee.configureReporting(zigbee.ONOFF_CLUSTER, 0x0000, 0x10, 0, 120, null, [destEndpoint: endpointValue])
        }
    } else {
        cmds += zigbee.configureReporting(zigbee.ONOFF_CLUSTER, 0x0000, 0x10, 0, 120, null, [destEndpoint: 0xFF])
    }
    cmds += refresh()
    return cmds
}
