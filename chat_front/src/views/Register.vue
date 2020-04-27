<template>
  <b-container>
    <b-row align-h="center" align-v="center">
      <b-col cols="6">
        <b-card border-variant="primary" align="center">
          <template v-slot:header>
            <h4>Register</h4>
          </template>
          <b-card-text>
            <b-form @keyup.enter.prevent="registerUset">
              <b-row>
                <b-col>
                  <label for="username">Username</label>
                  <b-input v-model="user.username" type="text" id="username"></b-input>
                </b-col>
              </b-row>
              <b-row class="mt-4">
                <b-col>
                  <label for="password">Password</label>
                  <b-input v-model="user.password" type="password" id="password"></b-input>
                </b-col>
              </b-row>
              <!-- <b-row class="mt-4">
                <b-col>
                  <label for="password">Confirm password</label>
                  <b-input v-model="user.password" type="password" id="password"></b-input>
                </b-col>
              </b-row> -->
            </b-form>
          </b-card-text>
          <template v-slot:footer>
            <p>
              Want to
              <router-link to="/login">
                login?
                <b-icon icon="people"></b-icon>
              </router-link>
            </p>
            <b-button type="submit" variant="primary" @click.prevent="registerUset">Register</b-button>
          </template>
        </b-card>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      user: {
        username: "",
        password: ""
      }
    };
  },
  methods: {
    registerUset() {
      axios
        .post("users/register", this.user)
        .then(() => {
          this.$router.push("/login");
        })
        .catch(e => {
          console.log(e);
        });
    }
  },
  mounted() {
    var loggedUsername = sessionStorage.getItem('username');
    if (loggedUsername) {
      this.$store.state.user = {}
      this.$store.state.user.username = loggedUsername;
      axios
        .get("messages/" + loggedUsername)
        .then(response => {
          this.$store.state.user.messages = response.data;
          this.$router.push("/");
        })
        .catch(e => {
          console.log(e);
        });
    }
  }
};
</script>

<style>
</style>