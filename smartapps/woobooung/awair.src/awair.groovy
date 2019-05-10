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
 public static String version() { return "v0.0.3.20190510" }
/*
 *	2019/05/10 >>> v0.0.3.20190510 - Modified data type of temperature (Integer -> Double)
 *	2019/05/07 >>> v0.0.2.20190507 - Modified data type of AirQualitySensor
 *	2019/05/05 >>> v0.0.1.20190505 - Initialize
 */
definition(
    name: "Awair",
    namespace: "WooBooung",
    author: "Booung",
    description: "Awair for SmartThings",
    category: "Health & Wellness",
	singleInstance: true,
    iconUrl: "https://raw.githubusercontent.com/WooBooung/BooungThings/master/icons/awair_large.png",
    iconX2Url: "https://raw.githubusercontent.com/WooBooung/BooungThings/master/icons/awair_large.png",
    iconX3Url: "https://raw.githubusercontent.com/WooBooung/BooungThings/master/icons/awair_large.png") 

preferences {
	page(name: "mainPage")
    page(name: "installPage")
    page(name: "userInfoPage")
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
    		childDevices?.each {childDevice->
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
        }
        else {
            return dynamicPage(name: "mainPage", title: "", install: true, uninstall: true) {
                section("User infos :") {
                    href "userInfoPage", title: "Email : ${state.userInfos.email}", description: "Show detail user info"
                }

                section("Devices :") {
                    state.devices.each { device->
                        paragraph "name : ${device.name}\ndeviceType: ${device.deviceType}\ndeviceId: ${device.deviceId}\ndeviceUUID: ${device.deviceUUID}\nlocationName: ${device.locationName}"
                    }
                }
                
                section("Available attributes", hideable:true, hidden:true) {
                    /*[{"awair": ["temp", "humid", "co2", "voc", "dust"],
                    "awair-glow": ["temp", "humid", "co2", "voc"],
                    "awair-mint": ["temp", "humid", "voc", "pm25", "pm10"],
                    "awair-omni": ["temp", "humid", "co2", "voc", "pm25", "pm10"],
                    "awair-r2": ["temp", "humid", "co2", "voc", "pm25", "pm10"]}]*/
                
                    paragraph "deviceType : awair-r2\nattributes : [temp, humid, co2, voc, pm25]"
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
            
            section("API Call limit count", hideable:true, hidden:true) {
                state.userInfos?.permissions.each { permission->
                	paragraph "${permission.scope} : ${permission.quota}"
        		}
            }
    }
}

def installPage() {
	log.debug "installPage"
	dynamicPage(name: "installPage", title: "", uninstall: true, install: true) {
            section("Enter the Awair token :") {
       	       input name: "awairToken", type: "text", required: true, title: "Awair Token: ", submitOnChange: true
				href url:"https://developer.getawair.com/console/access-token", title:"Tap below to log in to Awair", description:"Go to get access token, copy and paste above"
            }
    }
}

private addChildAwairDevices() {
	state.devices.each { device->
    
        def existing = getChildDevice(device.deviceUUID)
        if (!existing) {
            def childDevice = addChildDevice("woobooung", "Awair", device.deviceUUID, null, [completedSetup: true, name: device.deviceType, label: device.name])
        } else {
            log.debug "Device already created : ${device.deviceUUID}"
        }
    }
}

private getUserInfo() {
    log.debug "getUserInfo()"
    
	def params = [
        uri: "https://developer-apis.awair.is",
        path: "/v1/users/self",
        headers: ["Content-Type": "text/json", "Authorization": "Bearer ${state.awairToken}"]
    ]

    try {
        httpGet(params) { resp ->
        	//log.debug "${resp.data}"
            state.userInfos = resp.data
        }

        switch (state.userInfos?.tier.toLowerCase()) {
            case "hobbyist" : state.refreshInterval = 5; break;
            default : state.refreshInterval = 1
    	}
    } catch (e) {
        log.error e.getResponse().getData()
    }
}

private getDevices() {
    log.debug "getDevices()"
    
	def params = [
        uri: "https://developer-apis.awair.is",
        path: "/v1/users/self/devices",
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

def getRefreshIntervalTime() {
	log.debug "getRefreshIntervalTime: ${state.refreshInterval}"
	return state.refreshInterval ? state.refreshInterval : 5
}

def pullData(UUID) {
    def awairDeviceType = UUID.split('_')[0]
    def awairDeviceId = UUID.split('_')[1]
    
    log.debug "pullData() awairDeviceType : ${awairDeviceType}   awairDeviceId : ${awairDeviceId}"
    
    def params = [
        uri: "https://developer-apis.awair.is",
        path: "/v1/users/self/devices/${awairDeviceType}/${awairDeviceId}/air-data/latest",
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
    
   	updateChildDeviceData(UUID, responseData[0])
}

    /*
    [{"awair": ["temp", "humid", "co2", "voc", "dust"],
    "awair-glow": ["temp", "humid", "co2", "voc"],
    "awair-mint": ["temp", "humid", "voc", "pm25", "pm10"],
    "awair-omni": ["temp", "humid", "co2", "voc", "pm25", "pm10"],
    "awair-r2": ["temp", "humid", "co2", "voc", "pm25", "pm10"]}]
    
    https://developer.getawair.com/console/data-docs
    
    dot •:⋮⁘⁙
    
    */
private updateChildDeviceData(UUID, airLatestData) {

	def childDevice = getChildDevice(UUID)
    def now = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)

	if (airLatestData) {
        log.debug "updateChildDeviceData airData : ${airLatestData}"

        childDevice?.sendEvent(name: "airQuality", value: airLatestData.score as Integer)

        //log.debug "updateStatus indices : ${airLatestData.indices}"
        //log.debug "updateStatus sensors : ${airLatestData.sensors}"

        airLatestData.sensors.each {
            switch (it.comp) {

                case "temp" : 
                    Double tempDouble = it.value
                	childDevice?.sendEvent(name: "temperature", value: tempDouble.round(1), unit: getTemperatureScale())
                    break
                case "humid" : childDevice?.sendEvent(name: "humidity", value: it.value as Integer, unit: "%"); break
                case "co2" : childDevice?.sendEvent(name: "carbonDioxide", value: it.value as Integer, unit: "ppm"); break;
                case "voc" : childDevice?.sendEvent(name: "tvocLevel", value: it.value as Integer, unit: "ppb"); break;
                case "pm25" : childDevice?.sendEvent(name: "fineDustLevel", value: it.value as Integer, unit: "㎍/㎥"); break;
                case "pm10" : childDevice?.sendEvent(name: "dustLevel", value: it.value as Integer, unit: "㎍/㎥"); break;
                case "dust" : childDevice?.sendEvent(name: "dustLevel", value: it.value as Integer, unit: "㎍/㎥"); break;
                case "lux" : childDevice?.sendEvent(name: "illuminance", value: it.value as Integer); break;
                case "spl_a" : childDevice?.sendEvent(name: "soundPressureLevel", value: it.value as Integer, unit: "db"); break;
            }
        }

        airLatestData.indices.each {
            switch (it.comp) {
                case "temp" : childDevice?.sendEvent(name: "tempIndices", value: Math.abs(it.value as Integer)); break;
                case "humid" : childDevice?.sendEvent(name: "humidIndices", value: Math.abs(it.value as Integer)); break;
                case "co2" : childDevice?.sendEvent(name: "co2Indices", value: Math.abs(it.value as Integer)); break;
                case "voc" : childDevice?.sendEvent(name: "vocIndices", value: Math.abs(it.value as Integer)); break;
                case "pm25" : childDevice?.sendEvent(name: "pm25Indices", value: Math.abs(it.value as Integer)); break;
                case "pm10" : childDevice?.sendEvent(name: "pm10Indices", value: Math.abs(it.value as Integer)); break;
                case "dust" : childDevice?.sendEvent(name: "pm10Indices", value: Math.abs(it.value as Integer)); break;
                //case "lux" : childDevice?.sendEvent(name: "luxIndices", value: Math.abs(it.value as Integer)); break;
                //case "spl_a" : childDevice?.sendEvent(name: "splIndices", value: Math.abs(it.value as Integer)); break;
            }
        }

        childDevice?.sendEvent(name:"data_time", value: now)
    } else {
    	childDevice?.sendEvent(name:"data_time", value: "$now\n!!! Error - Data is empty !!!")
    }
}