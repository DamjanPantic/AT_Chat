<template>
  <div class="home">
    <b-row>
      <b-col cols="4">
        <b-nav pills align="center">
          <b-nav-item :active="usersType == 'all'" @click="getAllUsers">All</b-nav-item>
          <b-nav-item :active="usersType == 'active'" @click="getActiveUsers">Active</b-nav-item>
        </b-nav>
        <hr />
        <b-container class="mt-3" style="position:relative; overflow-y:auto; height:69%">
          <b-card
            border-variant="primary"
            class="text-center mt-1"
            v-for="user in usersToShow"
            :key="user.username"
            @click="selectUser(user)"
            :bg-variant="(message.reciver.username == user.username)? 'primary':'none'"
            :text-variant="(message.reciver.username == user.username)? 'white':'black'"
          >
            <b-card-text>
              {{user.username}}
              <span v-if="$store.state.user.username == user.username">(ME)</span>
              <b-icon class="ml-3" icon="chat"></b-icon>
            </b-card-text>
          </b-card>
        </b-container>
      </b-col>
      <b-col>
        <b-row>
          <b-col>
            <b-card
              border-variant="primary"
              class="mt-1"
              style="position:relative; overflow-y:auto; height:32em"
              id="messageBox"
            >
              <div v-for="message in chatMessages" :key="message.date">
                <b-card
                  bg-variant="primary"
                  text-variant="white"
                  class="text-center ml-auto mb-2"
                  style="max-width: 65%"
                  v-if="message.sender.username == $store.state.user.username"
                >
                  <template v-slot:header v-if="message.subject != ''">{{message.subject}}</template>
                  <b-card-text>{{message.content}}</b-card-text>
                </b-card>
                <b-card
                  bg-variant="secondary"
                  text-variant="white"
                  class="text-center mb-2"
                  style="max-width: 65%"
                  v-else
                >
                  <template v-slot:header v-if="message.subject != ''">{{message.subject}}</template>
                  <b-card-text>{{message.content}}</b-card-text>
                </b-card>
              </div>
            </b-card>
          </b-col>
        </b-row>
        <b-row class="mt-5">
          <b-col>
            <b-form-select v-model="message.reciver">
              <template v-slot:first>
                <b-form-select-option value disabled>Reciver..</b-form-select-option>
              </template>
              <b-form-select-option :value="user" v-for="user in usersToShow" :key="user.username">
                {{user.username}}
                <span v-if="$store.state.user.username == user.username">(ME)</span>
              </b-form-select-option>
            </b-form-select>
            <b-form-input
              v-model="message.subject"
              type="text"
              placeholder="Subject.."
              class="mt-2"
            />
            <b-form-textarea
              v-model="message.content"
              placeholder="Message Text.."
              rows="4"
              max-rows="4"
              class="mt-2"
            />
            <b-row class="mt-4" v-if="usersType == 'active'">
              <b-col align="right" class="mr-4">
                <b-button squared @click="sendMessageToAll">Send to All Active</b-button>
                <b-button
                  pill
                  class="ml-2"
                  variant="primary"
                  @click="sendMessageToUser"
                  :disabled="message.reciver == ''"
                >Send</b-button>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
      </b-col>
    </b-row>
    <!-- <HelloWorld msg="Welcome to Your Vue.js App" />-->
  </div>
</template>

<script>
// @ is an alias to /src
// import HelloWorld from "@/components/HelloWorld.vue";

import axios from "axios";

export default {
  name: "Home",
  data() {
    return {
      users: [],
      usersType: "active",
      message: {
        sender: {},
        reciver: {},
        subject: "",
        content: ""
      }
    };
  },
  computed: {
    chatMessages() {
      let usermessages = [];
      var loggedUsername = sessionStorage.getItem("username");

      this.$store.state.user.messages.forEach(message => {
        if (message.reciver) {
          if (this.message.reciver.username === loggedUsername) {
            if (
              message.sender.username === this.message.reciver.username &&
              message.reciver.username === this.message.reciver.username
            ) {
              usermessages.push(message);
            }
          } else if (
            message.sender.username === this.message.reciver.username ||
            message.reciver.username === this.message.reciver.username
          ) {
            if (this.message.reciver.username !== loggedUsername) {
              usermessages.push(message);
            }
          }
        } else{
          if (this.message.reciver.username === message.sender.username) {
            usermessages.push(message);
          }
        }
      });

      return usermessages;
    },
    usersToShow() {
      var usersTemp = [];
      var temp = this.users;
      var loggedUsername = sessionStorage.getItem("username");

      temp.sort(this.compare).forEach(user => {
        if (user.username !== loggedUsername) {
          usersTemp.push(user);
        } else {
          usersTemp.unshift(user);
        }
      });

      return usersTemp;
    }
  },
  methods: {
    getAllUsers() {
      axios
        .get("users/registered")
        .then(response => {
          this.users = response.data;
          this.usersType = "all";
        })
        .catch(e => {
          console.log(e);
        });
    },
    getActiveUsers() {
      axios
        .get("users/loggedIn")
        .then(response => {
          this.users = response.data;
          this.usersType = "active";
        })
        .catch(e => {
          console.log(e);
        });
    },
    selectUser(user) {
      this.message.reciver = user;
      this.$nextTick(() => {
        var container = document.getElementById("messageBox");
        container.scrollTop = container.scrollHeight;
      });
    },
    sendMessageToAll() {
      var loggedUsername = sessionStorage.getItem("username");
      this.message.sender = { username: loggedUsername };

      if (this.message.reciver == "") {
        this.message.reciver = {};
      }
      axios
        .post("messages/all", this.message)
        .then(() => {
          this.message.subject = "";
          this.message.content = "";
          this.message.reciver = "";
        })
        .catch(e => {
          console.log(e);
        });
    },
    sendMessageToUser() {
      var loggedUsername = sessionStorage.getItem("username");
      this.message.sender = { username: loggedUsername };
      axios
        .post("messages/user", this.message)
        .then(() => {
          this.message.subject = "";
          this.message.content = "";
        })
        .catch(e => {
          console.log(e);
        });
    },
    scrolltoBottom() {
      var container = document.getElementById("messageBox");
      container.scrollTop = container.scrollHeight;
    },
    compare(a, b) {
      if (a.username < b.username) {
        return -1;
      }
      if (a.username > b.username) {
        return 1;
      }
      return 0;
    }
  },
  mounted() {
    var loggedUsername = sessionStorage.getItem("username");
    if (!loggedUsername) {
      this.$router.push("/login");
    } else {
      this.$store.state.user = { username: "", messages: [] };
      this.$store.state.user.username = loggedUsername;

      this.message.sender = { username: loggedUsername };

      axios
        .get("messages/" + loggedUsername)
        .then(response => {
          this.$store.state.user.messages = response.data;
        })
        .catch(e => {
          console.log(e);
        });

      axios
        .get("users/loggedIn")
        .then(response => {
          this.users = response.data;
          this.usersType = "active";
        })
        .catch(e => {
          console.log(e);
        });

      var vue = this;

      this.socket = new WebSocket(`ws://localhost:8080/ChatWAR/ws`);

      // this.socket.onopen = function(event) {
      // };

      this.socket.onmessage = function(event) {
        var wsMessages = event.data.split(/:(.+)/);
        var action = wsMessages[0];
        var data = wsMessages[1];
        if (action == vue.usersType) {
          vue.users = JSON.parse(data);
        }
      };
    }
  },
  components: {
    // HelloWorld
  }
};
</script>
