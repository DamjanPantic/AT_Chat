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
            v-for="user in users"
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
              class="text-center mt-1"
              style="position:relative; overflow-y:auto; height:32em"
            ></b-card>
          </b-col>
        </b-row>
        <b-row class="mt-5">
          <b-col>
            <b-form-select v-model="message.reciver">
              <template v-slot:first>
                <b-form-select-option value="" disabled>Reciver..</b-form-select-option>
              </template>
              <b-form-select-option :value="user" v-for="user in users" :key="user.username">{{user.username}}</b-form-select-option>
            </b-form-select>
            <b-form-input v-model="message.subject" type="text" placeholder="Subject.." class="mt-2" />
            <b-form-textarea v-model="message.content" placeholder="Message Text.." rows="4" max-rows="4" class="mt-2" />
            <b-row class="mt-4">
              <b-col align="right" class="mr-4">
                <b-button squared @click="sendMessageToAll">Send to All</b-button>
                <b-button pill class="ml-2" variant="primary" @click="sendMessageToUser" :disabled="message.reciver == ''">Send</b-button>
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
        sender: this.$store.state.user,
        reciver: "",
        subject: "",
        content: ""
      },
    };
  },
  methods: {
    getAllUsers() {
      axios
        .get("users/registered")
        .then(response => {
          this.users = response.data;
          this.usersType = "all"
          this.message.reciver = "";
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
          this.usersType = "active"
          this.message.reciver = "";
        })
        .catch(e => {
          console.log(e);
        });
    },
    selectUser(user){
      this.message.reciver = user;
      axios
        .get("messages/" + this.$store.state.user.username)
        .then(response => {
          console.log(response.data);
        })
        .catch(e => {
          console.log(e);
        });
    },
    sendMessageToAll(){
      if (this.message.reciver == "") {
        this.message.reciver = {}
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
    sendMessageToUser(){
      axios
        .post("messages/user", this.message)
        .then(() => {
          this.message.subject = "";
          this.message.content = "";
        })
        .catch(e => {
          console.log(e);
        });
    }
  },
  mounted() {
    if (!this.$store.state.user) {
      this.$router.push("/login");
    }else{
      axios
        .get("users/loggedIn")
        .then(response => {
          this.users = response.data;
          this.usersType = "active"
        })
        .catch(e => {
          console.log(e);
        });
    }
  },
  components: {
    // HelloWorld
  }
};
</script>
