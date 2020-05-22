/**
 *  Awair
 *
 *  Copyright 2019 Booung
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
public static String version() { return "v0.0.14.20200522" }
/*
 *	2020/05/22 >>> v0.0.14 - Explicit displayed flag
 *  2020/04/23 >>> v0.0.13 - Support Awair-element
 *  2019/12/24 >>> v0.0.12 - Explicit pausable, and Fixed error in GetUserInfos
 *  2019/06/12 >>> v0.0.11 - Prevent duplication post command
 *  2019/06/06 >>> v0.0.10 - Added API Call Count in Awair SmartApp Page
 *  2019/06/06 >>> v0.0.9 - Added Awair-Omni
 *  2019/05/22 >>> v0.0.8 - Get current status Display Led Knocking
 *  2019/05/21 >>> v0.0.7 - Added co2homekitNotice for Homekit by shin4299 (Need to Update SmartApp and DTH)
 *  2019/05/15 >>> v0.0.6 - Changed Dust Sensor to Fine Dust Sensor(Only for Awair-R1)
 *  2019/05/13 >>> v0.0.5 - Seperated DTH (Need to Update SmartApp and DTH)
 *  2019/05/13 >>> v0.0.4 - Added Commands (Need to Update SmartApp and DTH)
 *	2019/05/10 >>> v0.0.3 - Modified data type of temperature (Integer -> Double)
 *	2019/05/07 >>> v0.0.2 - Modified data type of AirQualitySensor
 *	2019/05/05 >>> v0.0.1 - Initialize
 */

import groovy.json.*
    import groovy.json.JsonSlurper

definition(
    name: "Awair",
    namespace: "WooBooung",
    author: "Booung",
    description: "Awair for SmartThings",
    category: "Health & Wellness",
    singleInstance: true,
    pausable: false,
    iconUrl: "https://raw.githubusercontent.com/WooBooung/BooungThings/master/icons/awair_large.png",
    iconX2Url: "https://raw.githubusercontent.com/WooBooung/BooungThings/master/icons/awair_large.png",
    iconX3Url: "https://raw.githubusercontent.com/WooBooung/BooungThings/master/icons/awair_large.png")

preferences {
    page(name: "mainPage")
    page(name: "installPage")
    page(name: "userInfoPage")
    page(name: "apiCallInfoPage")
}

def installed() {
    log.debug "Installed with settings"

    initialize()
}

def uninstalled() {
    log.debug "uninstalled delete all child devices"

    def childDevices = getChildDevices()
    childDevices?.each {
        deleteChildDevice(it.deviceNetworkId)
    }
}

def updated() {
    log.debug "Updated()"

    initialize()
}

def initialize() {
    log.debug "initialize"
    state.awairToken = settings.awairToken

    if (state.awairToken) {
        getUserInfo()
        if (state.userInfos?.email) {
            getDevices()
            addChildAwairDevices()
            def childDevices = getChildDevices()
            childDevices?.each { childDevice ->
                childDevice.updated()
            }
        }
    }
}

