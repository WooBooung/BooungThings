/**
 *  Copyright 2020 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */



// @Deprecated don't use this



metadata {
    definition(name: "Bandi Child Switch", namespace: "WooBooung", author: "Booung", vid: "generic-switch") {
        capability "Switch"
        capability "Actuator"
        capability "Sensor"
        capability "Health Check"
    }
}

def installed() {
    // Auto replace to "Child Switch Health" 
    setDeviceType("Child Switch Health")
}

def updated() {
    // Auto replace to "Child Switch Health" 
    setDeviceType("Child Switch Health")
}

def on() {
    // Auto replace to "Child Switch Health" 
    setDeviceType("Child Switch Health")
}

def off() {
    // Auto replace to "Child Switch Health" 
    setDeviceType("Child Switch Health")
}
