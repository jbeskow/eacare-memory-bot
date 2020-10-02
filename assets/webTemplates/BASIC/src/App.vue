<template>
  <div id="app">
    <h1>Farmi IP</h1>
    <div>
      <p>current ip: {{ currentIp }} <button v-on:click="getCurrentIp"> refresh </button></p>
      <label class="label" for="farmi-ip">Ip and port of farmi-system</label>
        <div>
          http://<input id="farmi-ip" type="text" placeholder="123.456.7.89:5000" v-model="farmiIp"/>
          <button v-on:click="testIp"> Ping </button>
        </div>
    </div>
    <p v-if="status == true">
      ping {{ farmiUrl }} succeded
      <button v-if="status" v-on:click="saveIp"> Use Ip </button>
    </p>
    <p v-else-if="status == false">
      ping {{ farmiUrl }} failed
    </p>
  </div>
</template>

<script>

import FurhatGUI from "furhat-gui"

export default {
  name: 'app',
  data() {
    return {
      farmiIp: "",
      status: null,
      currentIp: ""
    }
  },
  computed: {
    farmiUrl() { return `http://${this.farmiIp}` }
  },
  watch: {
    farmiIp() { this.status = null }
  },
  methods: {
    updateCurrentIp(event) { this.currentIp = event.ip },
    handleIpSuccess() { this.status = true },
    handleIpFail() { this.status = false },
    testIp() {
      this.sendEvent({
        event_name: "FarmiIpTest",
        data: {
          url: this.farmiUrl
        }
      })
    },
    saveIp() {
      this.sendEvent({
        event_name: "FarmiIpSave",
        data: {
          ip: this.farmiIp
        }
      })
      this.farmiIp = ""
      this.getCurrentIp()
    },
    getCurrentIp() {
      this.sendEvent({
        event_name: "RequestFarmiIp",
        data: {}
      })
    }
  },
  beforeCreate() {
    const self = this
    FurhatGUI(furhat => {
      furhat.subscribe('RequestFarmiIpResponse', event => self.updateCurrentIp(event))
      furhat.subscribe('FarmiIpSuccess', event => self.handleIpSuccess(event))
      furhat.subscribe('FarmiIpFail', event => self.handleIpFail(event))
      self.sendEvent = ({ event_name, data}) => furhat.send({ event_name, ...data })
    })
  },
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  margin-top: 60px;
}
</style>