// mainPage
def mainPage() {
    if (!state.awairToken) {
        return installPage()
    } else {
        log.debug "mainPage"

        getUserInfo()
        getDevices()

        if (!state.userInfos?.email) {
            return installPage()
        } else {
            return dynamicPage(name: "mainPage", title: "", install: true, uninstall: true) {
                section("User infos :") {
                    href "userInfoPage", title: "Email : ${state.userInfos.email}", description: "Show detail user info"
                }

                section("Devices :") {
                    state.devices.each { device ->
                        //paragraph "deviceName : ${device.name}\ndeviceType: ${device.deviceType}\ndeviceId: ${device.deviceId}\ndeviceUUID: ${device.deviceUUID}\nlocationName: ${device.locationName}"
                        paragraph "name : ${device.name}\ndeviceUUID: ${device.deviceUUID}"
                    }

                    href "apiCallInfoPage", title: "API Call Info", description: "Show detail API Call info"
                }

                section("Available attributes", hideable: true, hidden: true) {
                    /*[{"awair": ["temp", "humid", "co2", "voc", "dust"],
                    "awair-glow": ["temp", "humid", "co2", "voc"],
                    "awair-mint": ["temp", "humid", "voc", "pm25", "pm10"],
                    "awair-omni": ["temp", "humid", "co2", "voc", "pm25", "pm10"],
                    "awair-r2": ["temp", "humid", "co2", "voc", "pm25", "pm10"]}],
                    "awair-element": ["temp", "humid", "co2", "voc", "pm25", "pm10"]}]*/

					paragraph "deviceType : awair-r2\nattributes : [temp, humid, co2, voc, pm25]"
                    paragraph "deviceType : awair-element\nattributes : [temp, humid, co2, voc, pm25]"
                    paragraph "deviceType : awair\nattributes : [temp, humid, co2, voc, dust]"
                    paragraph "deviceType : awair-mint\nattributes : [temp, humid, voc, pm25, lux]"
                    paragraph "deviceType : awair-omni\nattributes : [temp, humid, co2, voc, pm25, lux, spl_a]"
                    paragraph "deviceType : awair-glow\nattributes : [temp, humid, co2, voc]"
                }

                section("Version :") {
                    paragraph "${version()}"
                }
            }
        }
    }
}

def userInfoPage() {
    log.debug "userInfoPage"
    dynamicPage(name: "userInfoPage", title: "", uninstall: false, install: false) {
        section("Awair Token") {
            href "installPage", title: "Replace awair token", description: "Tap here, change token"
        }

        section("Detail User Infos :") {
            paragraph "Email : ${state.userInfos.email}"
            paragraph "FirstName : ${state.userInfos.firstName}"
            paragraph "LastName : ${state.userInfos.lastName}"
            paragraph "Tier : ${state.userInfos.tier}"
            paragraph "Polling Interval : ${getRefreshIntervalTime()} Min"
        }
    }
}

def apiCallInfoPage() {
    log.debug "apiCallInfoPage"
    dynamicPage(name: "userInfoPage", title: "", uninstall: false, install: false) {
        section("API Call Limit Info", hideable: true, hidden: true) {
            state.userInfos?.permissions.each { permission ->
                paragraph "${permission.scope} : ${permission.quota}"
            }
        }

        state.devices.each { device ->
            section("API USAGES - ${device.name}") {
                def usagesData = getApiUsages(device.deviceUUID)
                usagesData?.usages.each { usage ->
                    paragraph "${usage.scope} : ${usage.usage}"
                }
            }
        }
    }
}

def installPage() {
    log.debug "installPage"
    dynamicPage(name: "installPage", title: "", uninstall: true, install: true) {
        section("Enter the Awair token :") {
            input name: "awairToken", type: "text", required: true, title: "Awair Token: ", submitOnChange: true
            href url: "https://developer.getawair.com/console/access-token", title: "Tap below to log in to Awair", description: "Go to get access token, copy and paste above"
        }
    }
}

private addChildAwairDevices() {
    state.devices.each { device ->

        def existing = getChildDevice(device.deviceUUID)
        if (!existing) {
            def awairDthTypeName = "Awair"
            switch (device.deviceType) {
                case "awair-r2" : awairDthTypeName = "Awair-R2"; break;
                case "awair-element" : awairDthTypeName = "Awair-R2"; break;
                case "awair-mint" : awairDthTypeName = "Awair-Mint"; break;
                case "awair" : awairDthTypeName = "Awair-R1"; break;
            }

            def childDevice = addChildDevice("woobooung", awairDthTypeName, device.deviceUUID, null, [completedSetup: true, name: device.deviceType, label: device.name])
        } else {
            log.debug "Device already created : ${device.deviceUUID}"
        }
    }
}

