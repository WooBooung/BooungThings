/**
 *  Awair
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
public static String version() { return "v0.0.5.20190513" }
/*
 *   2019/05/13 >>> v0.0.5.20190513 - Seperated DTH (Need to Update SmartApp and DTH)
 *   2019/05/13 >>> v0.0.4.20190513 - Added Commands (Need to Update SmartApp and DTH)
 *   2019/05/05 >>> v0.0.1.20190505 - Initialize
 */
import groovy.json.*
import java.text.SimpleDateFormat
import groovy.json.JsonSlurper

metadata {
    definition(name: "Awair-Mint", namespace: "WooBooung", author: "Booung", vid: "SmartThings-smartthings-Awair", ocfDeviceType: "x.com.st.d.airqualitysensor") {
        capability "Air Quality Sensor" // Awair Score
        //capability "Carbon Dioxide Measurement" // co : clear, detected
        capability "Dust Sensor" // fineDustLevel : PM 2.5   dustLevel : PM 10
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Tvoc Measurement"
        capability "Illuminance Measurement"
        //capability "Sound Pressure Level"
        capability "Refresh"
        capability "Sensor"

        attribute "tempIndices", "number"
        attribute "humidIndices", "number"
        //attribute "co2Indices", "number"
        attribute "vocIndices", "number"
        attribute "pm25Indices", "number"
        //attribute "pm10Indices", "number"

        attribute "awairUUID", "string"

        command "refresh"
        command "ledLevel"
        command "command2AwairLedSleep"
        command "command2AwairLedOff"
        command "command2AwairLedManual"
        command "command2AwairLedAuto"
        command "command2AwairDisplayTemp"
        command "command2AwairDisplayHumid"
        //command "command2AwairDisplayCo2"
        command "command2AwairDisplayVoc"
        command "command2AwairDisplayPm25"
        command "command2AwairDisplayClock"
        command "command2AwairDisplayScore"
    }

    preferences {
        input type: "paragraph", element: "paragraph", title: "Version", description: version(), displayDuringSetup: false
    }

    simulator {
        // TODO: define status and reply messages here
    }

    tiles {
        multiAttributeTile(name: "airQuality", type: "generic", width: 6, height: 4) {
            tileAttribute("device.airQuality", key: "PRIMARY_CONTROL") {
                attributeState('default', label: '${currentValue}', icon: "https://raw.githubusercontent.com/WooBooung/BooungThings/master/icons/awair_large.png", backgroundColor: "#7EC6EE")
            }

            tileAttribute("device.data_time", key: "SECONDARY_CONTROL") {
                attributeState("default", label: 'Update time : ${currentValue}')
            }
        }

        valueTile("awairUUID_label", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: 'UUID'
        }

        valueTile("awairUUID", "device.awairUUID", width: 2, height: 1, decoration: "flat") {
            state "default", label: '${currentValue}'
        }

        valueTile("temperature_label", "", decoration: "flat") {
            state "default", label: 'Temp'
        }

        valueTile("temperature_value", "device.temperature") {
            state "default", label: '${currentValue}°'
        }

        valueTile("temperature_indices", "device.tempIndices", decoration: "flat") {
            state "0", label: '•', backgroundColor: "#7EC6EE"
            state "1", label: ':', backgroundColor: "#6ECA8F"
            state "2", label: '⋮', backgroundColor: "#E5C757"
            state "3", label: '⁘', backgroundColor: "#E40000"
            state "4", label: '⁙', backgroundColor: "#970203"
        }

        valueTile("humidity_label", "", decoration: "flat") {
            state "default", label: 'Humi'
        }

        valueTile("humidity_value", "device.humidity", decoration: "flat") {
            state "default", label: '${currentValue}%'
        }

        valueTile("humidity_indices", "device.humidIndices", decoration: "flat") {
            state "0", label: '•', backgroundColor: "#7EC6EE"
            state "1", label: ':', backgroundColor: "#6ECA8F"
            state "2", label: '⋮', backgroundColor: "#E5C757"
            state "3", label: '⁘', backgroundColor: "#E40000"
            state "4", label: '⁙', backgroundColor: "#970203"
        }

        valueTile("co2_label", "", decoration: "flat") {
            state "default", label: 'CO2\nppm'
        }

        valueTile("co2_value", "device.carbonDioxide", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

        valueTile("co2_indices", "device.co2Indices", decoration: "flat") {
            state "0", label: '•', backgroundColor: "#7EC6EE"
            state "1", label: ':', backgroundColor: "#6ECA8F"
            state "2", label: '⋮', backgroundColor: "#E5C757"
            state "3", label: '⁘', backgroundColor: "#E40000"
            state "4", label: '⁙', backgroundColor: "#970203"
        }

        valueTile("voc_label", "", decoration: "flat") {
            state "default", label: 'VOC\nppb'
        }

        valueTile("voc_value", "device.tvocLevel", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

        valueTile("voc_indices", "device.vocIndices", decoration: "flat") {
            state "0", label: '•', backgroundColor: "#7EC6EE"
            state "1", label: ':', backgroundColor: "#6ECA8F"
            state "2", label: '⋮', backgroundColor: "#E5C757"
            state "3", label: '⁘', backgroundColor: "#E40000"
            state "4", label: '⁙', backgroundColor: "#970203"
        }

        valueTile("pm25_label", "", decoration: "flat") {
            state "default", label: 'PM2.5\n㎍/㎥'
        }

        valueTile("pm25_value", "device.fineDustLevel", decoration: "flat") {
            state "default", label: '${currentValue}', unit: "㎍/㎥"
        }

        valueTile("pm25_indices", "device.pm25Indices", decoration: "flat") {
            state "0", label: '•', backgroundColor: "#7EC6EE"
            state "1", label: ':', backgroundColor: "#6ECA8F"
            state "2", label: '⋮', backgroundColor: "#E5C757"
            state "3", label: '⁘', backgroundColor: "#E40000"
            state "4", label: '⁙', backgroundColor: "#970203"
        }

        valueTile("pm10_label", "", decoration: "flat") {
            state "default", label: 'PM10\n㎍/㎥'
        }

        valueTile("pm10_value", "device.dustLevel", decoration: "flat") {
            state "default", label: '${currentValue}', unit: "㎍/㎥"
        }

        valueTile("pm10_indices", "device.pm10Indices", decoration: "flat") {
            state "0", label: '•', backgroundColor: "#7EC6EE"
            state "1", label: ':', backgroundColor: "#6ECA8F"
            state "2", label: '⋮', backgroundColor: "#E5C757"
            state "3", label: '⁘', backgroundColor: "#E40000"
            state "4", label: '⁙', backgroundColor: "#970203"
        }

        valueTile("lux_label", "", decoration: "flat") {
            state "default", label: 'LUX'
        }

        valueTile("lux_value", "device.illuminance", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

        valueTile("lux_indices", "device.luxIndices", decoration: "flat") {
            state "default", label: '', icon: "st.quirky.spotter.quirky-spotter-luminance-light"
        }

        valueTile("spl_label", "", decoration: "flat") {
            state "default", label: 'SPL\ndb'
        }

        valueTile("spl_value", "device.soundPressureLevel", decoration: "flat") {
            state "default", label: '${currentValue}', unit: "db"
        }

        valueTile("spl_indices", "device.splIndices", decoration: "flat") {
            state "default", label: '', icon: "st.quirky.spotter.quirky-spotter-sound-on"
        }

        valueTile("display_label", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: 'Display'
        }

        valueTile("display_value", "device.displayMode", width: 3, height: 1, decoration: "flat") {
            state "default", label: '${currentValue}'
        }

        valueTile("led_label", "", decoration: "flat") {
            state "default", label: 'Led'
        }

        valueTile("led_value", "device.ledMode", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

        valueTile("knocking_label", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: 'Knocking'
        }

        valueTile("knocking_value", "device.knockingMode", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

        standardTile("refresh_air_value", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: "", action: "refresh", icon: "st.secondary.refresh"
        }

        standardTile("led_off", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: "", action: "command2AwairLedOff", icon: "st.secondary.off", backgroundColor: "#d6c6c9"
        }

        standardTile("led_auto", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: "auto", action: "command2AwairLedAuto", icon: "st.illuminance.illuminance.light", backgroundColor: "#ff93ac"
        }

        standardTile("led_sleep", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: "sleep", action: "command2AwairLedSleep", icon: "st.illuminance.illuminance.dark", backgroundColor: "#ffc2cd"
        }

        standardTile("led_manual", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: "manual", action: "command2AwairLedManual", icon: "st.illuminance.illuminance.bright", backgroundColor: "#ff93ac"
        }

        controlTile("led_level", "device.ledLevel", "slider", width: 1, height: 1, range: "(1..100)") {
            state "ledLevel", action: "ledLevel", icon: "st.illuminance.illuminance.bright"
        }

        standardTile("mode_score", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: "Score", action: "command2AwairDisplayScore", icon: "st.Outdoor.outdoor19", backgroundColor: "#eecbff"
        }

        standardTile("mode_temp", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: "Temp", action: "command2AwairDisplayTemp", icon: "st.Weather.weather2", backgroundColor: "#feffa3"
        }

        standardTile("mode_humi", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: "Humid", action: "command2AwairDisplayHumid", icon: "st.Weather.weather12", backgroundColor: "#dbdcff"
        }

        standardTile("mode_co2", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: "CO2", action: "command2AwairDisplayCo2", icon: "st.Weather.weather1", backgroundColor: "#FFE17F"
        }

        standardTile("mode_voc", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: "VOC", action: "command2AwairDisplayVoc", icon: "st.Weather.weather3", backgroundColor: "#d4ffea"
        }

        standardTile("mode_pm25", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: "PM25", action: "command2AwairDisplayPm25", icon: "st.Weather.weather13", backgroundColor: "#d7c797"
        }

        standardTile("mode_clock", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: "Clock", action: "command2AwairDisplayClock", icon: "st.Office.office6", backgroundColor: "#ff93ac"
        }

        standardTile("knocking_on", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: "", action: "command2AwairKnockingOn", icon: "st.security.alarm.alarm"
        }

        standardTile("knocking_off", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: "", action: "command2AwairKnockingOff", icon: "st.security.alarm.clear"
        }

        standardTile("knocking_sleep", "", width: 1, height: 1, decoration: "flat") {
            state "default", label: "", action: "command2AwairKnockingSleep", icon: "st.samsung.da.RAC_ic_silence"
        }

        valueTile("color_infos", "", width: 5, height: 1, decoration: "flat") {
            state "default", label: 'Dust Level Table'
        }

        valueTile("pm25_kor_infos", "", decoration: "flat") {
            state "default", label: 'PM2.5'
        }

        valueTile("pm25_kor", "", decoration: "flat") {
            state "default", label: 'KOR'
        }

        valueTile("pm25_kor_1_value", "", decoration: "flat") {
            state "default", label: 'Good\n0~15', backgroundColor: "#7EC6EE"
        }

        valueTile("pm25_kor_2_value", "", decoration: "flat") {
            state "default", label: 'Normal\n16~35', backgroundColor: "#6ECA8F"
        }

        valueTile("pm25_kor_3_value", "", decoration: "flat") {
            state "default", label: 'Bad\n36~75', backgroundColor: "#E5C757"
        }

        valueTile("pm25_kor_4_value", "", decoration: "flat") {
            state "default", label: 'Worst\n76~', backgroundColor: "#E40000"
        }

        valueTile("pm25_who_infos", "", decoration: "flat") {
            state "default", label: 'PM2.5'
        }

        valueTile("pm25_who", "", decoration: "flat") {
            state "default", label: 'WHO'
        }

        valueTile("pm25_who_1_value", "", decoration: "flat") {
            state "default", label: 'Good\n0~15', backgroundColor: "#7EC6EE"
        }

        valueTile("pm25_who_2_value", "", decoration: "flat") {
            state "default", label: 'Normal\n16~25', backgroundColor: "#6ECA8F"
        }

        valueTile("pm25_who_3_value", "", decoration: "flat") {
            state "default", label: 'Bad\n26~50', backgroundColor: "#E5C757"
        }

        valueTile("pm25_who_4_value", "", decoration: "flat") {
            state "default", label: 'Worst\n51~', backgroundColor: "#E40000"
        }

        valueTile("pm10_kor_infos", "", decoration: "flat") {
            state "default", label: 'PM10'
        }

        valueTile("pm10_kor", "", decoration: "flat") {
            state "default", label: 'KOR'
        }

        valueTile("pm10_kor_1_value", "", decoration: "flat") {
            state "default", label: 'Good\n0~30', backgroundColor: "#7EC6EE"
        }

        valueTile("pm10_kor_2_value", "", decoration: "flat") {
            state "default", label: 'Normal\n31~80', backgroundColor: "#6ECA8F"
        }

        valueTile("pm10_kor_3_value", "", decoration: "flat") {
            state "default", label: 'Bad\n81~150', backgroundColor: "#E5C757"
        }

        valueTile("pm10_kor_4_value", "", decoration: "flat") {
            state "default", label: 'Worst\n151~', backgroundColor: "#E40000"
        }

        valueTile("pm10_who_infos", "", decoration: "flat") {
            state "default", label: 'PM10'
        }

        valueTile("pm10_who", "", decoration: "flat") {
            state "default", label: 'WHO'
        }

        valueTile("pm10_who_1_value", "", decoration: "flat") {
            state "default", label: 'Good\n0~30', backgroundColor: "#7EC6EE"
        }

        valueTile("pm10_who_2_value", "", decoration: "flat") {
            state "default", label: 'Normal\n31~50', backgroundColor: "#6ECA8F"
        }

        valueTile("pm10_who_3_value", "", decoration: "flat") {
            state "default", label: 'Bad\n51~100', backgroundColor: "#E5C757"
        }

        valueTile("pm10_who_4_value", "", decoration: "flat") {
            state "default", label: 'Worst\n101~', backgroundColor: "#E40000"
        }

        valueTile("blank_5tile", "",  width: 5, height: 1, decoration: "flat") {
            state "default", label: ''
        }

        main(["airQuality"])
        details(["airQuality",
                 "temperature_indices", "temperature_label", "temperature_value", "humidity_indices", "humidity_label", "humidity_value",
                 "voc_indices", "voc_label", "voc_value", "pm25_indices", "pm25_label", "pm25_value",
                 "lux_indices", "lux_label", "lux_value", "awairUUID_label", "awairUUID",
                 "display_label", "blank_5tile",
                 "mode_score", "mode_temp", "mode_humi", "mode_voc", "mode_pm25", "mode_clock",
                 "led_label", "led_off", "led_sleep", "led_auto", "led_manual", "led_level",
                 "color_infos", "refresh_air_value",
                 "cai_infos", "cai_0_value", "cai_1_value", "cai_2_value", "cai_3_value", "cai_4_value",
                 "pm25_who_infos", "pm25_who", "pm25_who_1_value", "pm25_who_2_value", "pm25_who_3_value", "pm25_who_4_value",
                 "pm25_kor_infos", "pm25_kor", "pm25_kor_1_value", "pm25_kor_2_value", "pm25_kor_3_value", "pm25_kor_4_value",
                 "pm10_who_infos", "pm10_who", "pm10_who_1_value", "pm10_who_2_value", "pm10_who_3_value", "pm10_who_4_value",
                 "pm10_kor_infos", "pm10_kor", "pm10_kor_1_value", "pm10_kor_2_value", "pm10_kor_3_value", "pm10_kor_4_value"
        ])
    }
}

// parse events into attributes
def parse(String description) {
    log.debug "Parsing '${description}'"
}

def installed() {
    log.debug "installed()"
    state.ledLevel = 50
    refresh()
}

def uninstalled() {
    log.debug "uninstalled()"
    unschedule()
}

def updated() {
    log.debug "updated()"
    refresh()
}

def refresh() {
    log.debug "refresh()"
    unschedule()

    sendEvent(name: "awairUUID", value: "${device.deviceNetworkId}")

    pullData()

    def airHealthCheckInterval = parent.getRefreshIntervalTime()

    log.debug "airHealthCheckInterval $airHealthCheckInterval"
    schedule("0 0/$airHealthCheckInterval * * * ?", pullData)
}

def command2AwairLedSleep() {
    command2awair("ledSleep")
}

def command2AwairLedAuto() {
    command2awair("ledAuto")
}

def command2AwairLedOff() {
    command2awair("ledOff")
}

def command2AwairLedManual() {
    command2awair("ledManual")
}

def command2AwairDisplayScore() {
    command2awair("displayScore")
}

def command2AwairDisplayClock() {
    command2awair("displayClock")
}

def command2AwairDisplayTemp() {
    command2awair("displayTemp")
}

def command2AwairDisplayHumid() {
    command2awair("displayHumid")
}

def command2AwairDisplayCo2() {
    command2awair("displayCo2")
}

def command2AwairDisplayVoc() {
    command2awair("displayVoc")
}

def command2AwairDisplayPm25() {
    command2awair("displayPm25")
}

def command2AwairKnockingOn() {
    command2awair("knockingOn")
}

def command2AwairKnockingOff() {
    command2awair("knockingOff")
}

def command2AwairKnockingSleep() {
    command2awair("knockingSleep")
}

def command2awair(def commandType) {
    log.debug "command2awair() : ${commandType}"
    def endpoint = ""
    def jsonData

    switch (commandType) {
        case "ledSleep":
            endpoint = "led"
            jsonData = new JsonBuilder("mode": "sleep")
            break
        case "ledAuto":
            endpoint = "led"
            jsonData = new JsonBuilder("mode": "auto", "brightness": 20)
            break
        case "ledOff":
            endpoint = "led"
            jsonData = new JsonBuilder("mode": "manual", "brightness": 0)
            break
        case "ledManual":
            endpoint = "led"
            jsonData = new JsonBuilder("mode": "manual", "brightness": state.ledLevel ? state.ledLevel : 20)
            break
        case "displayScore":
            endpoint = "display"
            jsonData = new JsonBuilder("mode": "score")
            break
        case "displayClock":
            endpoint = "display"
            jsonData = new JsonBuilder("mode": "clock")
            break
        case "displayTemp":
            endpoint = "display"
            jsonData = new JsonBuilder("mode": "temp")
            break
        case "displayHumid":
            endpoint = "display"
            jsonData = new JsonBuilder("mode": "humid")
            break
        case "displayCo2":
            endpoint = "display"
            jsonData = new JsonBuilder("mode": "co2")
            break
        case "displayVoc":
            endpoint = "display"
            jsonData = new JsonBuilder("mode": "voc")
            break
        case "displayPm25":
            endpoint = "display"
            jsonData = new JsonBuilder("mode": "pm25")
            break
        case "knockingOn":
            endpoint = "knocking"
            jsonData = new JsonBuilder("mode": "on")
            break
        case "knockingOff":
            endpoint = "knocking"
            jsonData = new JsonBuilder("mode": "off")
            break
        case "knockingSleep":
            endpoint = "knocking"
            jsonData = new JsonBuilder("mode": "sleep")
            break
    }

    parent.command2awair(device.deviceNetworkId, endpoint, jsonData)
}

def ledLevel(level) {
    log.debug "ledLevel() : ${level}"
    state.ledLevel = level
    sendEvent(name: "ledLevel", value: level)
    command2AwairLedManual()
}

def pullData() {
    log.debug "pullData() : ${device.deviceNetworkId}"
    parent.pullAirData(device.deviceNetworkId)
    //parent.pullDisplayMode(device.deviceNetworkId)
    //parent.pullLedMode(device.deviceNetworkId)
    //parent.pullKnockingMode(device.deviceNetworkId)
    //parent.pullPowerStatus(device.deviceNetworkId)
}