private getUserInfo() {
    log.debug "getUserInfo()"

    def params = [
        uri    : "https://developer-apis.awair.is",
        path   : "/v1/users/self",
        headers: ["Content-Type": "text/json", "Authorization": "Bearer ${state.awairToken}"]
    ]

    try {
        httpGet(params) { resp ->
            //log.debug "${resp.data}"
            state.userInfos = resp.data

            switch (state.userInfos?.tier.toLowerCase()) {
                case "hobbyist": state.refreshInterval = 5; break;
                default: state.refreshInterval = 1
            }
        }
    } catch (e) {
        log.error e.getResponse().getData()
    }
}

private getDevices() {
    log.debug "getDevices()"

    def params = [
        uri    : "https://developer-apis.awair.is",
        path   : "/v1/users/self/devices",
        headers: ["Content-Type": "text/json", "Authorization": "Bearer ${state.awairToken}"]
    ]

    try {
        httpGet(params) { resp ->
            //log.debug "${resp.data}"
            state.devices = resp.data.devices
        }
    } catch (e) {
        log.error e.getResponse().getData()
    }
}

private getApiUsages(UUID) {
    log.debug "getApiUsages()"
    def awairDeviceType = UUID.split('_')[0]
    def awairDeviceId = UUID.split('_')[1]

    def params = [
        uri    : "https://developer-apis.awair.is",
        path   : "/v1/users/self/devices/${awairDeviceType}/${awairDeviceId}/api-usages",
        headers: ["Content-Type": "text/json", "Authorization": "Bearer ${state.awairToken}"]
    ]

    def usageData
    try {
        httpGet(params) { resp ->
            //log.debug "${resp.data}"
            usageData = resp.data
        }
    } catch (e) {
        log.error e.getResponse().getData()
    }

    return usageData
}

def getRefreshIntervalTime() {
    log.debug "getRefreshIntervalTime: ${state.refreshInterval}"
    return state.refreshInterval ? state.refreshInterval : 5
}

def command2awair(UUID, endpoint, commandData) {
    def childDevice = getChildDevice(UUID)

    def awairDeviceType = UUID.split('_')[0]
    def awairDeviceId = UUID.split('_')[1]

    def jsonBody = commandData.toString()
    def mapData = new JsonSlurper().parseText(jsonBody)
    if (mapData) {
        def isNeedPost = true
        def mode = mapData.mode

        switch (endpoint) {
            case "display":
            if (childDevice?.currentState("displayMode").value == mode) {
                isNeedPost = false
            }
            break
            case "led":
            if (mode.toUpperCase() == "MANUAL") {
                def ledLevel = mapData.brightness as Integer
                def currentLedLevel = childDevice?.currentState("ledLevel").value
                def currentMode = childDevice?.currentState("ledMode").value
                if ((currentMode == "MANUAL") && ("${ledLevel}" == "0") && ("${currentLedLevel}" == "0")) {
                    isNeedPost = false
                }
            } else {
                if (childDevice?.currentState("ledMode").value == mode.toUpperCase()) {
                    isNeedPost = false
                }
            }
            break
            case "knocking":
            if (childDevice?.currentState("knockingMode").value == mode.toUpperCase()) {
                isNeedPost = false
            }
            break
        }

        if (!isNeedPost) {
            log.trace "[Skip] command2awair ${endpoint}: Need not PUT request - ${mapData}"
            return
        }
    }

    log.debug "${UUID} Http Put endpoint: ${endpoint} body: ${jsonBody}"
    log.debug "https://developer-apis.awair.is/v1/devices/${awairDeviceType}/${awairDeviceId}/${endpoint}"


    def params = [
        uri    : "https://developer-apis.awair.is/v1/devices/${awairDeviceType}/${awairDeviceId}/${endpoint}",
        headers: ["Authorization": "Bearer ${state.awairToken}", "Content-Type": "application/json"],
        body   : jsonBody
    ]

    try {
        httpPut(params) { resp ->
            log.debug "command2awair>> resp: ${resp.data}"

            switch (endpoint) {
                case "display":
                updateChildDeviceDisplayMode(UUID, mapData)
                break
                case "led":
                updateChildDeviceLedMode(UUID, mapData)
                break
                case "knocking":
                updateChildDeviceKnockingMode(UUID, mapData)
                break
            }
        }
    } catch (groovyx.net.http.HttpResponseException e) {
        log.error "command2awair>> HTTP Post Error : ${e}"
    }
}

def pullAirData(UUID) {
    def awairDeviceType = UUID.split('_')[0]
    def awairDeviceId = UUID.split('_')[1]

    log.debug "pullData() awairDeviceType : ${awairDeviceType}   awairDeviceId : ${awairDeviceId}"

    def params = [
        uri    : "https://developer-apis.awair.is",
        path   : "/v1/users/self/devices/${awairDeviceType}/${awairDeviceId}/air-data/latest",
        headers: ["Content-Type": "text/json", "Authorization": "Bearer ${state.awairToken}"]
    ]

    def responseData = null
    try {
        httpGet(params) { resp ->
            //log.debug "pullData() ${resp.data}"
            responseData = resp.data.data
        }
    } catch (e) {
        log.error e.getResponse().getData()
    }

    updateChildDeviceAirData(UUID, responseData[0])
}

/*
[{"awair": ["temp", "humid", "co2", "voc", "dust"],
"awair-glow": ["temp", "humid", "co2", "voc"],
"awair-mint": ["temp", "humid", "voc", "pm25", "pm10"],
"awair-omni": ["temp", "humid", "co2", "voc", "pm25", "pm10"],
"awair-r2": ["temp", "humid", "co2", "voc", "pm25", "pm10"]}],
"awair-element": ["temp", "humid", "co2", "voc", "pm25", "pm10"]}]

https://developer.getawair.com/console/data-docs

dot •:⋮⁘⁙

*/

private updateChildDeviceAirData(UUID, airLatestData) {
    def childDevice = getChildDevice(UUID)
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)

    if (airLatestData) {
        log.debug "updateChildDeviceData airData : ${airLatestData}"

        childDevice?.sendEvent(name: "airQuality", value: airLatestData.score as Integer, displayed: true)

        //log.debug "updateStatus indices : ${airLatestData.indices}"
        //log.debug "updateStatus sensors : ${airLatestData.sensors}"

        airLatestData.sensors.each {
            switch (it.comp) {
                case "temp":
                Double tempDouble = it.value
                childDevice?.sendEvent(name: "temperature", value: tempDouble.round(1), unit: getTemperatureScale(), displayed: true)
                break
                case "humid": childDevice?.sendEvent(name: "humidity", value: it.value as Integer, unit: "%", displayed: true); break
                case "co2": 
                def co2ppm = it.value as Integer
                childDevice?.co2homekitNotice(co2ppm)
                childDevice?.sendEvent(name: "carbonDioxide", value: co2ppm, unit: "ppm", displayed: true); break;
                case "voc": childDevice?.sendEvent(name: "tvocLevel", value: it.value as Integer, unit: "ppb", displayed: true); break;
                case "pm25": childDevice?.sendEvent(name: "fineDustLevel", value: it.value as Integer, unit: "㎍/㎥", displayed: true); break;
                case "pm10": childDevice?.sendEvent(name: "dustLevel", value: it.value as Integer, unit: "㎍/㎥", displayed: true); break;
                case "dust": childDevice?.sendEvent(name: "fineDustLevel", value: it.value as Integer, unit: "㎍/㎥", displayed: true); break;
                case "lux": childDevice?.sendEvent(name: "illuminance", value: it.value as Integer, displayed: true); break;
                case "spl_a": childDevice?.sendEvent(name: "soundPressureLevel", value: it.value as Integer, unit: "db", displayed: true); break;
            }
        }

        airLatestData.indices.each {
            switch (it.comp) {
                case "temp": childDevice?.sendEvent(name: "tempIndices", value: Math.abs(it.value as Integer)); break;
                case "humid": childDevice?.sendEvent(name: "humidIndices", value: Math.abs(it.value as Integer)); break;
                case "co2": childDevice?.sendEvent(name: "co2Indices", value: Math.abs(it.value as Integer)); break;
                case "voc": childDevice?.sendEvent(name: "vocIndices", value: Math.abs(it.value as Integer)); break;
                case "pm25": childDevice?.sendEvent(name: "pm25Indices", value: Math.abs(it.value as Integer)); break;
                case "pm10": childDevice?.sendEvent(name: "pm10Indices", value: Math.abs(it.value as Integer)); break;
                case "dust": childDevice?.sendEvent(name: "pm25Indices", value: Math.abs(it.value as Integer)); break;
                //case "lux" : childDevice?.sendEvent(name: "luxIndices", value: Math.abs(it.value as Integer)); break;
                //case "spl_a" : childDevice?.sendEvent(name: "splIndices", value: Math.abs(it.value as Integer)); break;
            }
        }

        childDevice?.sendEvent(name: "data_time", value: now, displayed: false)
    } else {
        childDevice?.sendEvent(name: "data_time", value: "$now\n!!! Error - Data is empty !!!", displayed: false)
    }
}

def pullDisplayMode(UUID) {
    def responseData = getResponseData(UUID, "display")
    updateChildDeviceDisplayMode(UUID, responseData)
}

private updateChildDeviceDisplayMode(UUID, responseData) {
    def childDevice = getChildDevice(UUID)

    if (responseData) {
        log.debug "updateChildDeviceDisplayMode : ${responseData}"
        childDevice?.sendEvent(name: "displayMode", value: responseData.mode)
    }
}

def pullLedMode(UUID) {
    def responseData = getResponseData(UUID, "led")
    updateChildDeviceLedMode(UUID, responseData)
}

private updateChildDeviceLedMode(UUID, responseData) {
    def childDevice = getChildDevice(UUID)

    if (responseData) {
        log.debug "updateChildDeviceLedMode : ${responseData}"
        childDevice?.sendEvent(name: "ledMode", value: responseData.mode.toUpperCase())
        if (responseData.mode.toUpperCase() == "MANUAL") {
            childDevice?.sendEvent(name: "ledLevel", value: responseData.brightness as Integer)
        }
    }
}

def pullKnockingMode(UUID) {
    def responseData = getResponseData(UUID, "knocking")
    updateChildDeviceKnockingMode(UUID, responseData)
}

private updateChildDeviceKnockingMode(UUID, responseData) {
    def childDevice = getChildDevice(UUID)

    if (responseData) {
        log.debug "updateChildDeviceKnockingMode : ${responseData}"
        childDevice?.sendEvent(name: "knockingMode", value: responseData.mode.toUpperCase())
    }
}

def pullPowerStatus(UUID) {
    def responseData = getResponseData(UUID, "power-status")

    updateChildDevicePowerData(UUID, responseData)
}

private updateChildDevicePowerData(UUID, responseData) {
    def childDevice = getChildDevice(UUID)

    if (responseData) {
        log.debug "updateChildDevicePowerData : ${responseData}"

        childDevice?.sendEvent(name: "powerSource", value: responseData.plugged ? "dc" : "battery", displayed: true)
        childDevice?.sendEvent(name: "battery", value: responseData.plugged ? 100 : responseData.percentage as Integer, unit: "%", displayed: true)
    }
}

private getResponseData(UUID, endpoint) {
    def awairDeviceType = UUID.split('_')[0]
    def awairDeviceId = UUID.split('_')[1]

    log.debug "getResponseData() awairDeviceType : ${awairDeviceType}   awairDeviceId : ${awairDeviceId} endpoint: ${endpoint}"

    def params = [
        uri    : "https://developer-apis.awair.is",
        path   : "/v1/devices/${awairDeviceType}/${awairDeviceId}/${endpoint}",
        headers: ["Content-Type": "text/json", "Authorization": "Bearer ${state.awairToken}"]
    ]

    def responseData = null
    try {
        httpGet(params) { resp ->
            log.debug "${resp.data}"
            responseData = resp.data
        }
    } catch (e) {
        log.error e
    }

    return responseData
}
